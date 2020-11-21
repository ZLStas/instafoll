package com.crane.instafoll.jobs.unfollow;

import com.crane.instafoll.services.InstaActionService;
import com.github.instagram4j.instagram4j.models.user.Profile;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.Date;
import java.util.List;

import static com.github.instagram4j.instagram4j.requests.friendships.FriendshipsFeedsRequest.FriendshipsFeeds.FOLLOWING;

@Setter
@Slf4j
public class UnfollowJob implements Job {

    private int maxActionNumber;

    private InstaActionService instaActionService;

    private Integer actionsPerformed;

    public void execute(JobExecutionContext context) {
        log.info("Unfollow started at: {}", new Date());
        if (this.actionsPerformed > maxActionNumber) {
            return;
        }

        List<Profile> usersToUnfollow = instaActionService.getUsersBy(instaActionService.getMyPk(), FOLLOWING);
        log.info("Number of users to unfollow: {}", usersToUnfollow.size());

        int unfollowedInBatch = instaActionService.doAction(usersToUnfollow);
        this.actionsPerformed += unfollowedInBatch;
        log.info("Unfollow completed at: {}, unfollowed in batch: {}, total unfollowed: {}",
                new Date(), unfollowedInBatch, this.actionsPerformed
        );
    }
}