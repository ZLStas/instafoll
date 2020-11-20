package com.crane.instafoll.jobs.follow;

import com.crane.instafoll.services.InstaActionService;
import com.github.instagram4j.instagram4j.models.user.Profile;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static com.github.instagram4j.instagram4j.requests.friendships.FriendshipsFeedsRequest.FriendshipsFeeds.FOLLOWING;

@Setter
@Slf4j
public class FollowJob implements Job {

    private String startWith;

    private int maxActionNumber;

    private int followed = 0;

    private InstaActionService instaActionService;

    //user to get list of users from to start following
    private Long hopUser;

    public void execute(JobExecutionContext context) {
        this.hopUser = (this.hopUser == null) ?
                instaActionService.getPkByUserName(startWith) :
                this.hopUser;

        if (this.followed > maxActionNumber) {
            log.info("reached max actions");
            return;
        }

        List<Profile> usersToFollow = instaActionService.getUsersBy(hopUser, FOLLOWING);

        log.info("Number of users to follow: {}", usersToFollow.size());
        int followedInBatch = instaActionService.doAction(usersToFollow);

        this.followed += followedInBatch;
        hopUser = instaActionService.pickHopUser(usersToFollow);
        log.info("Following completed at: {}, followed in batch: {}, total unfollowed: {}",
                new Date(), followedInBatch, this.followed
        );
    }

}