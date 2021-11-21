package uz.java.maniac.thisthingdoesnotexistbot.bot;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.java.maniac.thisthingdoesnotexistbot.akka.AkkaService;
import uz.java.maniac.thisthingdoesnotexistbot.bot.handler.Handler;
import uz.java.maniac.thisthingdoesnotexistbot.message.MessageTemplate;
import uz.java.maniac.thisthingdoesnotexistbot.model.State;
import uz.java.maniac.thisthingdoesnotexistbot.model.TelegramUser;
import uz.java.maniac.thisthingdoesnotexistbot.repository.TelegramUserRepository;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

@Component
@Service
public class UpdateReceiver {
    private final List<Handler> handlers;
    private final TelegramUserRepository telegramUserRepository;
    private final AkkaService akkaService;
    private final MessageTemplate messageTemplate;
    @Autowired
    public UpdateReceiver(List<Handler> handlers, TelegramUserRepository telegramUserRepository, AkkaService akkaService, MessageTemplate messageTemplate) {
        this.handlers = handlers;
        this.telegramUserRepository = telegramUserRepository;
        this.akkaService = akkaService;
        this.messageTemplate = messageTemplate;
    }

    @Transactional
    public List<PartialBotApiMethod<? extends Serializable>> handle(Update update,Bot bot){
        try {
            List<PartialBotApiMethod<? extends Serializable>> results=new ArrayList<>();

            if (update.hasMessage()){

                final Message message = update.getMessage();
//                System.out.println(message);
                final Long chatId = message.getFrom().getId();
                final TelegramUser user = telegramUserRepository.findById(chatId)
                        .orElseGet(() -> telegramUserRepository.save(new TelegramUser(update.getMessage().getFrom())));

                if (!user.isProgress()) {
                    if (message.hasText() && (
                            Objects.equals(message.getText(), "/person") ||
                                    Objects.equals(message.getText(), "/cat") ||
                                    Objects.equals(message.getText(), "/art")
                    )) {
                        switch (message.getText()) {
                            case "/person" : user.setState(State.PERSON); break;
                            case "/cat" : user.setState(State.CAT); break;
                            case "/art" : user.setState(State.ART); break;
                        }
                        user.setProgress(true);
                        akkaService.get(telegramUserRepository.save(user), bot);
                    } else {
                        user.setState(State.START);
                        telegramUserRepository.save(user);
                        return Collections.singletonList(messageTemplate.start(user));
                    }
                }
            }


            else if (update.hasCallbackQuery()) {

//                System.out.println(update.getCallbackQuery().getData());
                final CallbackQuery callbackQuery = update.getCallbackQuery();
                final Long chatId = callbackQuery.getFrom().getId();
                String message=callbackQuery.getData();
//                System.out.println(update.getCallbackQuery().getData());
                final TelegramUser user = telegramUserRepository.findById(chatId)
                        .orElseGet(() -> telegramUserRepository.save(new TelegramUser(callbackQuery.getFrom())));

                if (!user.isProgress()){
                    if (message.equals(State.START.name())){
                        return Collections.singletonList(messageTemplate.start(user));
                    }
                        user.setState(State.valueOf(message));
                        user.setProgress(true);
                        akkaService.get(telegramUserRepository.save(user),bot);
                }

            }

            throw new UnsupportedOperationException();
        } catch (UnsupportedOperationException e) {
            return Collections.emptyList();
        }
    }

    private Handler getHandlerByState(State state) {
//        System.out.println("state "+state);
        return handlers.stream()
                .filter(h -> h.operatedBotState() != null)
                .filter(h -> h.operatedBotState().equals(state))
                .findAny()
                .orElseThrow(UnsupportedOperationException::new);
    }

    private Handler getHandlerByCallBackQuery(String query,TelegramUser user) {
        String[] parts=query.split("-");
        String finalQuery = parts[0];
        return handlers.stream()
                .filter(h -> h.operatedCallBackQuery(user).stream()
                        .anyMatch(finalQuery::equals))
                .findAny()
                .orElseThrow(UnsupportedOperationException::new);
    }

    private boolean isMessageWithText(Update update) {
        return !update.hasCallbackQuery() && update.hasMessage() && update.getMessage().hasText()&& !update.getMessage().hasContact();
    }
}
