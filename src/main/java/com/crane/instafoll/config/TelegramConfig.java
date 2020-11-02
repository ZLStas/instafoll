package com.crane.instafoll.config;

import com.crane.instafoll.Bot;
import com.crane.instafoll.jobs.JobsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

@Configuration
public class TelegramConfig {

    @Bean
    public TelegramBotsApi getTelegramBotsApi(JobsService jobsService) {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new Bot(jobsService));
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }

        return telegramBotsApi;
    }

}
