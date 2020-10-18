package com.crane.instafoll.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@ConfigurationProperties("follow")
@Configuration
@EnableScheduling
public class FollowConfig {

    private String maxWaitTime;

    private String maxRequestsInOneBatch;

    private String startWith;


    public void setMaxWaitTime(String maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
    }

    public String getMaxWaitTime() {
        return maxWaitTime;
    }

    public String getMaxRequestsInOneBatch() {
        return maxRequestsInOneBatch;
    }

    public void setMaxRequestsInOneBatch(String maxRequestsInOneBatch) {
        this.maxRequestsInOneBatch = maxRequestsInOneBatch;
    }

    public String getStartWith() {
        return startWith;
    }

    public void setStartWith(String startWith) {
        this.startWith = startWith;
    }
}
