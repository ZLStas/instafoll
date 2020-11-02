package com.crane.instafoll.services;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.models.user.Profile;
import com.github.instagram4j.instagram4j.requests.friendships.FriendshipsFeedsRequest;
import com.github.instagram4j.instagram4j.requests.friendships.FriendshipsFeedsRequest.FriendshipsFeeds;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static com.github.instagram4j.instagram4j.requests.friendships.FriendshipsActionRequest.FriendshipsAction.CREATE;
import static com.github.instagram4j.instagram4j.requests.friendships.FriendshipsActionRequest.FriendshipsAction.DESTROY;
import static com.github.instagram4j.instagram4j.requests.friendships.FriendshipsFeedsRequest.FriendshipsFeeds.FOLLOWERS;

@Slf4j
@AllArgsConstructor
public class InstaActionService {

    private final IGClient client;

    private final int maxWaitTime;

    private final int maxRequestsInOneBatch;

    //min number of user to proceed with this one as hop user
    public static final int MIN_NUMBER_OF_USERS = 20;

    private final Random rand = new Random();


//    @Scheduled(cron = "0 1 1 * * ?") //TODO
//    private void dropActionsLimitEachDay() {
//        this.unfollowed = 0;
//        this.followed = 0;
//
//    }

    public int doAction(List<Profile> usersToDoActionOn) { //TODO refactor properly!
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

    public Long getMyPk() {
        return client.getSelfProfile().getPk();
    }

    public Long getPkByUserName(String userName) {
        return client.actions()
                .users()
                .findByUsername(userName)
                .join()
                .getUser()
                .getPk();
    }

    public Long pickHopUser(List<Profile> users) {
        int pickNumber = rand.nextInt(users.size());
        Profile pickedUser = users.get(pickNumber);
        List<Profile> followersOfPickedUsers = getUsersBy(pickedUser.getPk(), FOLLOWERS);
        return followersOfPickedUsers.size() > MIN_NUMBER_OF_USERS ? pickedUser.getPk() : pickHopUser(users);
    }

    public List<Profile> getUsersBy(Long pk, FriendshipsFeeds action) {
        List<Profile> myFollowings = new FriendshipsFeedsRequest(pk, action).execute(client).join().getUsers();
        log.info("{} have {} - {}]", pk, myFollowings.size(), action);
        return myFollowings;
    }

    private void waitBefore() {
        int timeToWaitBeforeAction = rand.nextInt(this.maxWaitTime);
        log.info("Waiting before action {}", timeToWaitBeforeAction);
        try {
            TimeUnit.SECONDS.sleep(timeToWaitBeforeAction);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private int getNumberOfUsersToDoActionOn(int numberOfUsers) {
        return Math.min(numberOfUsers, this.maxRequestsInOneBatch);
    }

}
