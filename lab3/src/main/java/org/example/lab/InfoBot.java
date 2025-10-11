package org.example.lab;

import io.github.cdimascio.dotenv.Dotenv;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class InfoBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final String chatgptToken;

    public InfoBot(String botToken) {
        super(botToken);

        Dotenv dotenv = Dotenv.load();
        this.botUsername = System.getenv("BOT_USERNAME") != null
                ? System.getenv("BOT_USERNAME")
                : dotenv.get("BOT_USERNAME");

        this.chatgptToken = System.getenv("OPENAI_API_KEY") != null
                ? System.getenv("OPENAI_API_KEY")
                : dotenv.get("OPENAI_API_KEY");
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText();

            if (text.equals("/start")) {
                sendMainMenu(chatId);
            } else {
                sendMessage(chatId, "Натисніть /start, щоб відкрити меню");
            }
        }
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage msg = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMainMenu(Long chatId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Вас вітає InfoBot! Виберіть необхідну команду:")
                .build();

        InlineKeyboardButton studentButton = InlineKeyboardButton.builder()
                .text("Student")
                .callbackData("student_info")
                .build();

        InlineKeyboardButton itButton = InlineKeyboardButton.builder()
                .text("IT-technologies")
                .callbackData("it_info")
                .build();

        InlineKeyboardButton contactsButton = InlineKeyboardButton.builder()
                .text("Contacts")
                .callbackData("contacts")
                .build();

        InlineKeyboardButton chatGPTButton = InlineKeyboardButton.builder()
                .text("ChatGPT")
                .callbackData("chat_gpt")
                .build();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(List.of(studentButton));
        rows.add(List.of(itButton));
        rows.add(List.of(contactsButton));
        rows.add(List.of(chatGPTButton));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(rows);
        message.setReplyMarkup(markup);

        sendMessage(message);
    }

    private void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}