package com.crane.instafoll.machine.states;

import com.crane.instafoll.machine.Machine;
import org.telegram.telegrambots.meta.api.objects.Update;

public class StartUnfollowJobState extends State {

    public StartUnfollowJobState(Machine machine) {
        super(machine);
    }

    @Override
    public void doProcess(Update update) {

    }

    @Override
    public String getStateName() {
        return "StartUnfollowJobState";
    }

}
