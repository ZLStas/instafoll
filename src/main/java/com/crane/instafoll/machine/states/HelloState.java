package com.crane.instafoll.machine.states;

import com.crane.instafoll.machine.Machine;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
public class HelloState implements State {

    private final Machine machine;


    @Override
    public void process(Update update) {
        machine.sendResponse(update,"Hello, let's first login to instagram!");
        machine.changeStateTo(new AskLoginState(machine));
    }
}
