package com.crane.instafoll.machine.states;

import com.crane.instafoll.machine.Machine;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
public class GetPasswordState implements State {

    private final Machine machine;

    @Override
    public void process(Update update) {
        String pass = machine.getMessageTest(update);
        if (valid(pass)) {
            machine.changeStateTo(new GetInstagramClient(machine));
        } else {
            machine.changeStateTo(new AskPasswordState(machine));
        }
    }

    private boolean valid(String pass) {
        return true; //TODO validate properly
    }

}
