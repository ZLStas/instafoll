package com.crane.instafoll.machine.states;


import com.crane.instafoll.machine.Machine;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
public class AskPasswordState implements State {

    private final Machine machine;

    @Override
    public void process(Update update) {
        machine.sendResponse(update, "Provide your password");
        machine.changeStateTo(new GetPasswordState(machine));
    }

}

