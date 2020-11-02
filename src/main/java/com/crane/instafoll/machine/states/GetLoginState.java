package com.crane.instafoll.machine.states;

import com.crane.instafoll.machine.Machine;
import com.mchange.v2.lang.StringUtils;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
public class GetLoginState implements State {

    private final Machine machine;

    @Override
    public void process(Update update) {
        String login = machine.getMessageTest(update).trim();
        if (valid(login)) {
            machine.getUserStorage().put(UserKeys.INSTAGRAM_LOGIN.name(), login);
            machine.changeStateTo(new GetPasswordState(machine));
        } else {
            machine.changeStateTo(new AskLoginState(machine));
        }

    }

    private boolean valid(String login) {
        StringUtils.nonEmptyOrNull(login);//TODO add proper validation
    }
}
