package com.crane.instafoll.jobs.unfollow;

import com.crane.instafoll.jobs.JobParams;
import com.github.instagram4j.instagram4j.IGClient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnfollowParams extends JobParams {

    @Builder
    public UnfollowParams(String userName,
                          int maxActionNumber,
                          int intervalInSeconds,
                          int maxWaitTime,
                          int maxRequestsInOneBatch,
                          IGClient userClient
    ) {
        super(
                userName,
                maxActionNumber,
                intervalInSeconds,
                maxWaitTime,
                maxRequestsInOneBatch,
                userClient
        );
    }

}
