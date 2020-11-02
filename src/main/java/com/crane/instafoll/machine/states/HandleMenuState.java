package com.crane.instafoll.machine.states;

import com.crane.instafoll.machine.Machine;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
public class HandleMenuState implements State {
    private final Machine machine;

    @Override
    public void process(Update update) {
        String message = machine.getMessageTest(update);
        switch (message) {
            case MenuOption.FOLLOW:
            case MenuOption.UNFOLLOW.toString().toLowerCase():
            case MenuOption.RELOGIN.toString():
                machine.changeStateTo(new AskLoginState(machine));
                break;
            default:
                machine.sendResponse(update,
                        String.format("No such command supported are: {}", MenuOption.values())
                        );
        }

    }
}
