package com.crane.instafoll.machine.states;

import com.crane.instafoll.machine.Machine;
import org.telegram.telegrambots.meta.api.objects.Update;

public class GetChallengeState extends State {

    public GetChallengeState(Machine machine) {
        super(machine);
    }

    @Override
    protected void doProcess(Update update) {
        machine.getUserStorage().put(UserKeys.INSTAGRAM_CHALLENGE.toString(), machine.getMessageTest(update));
    }

    @Override
    public String getStateName() {
        return "GetChallengeState";
    }
}
