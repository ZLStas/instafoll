package com.crane.instafoll;

import com.crane.instafoll.config.FollowConfig;
import com.crane.instafoll.config.UserConfig;
import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.exceptions.IGLoginException;
import com.github.instagram4j.instagram4j.models.user.Profile;
import com.github.instagram4j.instagram4j.requests.friendships.FriendshipsFeedsRequest;
import com.github.instagram4j.instagram4j.requests.friendships.FriendshipsFeedsRequest.FriendshipsFeeds;
import com.github.instagram4j.instagram4j.utils.IGChallengeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static com.github.instagram4j.instagram4j.requests.friendships.FriendshipsActionRequest.FriendshipsAction.CREATE;
import static com.github.instagram4j.instagram4j.requests.friendships.FriendshipsActionRequest.FriendshipsAction.DESTROY;
import static com.github.instagram4j.instagram4j.requests.friendships.FriendshipsFeedsRequest.FriendshipsFeeds.FOLLOWERS;
import static com.github.instagram4j.instagram4j.requests.friendships.FriendshipsFeedsRequest.FriendshipsFeeds.FOLLOWING;
import static java.lang.Integer.parseInt;


@Service
@Slf4j
public class FollowService {

    public static final int MIN_NUMBER_OF_USERS = 20;
    public static final int MAX_ACTION_NUMBER = 900;
    public static final int ONE_HOUR = 3600000;

    private final UserConfig userConfig;

    private final FollowConfig followConfig;

    private final IGClient client;

    private final Random rand = new Random();

    private final Scanner scanner = new Scanner(System.in);

    //user to get list of users from to start following
    private Long hopUser;

    private int followed = 0;

    private int unfollowed = 0;

    @Autowired
    public FollowService(UserConfig userConfig, FollowConfig followConfig) {
        this.userConfig = userConfig;
        this.followConfig = followConfig;
        this.client = tryLogin();
        this.hopUser = (this.hopUser == null) ?
                getPkByUserName(followConfig.getStartWith()) :
                this.hopUser;
    }

    private IGClient tryLogin() {
        try {
            return login();
        } catch (IGLoginException e) {
            log.error("Can't login: {}", e.getMessage());
        }
        return null;
    }

    private IGClient login() throws IGLoginException {
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
                .username(userConfig.getLogin())
                .password(userConfig.getPassword())
                .onChallenge(challengeHandler)
                .login();
    }

    @Scheduled(cron = "0 1 1 * * ?")
    private void dropActionsLimitEachDay() {
        this.unfollowed = 0;
        this.followed = 0;

    }

//    @Scheduled(fixedDelay = ONE_HOUR, initialDelay = 5000)
    private void follow() {
        if (this.followed > MAX_ACTION_NUMBER) {
            log.info("reached max actions");
            return;
        }

        List<Profile> usersToFollow = getUsersBy(hopUser, FOLLOWING);

        log.info("Number of users to follow: {}", usersToFollow.size());
        int followedInBatch = doAction(usersToFollow);

        this.followed += followedInBatch;
        hopUser = pickHopUser(usersToFollow);
        log.info("Following completed at: {}, followed in batch: {}, total unfollowed: {}",
                new Date(), followedInBatch, this.followed
        );
    }

//    @Scheduled(fixedDelay = ONE_HOUR, initialDelay = 5000) //start within ~5sec
    public void unfollow() {
        log.info("Unfollow started at: {}", new Date());
        if (this.unfollowed > MAX_ACTION_NUMBER) {
            return;
        }

        List<Profile> usersToUnfollow = getUsersBy(client.getSelfProfile().getPk(), FOLLOWING);
        log.info("Number of users to unfollow: {}", usersToUnfollow.size());

        int unfollowedInBatch = doAction(usersToUnfollow);
        this.unfollowed += unfollowedInBatch;
        log.info("Unfollow completed at: {}, unfollowed in batch: {}, total unfollowed: {}",
                new Date(), unfollowedInBatch, this.unfollowed
        );
    }

    public int doAction(List<Profile> usersToDoActionOn) {
        AtomicInteger actionsPerformed = new AtomicInteger();
        int numberOfUsersToActionOn = getNumberOfUsersToDoActionOn(usersToDoActionOn.size());

        IntStream.range(0, numberOfUsersToActionOn)
                .forEach(i -> {
                    Profile userToDoActionOn = usersToDoActionOn.get(i);
                    log.info("Processing {}", userToDoActionOn.getUsername());
                    client.actions().users().findByUsername(userToDoActionOn.getUsername())
                            .thenAccept(userAction ->
                                    userAction.getFriendship()
                                            .thenAccept(friendship -> {
                                                waitBefore();
                                                log.info("Performing opposite action on {}, current friendship status is {}",
                                                        userAction.getUser().getUsername(),
                                                        friendship.isFollowing()
                                                );
                                                userAction.action(friendship.isFollowing() ? DESTROY : CREATE).thenAccept(actionResponse -> {
                                                            actionsPerformed.incrementAndGet();
                                                            log.info("Current frendship status is {}", actionResponse.getFriendship_status().isFollowing());
                                                        }
                                                );
                                            }).join()
                            ).join();

                });

        log.debug("Success");
        return actionsPerformed.get();
    }

    private Long getPkByUserName(String userName) {
        return client.actions()
                .users()
                .findByUsername(userName)
                .join()
                .getUser()
                .getPk();
    }

    private Long pickHopUser(List<Profile> users) {
        int pickNumber = rand.nextInt(users.size());
        Profile pickedUser = users.get(pickNumber);
        List<Profile> followersOfPickedUsers = getUsersBy(pickedUser.getPk(), FOLLOWERS);
        return followersOfPickedUsers.size() > MIN_NUMBER_OF_USERS ? pickedUser.getPk() : pickHopUser(users);
    }

    private List<Profile> getUsersBy(Long pk, FriendshipsFeeds action) {
        List<Profile> myFollowings = new FriendshipsFeedsRequest(pk, action).execute(client).join().getUsers();
        log.info("{} have {} - {}]", pk, myFollowings.size(), action);
        return myFollowings;
    }

    private void waitBefore() {
        int timeToWaitBeforeAction = rand.nextInt(parseInt(followConfig.getMaxWaitTime()));
        log.info("Waiting before action {}", timeToWaitBeforeAction);
        try {
            TimeUnit.SECONDS.sleep(timeToWaitBeforeAction);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private int getNumberOfUsersToDoActionOn(int numberOfUsers) {
        int MaxRequestsInOneBatch = parseInt(followConfig.getMaxRequestsInOneBatch());
        return Math.min(numberOfUsers, MaxRequestsInOneBatch);
    }

}
