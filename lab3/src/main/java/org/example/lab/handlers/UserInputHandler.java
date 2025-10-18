package org.example.lab.handlers;

import org.example.lab.InfoBot;

public interface UserInputHandler {
    void handle(Long chatId, String input, InfoBot bot);
}