package com.crane.instafoll;

import com.crane.instafoll.jobs.JobsService;
import com.crane.instafoll.services.LoginService;
import com.crane.instafoll.machine.Machine;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Bot extends TelegramLongPollingBot {


    public static final int ONE_HOUR = 3600;

    public static final String USER_MACHINE_STATE = "userMachineState";
    public static final String FOLLOW_COMMAND = "follow";

    public static final String INPUT_LOGIN_SENT_STATE = "inputLoginSent";
    public static final String PASSWORD_SET_STATE = "passwordSet";
    public static final String GRITTING_SENT_STATE = "grittingSent";

    public static final String CURRENT_INTERACTION = "currentInteraction";
    public static final String INSTAGRAM_LOGIN = "instagramLogin";
    public static final String INSTAGRAM_PASSWORD = "instagramPassword";


    private final JobsService jobsService;


    private final Map<String, Machine> machines = new HashMap<>();

    public Bot(JobsService jobsService) {
        this.jobsService = jobsService;
    }



    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        Machine machine = getUserMachine(update);
        machine.process(update);


//        String messageTest = getMessageTest(update);
//        log.info("Message: \"{}\" got from user: {}", messageTest, getUserName(update));
//
//        Map<String, String> currentUserStorage = getCurrentUserStorage(update);
//        String currentUserState = currentUserStorage.get(CURRENT_INTERACTION);
//
//        if (currentUserState == null) {
//            currentUserStorage.put(CURRENT_INTERACTION, GRITTING_SENT_STATE);
//            sendResponse(update, "Доступна фіча автофоловінгу, щоб розпочати введіть \"follow\"");
//            return;
//        }
//
//        if (FOLLOW_COMMAND.equalsIgnoreCase(messageTest)) {
//            currentUserStorage.put(CURRENT_INTERACTION, INPUT_LOGIN_SENT_STATE);
//            sendResponse(update, "Введіть логін");
//            return;
//        }
//
//        if (INPUT_LOGIN_SENT_STATE.equalsIgnoreCase(currentUserState)) {
//            currentUserStorage.put(INSTAGRAM_LOGIN, messageTest);
//            currentUserStorage.put(CURRENT_INTERACTION, PASSWORD_SET_STATE);
//            sendResponse(update, "Введіть пароль");
//            return;
//        }
//
//        if (PASSWORD_SET_STATE.equalsIgnoreCase(currentUserState)) {
//            currentUserStorage.put(INSTAGRAM_PASSWORD, messageTest);
//            currentUserStorage.put(CURRENT_INTERACTION, PASSWORD_SET_STATE);
//
//            startFollowJob(update);
//
//            sendResponse(update, "фоллов джоба запущенна!, перевірте свій інстаграм!");
//            return;
//        }

//        sendResponse(update, "Упс, фігня якась, нема такої команди!");
    }

    @NotNull
    Machine getUserMachine(Update update) {
        return machines.computeIfAbsent(getUserName(update), x -> new Machine(new HashMap<>(), this, jobsService));

    }


    public static String getUserName(Update update) {
        return update.getMessage().getFrom().getUserName();
    }



    @Override
    public String getBotUsername() {
        return "InstafollControlBot";
    }

    @Override
    public String getBotToken() {
        return "1272038774:AAGhfPBkI458FTQexuhQ2jOzT-pdDwc3QoE";
    }
}
