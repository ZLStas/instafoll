package com.crane.instafoll.machine.states;

import com.crane.instafoll.machine.Machine;
import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Update;

public class GetLoginState extends State {

    public GetLoginState(Machine machine) {
        super(machine);
    }

    @Override
    public void doProcess(Update update) {
        String login = machine.getMessageTest(update).trim();
        if (valid(login)) {
            machine.getUserStorage().put(UserKeys.INSTAGRAM_LOGIN.name(), login);

            machine.sendResponse(update, "Provide your password:");
            machine.changeStateTo(new GetPasswordState(machine));
        } else {
            machine.sendResponse(update, "Provide your login:");
            machine.changeStateTo(new GetLoginState(machine));
        }
    }

    @Override
    public String getStateName() {
        return "GetLoginState";
    }

    private boolean valid(String login) {
        return StringUtils.isNotBlank(login);//TODO add proper validation
    }


}
