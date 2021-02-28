package com.crane.instafoll.machine;

import com.crane.instafoll.Bot;
import com.crane.instafoll.jobs.JobsService;
import com.crane.instafoll.jobs.follow.FollowParams;
import com.crane.instafoll.jobs.unfollow.UnfollowParams;
import com.crane.instafoll.machine.states.HelloState;
import com.crane.instafoll.machine.states.State;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class Machine {

    private final Bot bot;

    private final JobsService jobsService;

    private final Map<String, Object> userStorage;

    private State state = new HelloState(this);

    public void process(Update update) {
        state.process(update);
    }

    public void changeStateTo(State state) {
        this.state = state;
    }

    public void sendResponse(Update update, String textToSend) {
        SendMessage message = new SendMessage();
        message.setText(textToSend);
        message.setChatId(update.getMessage().getChatId());
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        log.info("Message: \"{}\" sent to user: {}", textToSend, Bot.getUserName(update));
    }

    public String getMessageTest(Update update) {
        return update.getMessage().getText();
    }

    public Map<String, Object> getUserStorage() {
        return userStorage;
    }

    public boolean scheduleFollowJob(FollowParams followParams) {
        return jobsService.scheduleFollowJob(followParams);
    }

    public boolean scheduleUnfollowJob(UnfollowParams unfollowParams) {
        return jobsService.scheduleUnFollowJob(unfollowParams);
    }

    public String getScheduledJobsDetails(String groupName) {
        return jobsService.getScheduledJobsDetails(groupName);
    }

    public boolean stopJob(String key, String groupName) {
        return jobsService.stopJob(key, groupName);
    }

    public String getScheduledJobs(String groupName) {
        return jobsService.getScheduledJobs(groupName);
    }

    public String getTriggersDescription(String groupName) {
        return jobsService.getTriggersDescription(groupName);
    }
}
