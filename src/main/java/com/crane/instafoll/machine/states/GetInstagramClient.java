package com.crane.instafoll.machine.states;

import com.crane.instafoll.machine.Machine;
import com.github.instagram4j.instagram4j.IGClient;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

@RequiredArgsConstructor
public class GetInstagramClient implements State {

    private final Machine machine;

    @Override
    public void process(Update update) {
        Map<String, Object> storage = machine.getUserStorage();
        IGClient client = machine.instagramLogin(
                (String) storage.get(UserKeys.INSTAGRAM_LOGIN.name()),
                (String) storage.get(UserKeys.INSTAGRAM_PASSWORD.name())
        );
        if (client != null) {
            storage.put(UserKeys.INSTAGRAM_CLIENT.name(), client);
            machine.changeStateTo(new RenderMenuState(machine));

        } else {
            machine.sendResponse(update, "Can't login with provided login and password");
            machine.changeStateTo(new GetLoginState(machine));
        }
    }
}
