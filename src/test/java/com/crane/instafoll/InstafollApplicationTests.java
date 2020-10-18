package com.crane.instafoll;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class InstafollApplicationTests {

    @MockBean
    FollowService followService;

    @Test
    void contextLoads() {
    }

}
