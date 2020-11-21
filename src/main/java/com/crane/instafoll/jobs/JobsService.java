package com.crane.instafoll.jobs;

import com.crane.instafoll.jobs.follow.FollowJob;
import com.crane.instafoll.jobs.follow.FollowParams;
import com.crane.instafoll.jobs.unfollow.UnfollowJob;
import com.crane.instafoll.jobs.unfollow.UnfollowParams;
import com.crane.instafoll.services.InstaActionService;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

@AllArgsConstructor
@Component
public class JobsService {

    public static final String MAX_ACTION_NUMBER = "maxActionNumber";
    public static final String START_WITH = "startWith";
    public static final String INSTA_ACTION_SERVICE = "instaActionService";
    public static final List<String> nonUserParams = asList(INSTA_ACTION_SERVICE);
    public static final String ACTIONS_PERFORMED = "actionsPerformed";

    private final Scheduler scheduler;

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
            Class<? extends Job> jobType
    ) {
        jobData.put(MAX_ACTION_NUMBER, params.getMaxActionNumber());
        jobData.put(ACTIONS_PERFORMED, params.getActionsPerformed());
        jobData.put(INSTA_ACTION_SERVICE,
                new InstaActionService(
                        params.getUserClient(),
                        params.getMaxWaitTime(),
                        params.getMaxRequestsInOneBatch()
                )
        );

        JobDetail job = JobBuilder.newJob(jobType)
                .withIdentity(jobType.getSimpleName(), params.getUserName())
                .usingJobData(jobData)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobType.getSimpleName(), params.getUserName())
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(params.getIntervalInSeconds())
                        .repeatForever())
                .build();

        return startJob(job, trigger);
    }

    private boolean startJob(JobDetail job, Trigger trigger) {
        try {
            this.scheduler.scheduleJob(job, trigger);
            return true;
        } catch (SchedulerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getScheduledJobs(String groupName) {
        return getCurrentUserJobs(groupName).stream()
                .map(job -> job.getJobDetail().getKey().getName())
                .collect(Collectors.joining("\n"));
    }

    public String getScheduledJobsDetails(String groupName) {
        return getCurrentUserJobs(groupName).stream()
                .map(this::extractCurrentJobDetails)
                .collect(Collectors.joining("\n"));
    }

    private String extractCurrentJobDetails(JobExecutionContext job) {
        return String.format("%s\n Job started: %s \n Next start at: %s \nParams:\n %s",
                job.getJobDetail().getKey().toString(),
                job.getFireTime(),
                job.getNextFireTime(),
                job.getJobDetail().getJobDataMap().entrySet().stream()
                        .filter(e -> !nonUserParams.contains(e.getKey()))
                        .map((e) -> e.getKey() + " : " + e.getValue())
                        .collect(Collectors.joining("\n"))

        );
    }

    private String extractJobDetails(Trigger trigger) {
        return String.format("%s\n Previous Fire Time: %s \n Next start at: %s \nEnd time:\n %s Start time:\n %s",
                trigger.getJobKey().toString(),
                trigger.getPreviousFireTime().toString(),
                trigger.getNextFireTime().toString(),
                trigger.getEndTime().toString(),
                trigger.getStartTime().toString()
        );
    }

    public boolean stopJob(String key, String groupName) {
        try {
            scheduler.deleteJob(JobKey.jobKey(key, groupName));
            return true;
        } catch (SchedulerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<JobExecutionContext> getCurrentUserJobs(String userName) {
        try {

            Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(userName));
           return scheduler.getCurrentlyExecutingJobs().stream()
                    .filter(job -> jobKeys.contains(job.getJobDetail().getKey()))
                    .collect(Collectors.toList());
        } catch (SchedulerException e) {
            e.printStackTrace();
            return null;
        }

    }

    public String getTriggersDescription(String userName) {

        Set<JobKey> jobKeys;
        try {
            jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(userName));
        } catch (SchedulerException e) {
            e.printStackTrace();
            return "";
        }
        return jobKeys.stream()
                .map(this::getTriggersDescription)
                .collect(Collectors.joining(","));

    }

    private String getTriggersDescription(JobKey jobKey) {
        try {
            return scheduler.getTriggersOfJob(jobKey).stream()
                    .map(this::extractJobDetails)
                    .collect(Collectors.joining(","));
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return "";
    }

}
