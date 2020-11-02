package com.crane.instafoll.jobs;

import com.crane.instafoll.jobs.follow.FollowJob;
import com.crane.instafoll.jobs.follow.FollowParams;
import com.crane.instafoll.services.InstaActionService;
import lombok.AllArgsConstructor;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class JobsService {

    public static final String MAX_ACTION_NUMBER = "maxActionNumber";
    public static final String START_WITH = "startWith";
    public static final String FOLLOWING_JOB = "FollowingJob";
    public static final String INSTA_ACTION_SERVICE = "instaActionService";

//    private final Scheduler scheduler;

    public void scheduleFollowJob(FollowParams params) throws SchedulerException {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();
        scheduler.start();

        JobDataMap jobData = new JobDataMap();
        jobData.put(MAX_ACTION_NUMBER, params.getMaxActionNumber());
        jobData.put(START_WITH, params.getStartWith());
        jobData.put(INSTA_ACTION_SERVICE,
                new InstaActionService(
                        params.getUserClient(),
                        params.getMaxWaitTime(),
                        params.getMaxRequestsInOneBatch()
                )
        );

        JobDetail job = JobBuilder.newJob(FollowJob.class)
                .withIdentity(FOLLOWING_JOB, params.getUserName())
                .usingJobData(jobData)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(FOLLOWING_JOB, params.getUserName())
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(params.getIntervalInSeconds())
                        .repeatForever())
                .build();

        scheduler.scheduleJob(job, trigger);

    }

}
