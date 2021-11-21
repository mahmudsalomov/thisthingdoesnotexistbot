package uz.java.maniac.thisthingdoesnotexistbot.akka;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import org.checkerframework.checker.units.qual.A;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.User;
import uz.java.maniac.thisthingdoesnotexistbot.akka.command.Command;
import uz.java.maniac.thisthingdoesnotexistbot.akka.command.CompleteCommand;
import uz.java.maniac.thisthingdoesnotexistbot.akka.command.DoCommand;
import uz.java.maniac.thisthingdoesnotexistbot.akka.command.TaskCommand;
import uz.java.maniac.thisthingdoesnotexistbot.bot.Bot;
import uz.java.maniac.thisthingdoesnotexistbot.model.State;
import uz.java.maniac.thisthingdoesnotexistbot.model.TelegramUser;
import uz.java.maniac.thisthingdoesnotexistbot.model.Urls;
import uz.java.maniac.thisthingdoesnotexistbot.utils.ButtonModel.Col;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.Duration;
import java.util.Collections;
import java.util.Random;

public class Worker extends AbstractBehavior<Command> {

    private WorkerState state;
    private Worker(ActorContext<Command> context) {
        super(context);
        this.state=WorkerState.IDLE;
    }
    public static Behavior<Command> create() {
        return Behaviors.setup(Worker::new);
    }
    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(TaskCommand.class,task-> Behaviors.withTimers(timer->{
                    timer.startSingleTimer(new DoCommand(
                            task.getState(),
                            task.getSender(),
                            task.getBot(),
                            task.getUser()),
                            Duration.ofMillis(10));
                    return Behaviors.same();
                }))
                .onMessage(DoCommand.class,task->{
//                    System.out.println("task "+getContext().getSelf().path());
                    test(task.getUser(), task.getBot());
                    task.getSender().tell(new CompleteCommand(getContext().getSelf(),task.getUser()));
                    return Behaviors.same();
                })
                .build();
    }
    public void test(TelegramUser user, Bot bot) throws IOException {
        Col col=new Col();
        URL url =new URL("https://thispersondoesnotexist.com/image");
        boolean isPhoto=true;
        switch (user.getState()) {
            case PERSON : url = new URL(Urls.PERSON); break;
            case ART : url = new URL(Urls.ART); break;
            case CAT : url = new URL(Urls.CAT); break;

//            case BUTTERFLY:url=new URL(Urls.BUTTERFLY+(new Random().nextInt(30000)+".jpg")); break;
//            case HORSE:url=new URL(Urls.HORSE); break;
//            case IDEA:{
//                isPhoto=false;
//                idea(user,bot,randomIdea());
//            } break;

        }
        URLConnection connection=url.openConnection();
        SendPhoto msg = new SendPhoto();
        InputFile inputFile = new InputFile();
        inputFile.setMedia(connection.getInputStream(), String.valueOf(new Random().nextInt()));
        msg.setChatId(String.valueOf(user.getId()));
        msg.setPhoto(inputFile);
        col.add("New",user.getState().name());
        col.add("Select", State.START.name());
        msg.setReplyMarkup(col.getMarkup());
        bot.executeWithExceptionCheck(msg);

    }
//    public void idea(TelegramUser user, Bot bot,String url){
//        RestTemplate restTemplate=new RestTemplate();
//        String object = restTemplate.getForObject(url, String.class);
//        assert object != null;
//        int i = object.indexOf("<h2>");
//        int i1 = object.indexOf("</h2>");
//        String s = object.substring(i, i1);
//        s=s.replace("<h2>","");
//        SendMessage sendMessage=new SendMessage();
//        sendMessage.setChatId(String.valueOf(user.getId()));
//        sendMessage.setText("*"+s+"*");
//        sendMessage.setParseMode(ParseMode.MARKDOWN);
//        bot.executeWithExceptionCheck(sendMessage);
//
//    }
//    public static void testMessage(String chatId,Bot bot){
//        SendMessage sendMessage=new SendMessage();
//        sendMessage.setChatId(chatId);
//        sendMessage.setText("aaaa");
//        bot.executeWithExceptionCheck(sendMessage);
//    }
//    public static String randomIdea(){
//        int i = new Random().nextInt(2);
//        if (i==1) return Urls.IDEA_COMPANY;
//        else return Urls.IDEA_OPEN_SOURCE;
//    }

    public WorkerState getState() {
        return state;
    }
}
