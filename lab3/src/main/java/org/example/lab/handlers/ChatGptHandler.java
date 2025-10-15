package org.example.lab.handlers;

import org.example.lab.InfoBot;
import org.example.lab.data.chat_gpt.ChatService;

public class ChatGptHandler implements UserInputHandler {

    private final ChatService chatGptService;

    public ChatGptHandler(ChatService chatService) {
        this.chatGptService = chatService;
    }

    @Override
    public void handle(Long chatId, String input, InfoBot bot) {
        String response = chatGptService.getResponse(input);

        Integer messageId = bot.getPendingMessageId(chatId);
        if (messageId != null) {
            bot.editMessageWithBack(chatId, messageId, response);
            bot.clearPendingMessage(chatId);
        } else {
            bot.sendMessage(chatId, response);
        }

        bot.clearUserState(chatId);
    }
}