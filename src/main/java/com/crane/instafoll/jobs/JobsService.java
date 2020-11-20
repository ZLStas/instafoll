package com.crane.instafoll.jobs;

import com.crane.instafoll.jobs.follow.FollowJob;
import com.crane.instafoll.jobs.follow.FollowParams;
import com.crane.instafoll.jobs.unfollow.UnfollowJob;
import com.crane.instafoll.jobs.unfollow.UnfollowParams;
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
    public static final String UNFOLLOW_JOB = "UnfollowJob";
    public static final String INSTA_ACTION_SERVICE = "instaActionService";

//    private final Scheduler scheduler;

    public boolean scheduleFollowJob(FollowParams params) {
        JobDataMap jobData = new JobDataMap();
        jobData.put(START_WITH, params.getStartWith());
        return scheduleJob(
                jobData,
                params,
                FollowJob.class
        );

    }

    public boolean scheduleUnFollowJob(UnfollowParams params) {
        JobDataMap jobData = new JobDataMap();
        return scheduleJob(
                jobData,
                params,
                UnfollowJob.class
        );
    }

    private boolean scheduleJob(
            JobDataMap jobData,
            JobParams params,
            Class<? extends Job> jobtype
    ) {
        Scheduler scheduler = getScheduler();
        if (!startScheduling(scheduler)) {
            return false;
        }

        jobData.put(MAX_ACTION_NUMBER, params.getMaxActionNumber());
        jobData.put(INSTA_ACTION_SERVICE,
                new InstaActionService(
                        params.getUserClient(),
                        params.getMaxWaitTime(),
                        params.getMaxRequestsInOneBatch()
                )
        );

        JobDetail job = JobBuilder.newJob(jobtype)
                .withIdentity(jobtype.getSimpleName(), params.getUserName())
                .usingJobData(jobData)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobtype.getSimpleName(), params.getUserName())
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(params.getIntervalInSeconds())
                        .repeatForever())
                .build();

        return startJob(scheduler, job, trigger);
    }

    Scheduler getScheduler() {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        try {
            return schedulerFactory.getScheduler();
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

    private boolean startJob(Scheduler scheduler, JobDetail job, Trigger trigger) {
        try {
            scheduler.scheduleJob(job, trigger);
            return true;
        } catch (SchedulerException e) {
            e.printStackTrace();
            return false;
        }
    }

}
