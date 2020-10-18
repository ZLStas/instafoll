package com.crane.instafoll.config;

import org.quartz.Scheduler;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class JobController extends TelegramLongPollingBot {

    private Scheduler scheduler;

    public JobController(Scheduler scheduler) {
        this.scheduler = scheduler;
    }


    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("Here is message from telegram! " + update.getMessage().getText());

        SendMessage message = new SendMessage();
        message.setText("Hi, Yura here is our bot to control jobs (follow/unfollow) for Instagram," +
                " since the application which performs main logic already exist and functioning well," +
                " this part about login and control the jobs with telegram is under development," +
                " but you already able to send message to it and get a response." +
                " Have fun! You also sent me this shit ====>>>>> " + update.getMessage().getText());
        message.setChatId(update.getMessage().getChatId());
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

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
