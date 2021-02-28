package com.crane.instafoll.jobs;

import com.github.instagram4j.instagram4j.IGClient;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JobParams {

    private final String userName;

    private final int maxActionNumber;

    private final Integer actionsPerformed;

    private final int intervalInSeconds;

    private final int maxWaitTime;

    private final int maxRequestsInOneBatch;

    private final IGClient userClient;

}
