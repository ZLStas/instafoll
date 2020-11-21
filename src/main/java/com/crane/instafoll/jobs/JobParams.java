package com.crane.instafoll.jobs;

import com.github.instagram4j.instagram4j.IGClient;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JobParams {

    private String userName;

    private int maxActionNumber;

    private Integer actionsPerformed;

    private int intervalInSeconds;

    private int maxWaitTime;

    private int maxRequestsInOneBatch;

    private IGClient userClient;

}
