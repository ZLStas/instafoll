package com.crane.instafoll.machine.states;

import com.crane.instafoll.machine.Machine;
import com.github.instagram4j.instagram4j.IGClient;
import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

public class GetPasswordState extends State {

    public GetPasswordState(Machine machine) {
        super(machine);
    }

    @Override
    public void doProcess(Update update) {
        String pass = machine.getMessageTest(update);
        if (valid(pass)) {
            Map<String, Object> storage = machine.getUserStorage();
            storage.put(UserKeys.INSTAGRAM_PASSWORD.toString(), pass);
            tryLogin(update, storage);
        } else {
            machine.sendResponse(update, "Password is not valid, try one more time:");
            machine.changeStateTo(new GetPasswordState(machine));
        }
    }

    void tryLogin(Update update, Map<String, Object> storage) {
        IGClient client = machine.instagramLogin(
                (String) storage.get(UserKeys.INSTAGRAM_LOGIN.name()),
                (String) storage.get(UserKeys.INSTAGRAM_PASSWORD.name())
        );
        if (client != null) {
            storage.put(UserKeys.INSTAGRAM_CLIENT.toString(), client);
            renderMenu(update);
        } else {
            relogin(update);
        }
    }

    @Override
    public String getStateName() {
        return "GetPasswordState";
    }

    private boolean valid(String pass) {
        return StringUtils.isNotBlank(pass); //TODO validate properly
    }

}
