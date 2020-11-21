package com.crane.instafoll.config;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SchedulerConfiguration {

    @Bean
    public Scheduler scheduler() {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        try {
            Scheduler scheduler = schedulerFactory.getScheduler();
            startScheduling(scheduler);
            return scheduler;
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return null;
    }

    boolean startScheduling(Scheduler scheduler) {
        if (scheduler == null) {
            return false;
        }
        try {
            scheduler.start();
            return true;
        } catch (SchedulerException e) {
            e.printStackTrace();
            return false;
        }
    }

}