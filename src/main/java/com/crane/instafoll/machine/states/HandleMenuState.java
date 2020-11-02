package com.crane.instafoll.machine.states;

import com.crane.instafoll.jobs.follow.FollowParams;
import com.crane.instafoll.machine.Machine;
import com.github.instagram4j.instagram4j.IGClient;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Map;

import static com.crane.instafoll.Bot.getUserName;
import static com.crane.instafoll.machine.states.UserKeys.INSTAGRAM_CLIENT;
import static java.util.Arrays.asList;

public class HandleMenuState extends State {

    private static final String FOLLOW = "follow";
    private static final String UNFOLLOW = "unfollow";
    private static final String RELOGIN = "relogin";

    List<String> menuOptions = asList(FOLLOW, UNFOLLOW, RELOGIN);

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
                machine.changeStateTo(new StartUnfollowJobState(machine));
                break;
            case RELOGIN:
                relogin(update);
                break;
            default:
                machine.sendResponse(update,
                        String.format("No such command supported are: {}", menuOptions)
                );
                break;
        }
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

    @Override
    public String getStateName() {
        return "HandleMenuState";
    }
}
