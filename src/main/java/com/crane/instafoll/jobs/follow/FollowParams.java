package com.crane.instafoll.jobs.follow;

import com.github.instagram4j.instagram4j.IGClient;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class FollowParams {

    private  String userName;

    private  String startWith;


    private  int maxActionNumber;

    private  int intervalInSeconds;

    private  int maxWaitTime;

    private  int maxRequestsInOneBatch;

    private  IGClient userClient;

}
