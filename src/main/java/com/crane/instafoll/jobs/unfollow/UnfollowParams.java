package com.crane.instafoll.jobs.unfollow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class UnfollowParams {

    private final int maxActionNumber;

}
