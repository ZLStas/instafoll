package com.crane.instafoll.machine.states;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface State {

    void process(Update update);

}
