package uz.java.maniac.thisthingdoesnotexistbot.config;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uz.java.maniac.thisthingdoesnotexistbot.akka.Manager;
import uz.java.maniac.thisthingdoesnotexistbot.akka.command.Command;

@Configuration
public class AkkaConfig {
//    @Bean
//    public Behavior<Command> commandManager(){
//        return Behaviors.setup(context -> new Manager(context, sender));
//    }
//    @Bean
//    public Behavior<String> printActorBehavior(PrintService printService) {
//        return Behaviors.setup(ctx -> new PrintActor(ctx, printService));
//    }
//
//    @Bean
//    public Behavior<GreetActor.Greet> greetActorBehavior(GreetService greetService,
//                                                         Behavior<String> printerActorBehavior) {
//        return Behaviors.setup(ctx -> new GreetActor(ctx, greetService, printerActorBehavior));
//    }
}
