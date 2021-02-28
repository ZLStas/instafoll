package com.crane.instafoll.machine.states;

import com.crane.instafoll.machine.Machine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.crane.instafoll.Bot.getUserName;
import static com.crane.instafoll.machine.states.HandleMenuState.menuOptions;

@Slf4j
@RequiredArgsConstructor
public abstract class State {

    public static final String RESTART = "restart";
    public final Machine machine;

    public abstract String getStateName();

    protected abstract void doProcess(Update update);

    public void process(Update update) {
        log.info(String.format("%s's machine in: %s", getUserName(update), getStateName()));
        if (machine.getMessageTest(update).equalsIgnoreCase(RESTART)) {
            machine.changeStateTo(new HelloState(machine));
        }
        doProcess(update);
    }

    protected void relogin(Update update) {
        machine.sendResponse(update, "Enter you login:");
        machine.changeStateTo(new GetLoginState(machine));
    }

    protected void renderMenu(Update update) {
        machine.sendResponse(update,
                String.format("***Menu***\n%s",
                        String.join("\n", menuOptions)
                )
        );
        machine.changeStateTo(new HandleMenuState(machine));
    }

}
