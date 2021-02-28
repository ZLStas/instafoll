package com.crane.instafoll.machine.states;

import com.crane.instafoll.machine.Machine;
import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.exceptions.IGLoginException;
import com.github.instagram4j.instagram4j.utils.IGChallengeUtils;
import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;
import java.util.concurrent.Callable;

import static com.crane.instafoll.machine.states.UserKeys.INSTAGRAM_CHALLENGE;
import static com.crane.instafoll.machine.states.UserKeys.INSTAGRAM_CLIENT;
import static com.crane.instafoll.machine.states.UserKeys.INSTAGRAM_LOGIN;
import static com.crane.instafoll.machine.states.UserKeys.INSTAGRAM_PASSWORD;

public class GetPasswordState extends State {

    public GetPasswordState(Machine machine) {
        super(machine);
    }

    @Override
    public void doProcess(Update update) {
        String pass = machine.getMessageTest(update);
        if (valid(pass)) {
            Map<String, Object> storage = machine.getUserStorage();
            storage.put(INSTAGRAM_PASSWORD.toString(), pass);
            tryLogin(update, storage);
        } else {
            machine.sendResponse(update, "Password is not valid, try one more time:");
            machine.changeStateTo(new GetPasswordState(machine));
        }
    }

    void tryLogin(Update update, Map<String, Object> storage) {
        IGClient client = buildClient(storage, update);

        if (client != null) {
            storage.put(INSTAGRAM_CLIENT.toString(), client);
            renderMenu(update);
        } else {
            relogin(update);
        }
    }

    private IGClient buildClient(Map<String, Object> storage, Update update) {

        Callable<String> inputCode = () -> {
            storage.remove(INSTAGRAM_CHALLENGE.toString()); //clean up old challenge
            machine.sendResponse(update, "Input challenge sent to you by instagram");
            machine.changeStateTo(new GetChallengeState(machine));
            String challenge = (String) storage.get(INSTAGRAM_CHALLENGE.toString());
            while (challenge == null) {
                challenge = (String) storage.get(INSTAGRAM_CHALLENGE.toString());
                Thread.sleep(3000);
            }

            return challenge;
        };

        try {
            return IGClient.builder()
                    .username((String) storage.get(INSTAGRAM_LOGIN.name()))
                    .password((String) storage.get(INSTAGRAM_PASSWORD.name()))
                    .onChallenge((client, response) -> IGChallengeUtils.resolveChallenge(client, response, inputCode))
                    .login();
        } catch (IGLoginException e) {
            e.printStackTrace();
            return null;
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
