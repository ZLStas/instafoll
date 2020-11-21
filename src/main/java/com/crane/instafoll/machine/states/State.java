package com.crane.instafoll.machine.states;

import com.crane.instafoll.machine.Machine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.stream.Collectors;

import static com.crane.instafoll.Bot.getUserName;
import static com.crane.instafoll.machine.states.HandleMenuState.menuOptions;

@Slf4j
@RequiredArgsConstructor
public abstract class State {

    public final Machine machine;

    public void process(Update update) {
        log.info(String.format("%s's machine in: %s", getUserName(update), getStateName()));
        doProcess(update);
    }

    protected abstract void doProcess(Update update);

    public abstract String getStateName();

    protected void relogin(Update update) {
        machine.sendResponse(update, "Can't login with provided login and password, try one more time enter you login:");
        machine.changeStateTo(new GetLoginState(machine));
    }

    protected void renderMenu(Update update) {
        machine.sendResponse(update,
                String.format("***Menu***\n%s",
                        String.join("\n", menuOptions)
                ));
        machine.changeStateTo(new HandleMenuState(machine));
    }

}
