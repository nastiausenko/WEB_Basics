package org.example.lab.handlers;

import org.example.lab.InfoBot;
import org.example.lab.data.it.ITService;

public class ITHandler implements UserInputHandler {

    private final ITService itService;

    public ITHandler(ITService itService) {
        this.itService = itService;
    }

    @Override
    public void handle(Long chatId, String input, InfoBot bot) {
        String[] techs = input.split(",");
        for (String tech : techs) {
            itService.saveTechnology(chatId, tech.trim());
        }
        bot.sendMainMenu(chatId, "IT-технології збережено ✅\nВиберіть необхідну команду:");
        bot.clearUserState(chatId);
    }
}