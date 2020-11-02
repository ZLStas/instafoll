package com.crane.instafoll.machine.states;

import com.crane.instafoll.machine.Machine;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
public class RenderMenuState implements State {

    private final Machine machine;

    @Override
    public void process(Update update) {
        machine.sendResponse(update,
                "Currently supported jobs follow, and unfollow to proceed enter follow/unfollow respectfully ");
        machine.changeStateTo(new HandleMenuState(machine));
    }

}
