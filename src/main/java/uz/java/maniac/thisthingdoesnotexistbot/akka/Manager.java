package uz.java.maniac.thisthingdoesnotexistbot.akka;
import akka.actor.AbstractActor;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import org.checkerframework.checker.units.qual.A;
import uz.java.maniac.thisthingdoesnotexistbot.akka.command.Command;
import uz.java.maniac.thisthingdoesnotexistbot.akka.command.CompleteCommand;
import uz.java.maniac.thisthingdoesnotexistbot.akka.command.StartCommand;
import uz.java.maniac.thisthingdoesnotexistbot.akka.command.TaskCommand;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Manager extends AbstractBehavior<Command>{

    private AkkaService sender;
    private Set<ActorRef<Command>> workers=new HashSet<>();
    private Map<ActorRef<Command>,WorkerState> workerStateMap=new HashMap<>();

    public Manager(ActorContext<Command> context, AkkaService sender) {
        super(context);
        this.sender = sender;
    }
    

    public static Behavior<Command> create(AkkaService sender) {
        return Behaviors.setup(context -> new Manager(context, sender));
    }
    
    
    public ActorRef<Command> order(){
        System.out.println("SIZE = "+workerStateMap.size());
//        workerStateMap.keySet().forEach(System.out::println);
        for (ActorRef<Command> worker:workerStateMap.keySet()) {
            if (workerStateMap.get(worker).equals(WorkerState.IDLE)) return worker;
        }
        ActorRef<Command> worker = getContext().spawn(Worker.create(), String.valueOf(workerStateMap.size()));
        workerStateMap.put(worker,WorkerState.IDLE);
        return worker;
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(StartCommand.class, start -> {
                    ActorRef<Command> worker = order();
                    worker.tell(new TaskCommand(start.getState(),getContext().getSelf(),start.getBot(),start.getUser()));
                    workerStateMap.put(worker,WorkerState.ACTIVE);
                    return Behaviors.same();
                })
                .onMessage(CompleteCommand.class,complete->{
                    workerStateMap.put(complete.getWorker(),WorkerState.IDLE);
                    sender.complete(complete.getUser());
                    return Behaviors.same();
                })
                .build();
    }

}
