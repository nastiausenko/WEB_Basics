package org.example.lab.handlers;

import org.example.lab.InfoBot;
import org.example.lab.data.chat_gpt.ChatService;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class ChatGptHandler implements UserInputHandler {

    private final ChatService chatGptService;

    public ChatGptHandler(ChatService chatService) {
        this.chatGptService = chatService;
    }

    @Override
    public void handle(Long chatId, String input, InfoBot bot) {
        String response = chatGptService.getResponse(input);

        bot.sendMessageWithBack(chatId, response);

        bot.clearUserState(chatId);
    }

}