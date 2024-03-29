package com.crane.instafoll.machine.states;

import com.crane.instafoll.machine.Machine;
import org.telegram.telegrambots.meta.api.objects.Update;

public class HelloState extends State {

    public HelloState(Machine machine) {
        super(machine);
    }

    @Override
    public void doProcess(Update update) {
        machine.sendResponse(update, "Hello, let's first login to instagram!,provide your login:");
        machine.changeStateTo(new GetLoginState(machine));
    }

    @Override
    public String getStateName() {
        return "HelloState";
    }
}
