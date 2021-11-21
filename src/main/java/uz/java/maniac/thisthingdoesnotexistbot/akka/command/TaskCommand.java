package uz.java.maniac.thisthingdoesnotexistbot.akka.command;

import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.java.maniac.thisthingdoesnotexistbot.bot.Bot;
import uz.java.maniac.thisthingdoesnotexistbot.model.State;
import uz.java.maniac.thisthingdoesnotexistbot.model.TelegramUser;

@AllArgsConstructor
@Getter
public class TaskCommand implements Command{
    private State state;
    private ActorRef<Command> sender;
    private Bot bot;
    private TelegramUser user;


}
