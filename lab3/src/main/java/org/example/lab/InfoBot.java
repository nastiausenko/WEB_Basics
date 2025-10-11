package org.example.lab;

import io.github.cdimascio.dotenv.Dotenv;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class InfoBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final String chatgptToken;

    public InfoBot(String botToken) {
        super(botToken);

        Dotenv dotenv = Dotenv.load();
        this.botUsername = System.getenv("BOT_USERNAME") != null
                ? System.getenv("BOT_USERNAME")
                : dotenv.get("BOT_USERNAME", "InfoBot");

        this.chatgptToken = System.getenv("OPENAI_API_KEY") != null
                ? System.getenv("OPENAI_API_KEY")
                : dotenv.get("OPENAI_API_KEY", "");
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
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
        }
    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();

        Map<String, Consumer<Long>> actions = Map.of(
                "student_info", this::handleStudentQuery,
                "it_info", this::handleItQuery,
                "contacts", this::handleContactsQuery,
                "chat_gpt", this::handleChatGptQuery
        );

        if (actions.containsKey(data)) {
            actions.get(data).accept(chatId);
        } else {
            sendMessage(chatId, "Невідома команда!");
        }
    }

    private void handleStudentQuery(Long chatId) {
        sendMessage(chatId, "Заглушка: інформація про студента");
    }

    private void handleItQuery(Long chatId) {
        sendMessage(chatId, "Заглушка: інформація про IT-технології");
    }

    private void handleContactsQuery(Long chatId) {
        sendMessage(chatId, "Заглушка: контакти");
    }

    private void handleChatGptQuery(Long chatId) {
        sendMessage(chatId, "Заглушка: ChatGPT");
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