package com.crane.instafoll.machine.states;

import com.crane.instafoll.jobs.follow.FollowParams;
import com.crane.instafoll.jobs.unfollow.UnfollowParams;
import com.crane.instafoll.machine.Machine;
import com.github.instagram4j.instagram4j.IGClient;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.util.List;
import java.util.Map;

import static com.crane.instafoll.Bot.getUserName;
import static com.crane.instafoll.machine.states.UserKeys.INSTAGRAM_CLIENT;
import static java.lang.String.format;
import static java.util.Arrays.asList;

public class HandleMenuState extends State {

     static final String FOLLOW = "follow";
     static final String UNFOLLOW = "unfollow";
    private static final String RELOGIN = "relogin";
    private static final String SCHEDULED = "scheduled";
    private static final String STOP = "stop";

    static final List<String> menuOptions = asList(FOLLOW, UNFOLLOW, RELOGIN, SCHEDULED, STOP);

    public HandleMenuState(Machine machine) {
        super(machine);
    }

    @Override
    public void doProcess(Update update) {
        String message = machine.getMessageTest(update);
        switch (message) {
            case FOLLOW:
                startFollowJob(update);
                break;
            case UNFOLLOW:
                startUnfollowJob(update);
                break;
            case RELOGIN:
                relogin(update);
                break;
            case SCHEDULED:
                showScheduledJobs(update);
                break;
            case STOP:
                machine.sendResponse(update, machine.getScheduledJobs(getUserName(update)));
                machine.changeStateTo(new StopJobState(machine));
                break;
            default:
                machine.sendResponse(update, format("No such command supported are: %s", menuOptions));
                break;
        }
    }

    private void showScheduledJobs(Update update) {
        machine.sendResponse(update, machine.getScheduledJobsDetails(getUserName(update)));
        renderMenu(update);
    }

    public void startFollowJob(Update update) {
        Map<String, Object> userStorage = machine.getUserStorage();
        IGClient client = (IGClient) userStorage.get(INSTAGRAM_CLIENT.toString());

        FollowParams followParams = FollowParams.builder()
                .intervalInSeconds(3600)
                .maxActionNumber(500)
                .maxRequestsInOneBatch(100)
                .maxWaitTime(10)
                .startWith("karol_461")
                .userClient(client)
                .userName(getUserName(update))
                .build();

        boolean jobScheduled = machine.scheduleFollowJob(followParams);
        if (jobScheduled) {
            machine.sendResponse(update, "Job has been scheduled!");
            renderMenu(update);
        } else {
            machine.sendResponse(update, "Job scheduling failed try later");
            relogin(update);
        }
    }

    public void startUnfollowJob(Update update) {
        Map<String, Object> userStorage = machine.getUserStorage();
        IGClient client = (IGClient) userStorage.get(INSTAGRAM_CLIENT.toString());

        UnfollowParams params = UnfollowParams.builder()
                .intervalInSeconds(3600)
                .maxActionNumber(500)
                .actionsPerformed(0)
                .maxRequestsInOneBatch(100)
                .maxWaitTime(10)
                .userClient(client)
                .userName(getUserName(update))
                .build();

        boolean jobScheduled = machine.scheduleUnfollowJob(params);
        if (jobScheduled) {
            machine.sendResponse(update, "Job has been scheduled!");
            renderMenu(update);
        } else {
            machine.sendResponse(update, "Job scheduling failed try later");
            relogin(update);
        }
    }

    @Override
    public String getStateName() {
        return "HandleMenuState";
    }
}
