package uz.java.maniac.thisthingdoesnotexistbot.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.typed.ActorSystem;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import uz.java.maniac.thisthingdoesnotexistbot.akka.command.Command;
import uz.java.maniac.thisthingdoesnotexistbot.akka.command.CompleteCommand;
import uz.java.maniac.thisthingdoesnotexistbot.akka.command.StartCommand;
import uz.java.maniac.thisthingdoesnotexistbot.bot.Bot;
import uz.java.maniac.thisthingdoesnotexistbot.model.State;
import uz.java.maniac.thisthingdoesnotexistbot.model.TelegramUser;
import uz.java.maniac.thisthingdoesnotexistbot.repository.TelegramUserRepository;

@Service
@Component
@Actor
public class AkkaService {

//    @Autowired
//    private final Bot bot;

    private final TelegramUserRepository userRepository;

    private final ActorSystem<Command> actorSystem=ActorSystem.create(Manager.create(this),"Manager");

    public AkkaService(TelegramUserRepository userRepository) {
        this.userRepository = userRepository;
    }

//    public AkkaService(Bot bot) {
//        this.bot = bot;
//    }

    public void get(TelegramUser user,Bot bot){
        if (!user.getState().equals(State.START)){
            actorSystem.tell(new StartCommand(State.START,bot,user));
        }
    }

    public void complete(TelegramUser user){
        user.setState(State.START);
        user.setProgress(false);
        userRepository.save(user);
    }

//    @Override
//    public Receive createReceive() {
//        return receiveBuilder()
//                .match(CompleteCommand.class,command->{
//                    TelegramUser user=command.getUser();
//                    user.setState(State.START);
//                    userRepository.save(user);
//                })
//                .build();
//    }
}
