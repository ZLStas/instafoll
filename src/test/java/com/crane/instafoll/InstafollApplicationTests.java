package com.crane.instafoll;

import com.crane.instafoll.services.InstaActionService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.TelegramBotsApi;

@SpringBootTest
class InstafollApplicationTests {

    @MockBean
    InstaActionService instaActionService;
    @MockBean
    TelegramBotsApi telegramBotsApi;
    @Test
    void contextLoads() {
    }

}
