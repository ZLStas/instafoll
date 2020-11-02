package com.crane.instafoll.machine.states;

public enum MenuOption {
    FOLLOW("follow"), UNFOLLOW("unfollow"), RELOGIN("relogin");

    private final String name;

     MenuOption(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
