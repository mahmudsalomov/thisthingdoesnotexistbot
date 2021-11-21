package uz.java.maniac.thisthingdoesnotexistbot.akka.command;

import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.java.maniac.thisthingdoesnotexistbot.model.TelegramUser;

@AllArgsConstructor
@Getter
public class CompleteCommand implements Command{
    private ActorRef<Command> worker;
    private TelegramUser user;

}
