package com.crane.instafoll;

import com.crane.instafoll.jobs.JobsService;
import com.crane.instafoll.machine.Machine;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot {

    private final JobsService jobsService;

    private final Map<String, Machine> machines = new HashMap<>();

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        Machine machine = getUserMachineCreateIfAbsent(update);
        machine.process(update);
    }

    @NotNull
    Machine getUserMachineCreateIfAbsent(Update update) {
        return machines.computeIfAbsent(
                getUserName(update),
                x -> new Machine(
                        this,
                        jobsService,
                        new HashMap<>()
                )
        );
    }

    public static String getUserName(Update update) {
        return update.getMessage().getFrom().getUserName();
    }

    @Override
    public String getBotUsername() {
        //noinspection SpellCheckingInspection
        return "InstafollControlBot";
    }

    @Override
    public String getBotToken() {
        //noinspection SpellCheckingInspection
        return "1272038774:AAGhfPBkI458FTQexuhQ2jOzT-pdDwc3QoE";
    }

}
