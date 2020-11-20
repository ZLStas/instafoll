package com.crane.instafoll.services;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.exceptions.IGLoginException;
import com.github.instagram4j.instagram4j.utils.IGChallengeUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;
import java.util.concurrent.Callable;

@Slf4j
public class LoginService {

    private static final Scanner scanner = new Scanner(System.in);

    public static IGClient tryLogin(String userName, String password) {
        try {
            return login(userName, password);
        } catch (IGLoginException e) {
            log.error("Can't login: {}", e.getMessage());
        }
        return null;
    }

    private static IGClient login(String userName, String password) throws IGLoginException {
        Callable<String> inputCode = () -> {
            System.out.print("Please input code: ");
            return scanner.nextLine();
        };

        // handler for challenge login
        IGClient.Builder.LoginHandler challengeHandler = (client, response) -> {
            // included utility to resolve challenges
            // may specify retries. default is 3
            return IGChallengeUtils.resolveChallenge(client, response, inputCode);
        };

        return IGClient.builder()
                .username(userName)
                .password(password)
                .onChallenge(challengeHandler)
                .login();
    }

}
