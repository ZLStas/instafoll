package com.crane.instafoll.machine;

import com.crane.instafoll.Bot;
import com.crane.instafoll.machine.states.HelloState;
import com.crane.instafoll.machine.states.State;
import com.crane.instafoll.services.LoginService;
import com.github.instagram4j.instagram4j.IGClient;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;

@Slf4j
public class Machine {

    private final Bot bot;

    private State state;

    private final Map<String, Object> userStorage;

    public Machine(Map<String, Object> userStorage, Bot bot) {
        this.bot = bot;
        this.userStorage = userStorage;
        this.state = new HelloState(this);
    }

    public void process(Update update) {
        state.process(update);
    }

    public void changeStateTo(State state) {
        this.state = state;
    }

    public void sendResponse(Update update, String textToSend) {
        SendMessage message = new SendMessage();
        message.setText(textToSend);
        message.setChatId(update.getMessage().getChatId());
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        log.info("Message: \"{}\" sent to user: {}", textToSend, bot.getUserName(update));
    }

    public String getMessageTest(Update update) {
        return update.getMessage().getText();
    }

    public Map<String, Object> getUserStorage() {
        return userStorage;
    }

    public IGClient instagramLogin(String login, String password) {
        return LoginService.tryLogin(login, password);
    }
}
