package com.crane.instafoll.jobs.follow;

import com.crane.instafoll.jobs.JobParams;
import com.github.instagram4j.instagram4j.IGClient;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class FollowParams extends JobParams {

    private String startWith;

    @Builder
    public FollowParams(String userName,
                        String startWith,
                        int maxActionNumber,
                        int actionsPerformed,
                        int intervalInSeconds,
                        int maxWaitTime,
                        int maxRequestsInOneBatch,
                        IGClient userClient
    ) {
        super(
                userName,
                maxActionNumber,
                actionsPerformed,
                intervalInSeconds,
                maxWaitTime,
                maxRequestsInOneBatch,
                userClient
        );
        this.startWith = startWith;
    }
}
