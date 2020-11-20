package com.crane.instafoll.jobs.unfollow;

import com.crane.instafoll.services.InstaActionService;
import com.github.instagram4j.instagram4j.models.user.Profile;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static com.github.instagram4j.instagram4j.requests.friendships.FriendshipsFeedsRequest.FriendshipsFeeds.FOLLOWING;

@Setter
@Slf4j
public class UnfollowJob implements Job {

    private int maxActionNumber;

    private InstaActionService instaActionService;

    private int unfollowed = 0;

    public void execute(JobExecutionContext context) {
        log.info("Unfollow started at: {}", new Date());
        if (this.unfollowed > maxActionNumber) {
            return;
        }

        List<Profile> usersToUnfollow = instaActionService.getUsersBy(instaActionService.getMyPk(), FOLLOWING);
        log.info("Number of users to unfollow: {}", usersToUnfollow.size());

        int unfollowedInBatch = instaActionService.doAction(usersToUnfollow);
        this.unfollowed += unfollowedInBatch;
        log.info("Unfollow completed at: {}, unfollowed in batch: {}, total unfollowed: {}",
                new Date(), unfollowedInBatch, this.unfollowed
        );
    }
}