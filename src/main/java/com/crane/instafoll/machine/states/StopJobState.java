package com.crane.instafoll.machine.states;

import com.crane.instafoll.machine.Machine;

import org.telegram.telegrambots.meta.api.objects.Update;

import static com.crane.instafoll.Bot.getUserName;
import static com.crane.instafoll.machine.states.HandleMenuState.FOLLOW;
import static com.crane.instafoll.machine.states.HandleMenuState.UNFOLLOW;
import static java.lang.String.format;

public class StopJobState extends State {

    public StopJobState(Machine machine) {
        super(machine);
    }

    @Override
    protected void doProcess(Update update) {
        String message = machine.getMessageTest(update);
        switch (message) {
            case FOLLOW:
                stopJob(update,"FollowJob");
                break;
            case UNFOLLOW:
                stopJob(update,"UnfollowJob");
                break;
            default:
                machine.sendResponse(update, format("No such command supported are: %s",
                        machine.getScheduledJobs(getUserName(update)))
                );
                break;
        }
    }

    private void stopJob(Update update, String jobName) {
        if (machine.stopJob(jobName, getUserName(update))) {
            machine.sendResponse(update, "Job successfully stopped!");
        } else {
            machine.sendResponse(update, "Termination unsuccessful.");
        }
        renderMenu(update);
    }

    @Override
    public String getStateName() {
        return "StopJobState";
    }
}
