package com.crane.instafoll.config;

import org.quartz.Scheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

@Configuration
public class TelegramConfig {

    @Bean
    public TelegramBotsApi getTelegramBotsApi(Scheduler scheduler) {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new JobController(scheduler));
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }

        return telegramBotsApi;
    }

}
