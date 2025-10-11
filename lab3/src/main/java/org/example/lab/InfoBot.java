package org.example.lab;

import io.github.cdimascio.dotenv.Dotenv;
import org.example.lab.student.StudentService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class InfoBot extends TelegramLongPollingBot {

    private final StudentService studentService = new StudentService();
    private final Map<Long, Boolean> waitingForStudentData = new ConcurrentHashMap<>();
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

            if (Boolean.TRUE.equals(waitingForStudentData.get(chatId))) {
                processStudentData(chatId, text);
            } else if (text.equals("/start")) {
                sendMainMenu(chatId, "Вас вітає InfoBot! Виберіть необхідну команду:");
            } else {
                sendMessage(chatId, "Натисніть /start, щоб відкрити меню");
            }

        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
        }
    }

    private void handleCallbackQuery(CallbackQuery cq) {
        String data = cq.getData();
        Long chatId = cq.getMessage().getChatId();

        Map<String, Consumer<CallbackQuery>> actions = Map.of(
                "student_info", this::handleStudentQuery,
                "it_info", this::handleItQuery,
                "contacts", this::handleContactsQuery,
                "chat_gpt", this::handleChatGptQuery,
                "back_to_menu", this::editMessageToMainMenu
        );

        actions.getOrDefault(data, callback -> sendMessage(chatId, "Невідома команда!"))
                .accept(cq);
    }

    private void handleStudentQuery(CallbackQuery cq) {
        long chatId = cq.getMessage().getChatId();
        if (studentService.hasStudentData(chatId)) {
            editMessageWithBack(cq, studentService.getStudent(chatId).toString());
        } else {
            editMessageWithBack(cq, "Будь ласка, введіть своє ім'я та групу у форматі:\nПрізвище І.П., Група");
            waitingForStudentData.put(chatId, true);
        }
    }

    private void processStudentData(Long chatId, String text) {
        String[] parts = text.split(",");
        if (parts.length == 2) {
            studentService.saveStudentData(chatId, parts[0].trim(), parts[1].trim());
            sendMainMenu(chatId, "Дані збережено ✅\nВиберіть необхідну команду:");
            waitingForStudentData.remove(chatId);
        } else {
            sendMessage(chatId, "Невірний формат. Використовуйте:\nПрізвище І.П., Група");
        }
    }

    private void handleItQuery(CallbackQuery cq) {
        editMessageWithBack(cq, "Заглушка: інформація про IT-технології");
    }

    private void handleContactsQuery(CallbackQuery cq) {
        editMessageWithBack(cq, "Заглушка: контакти");
    }

    private void handleChatGptQuery(CallbackQuery cq) {
        editMessageWithBack(cq, "Заглушка: ChatGPT");
    }

    private void sendMainMenu(Long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(buildMainMenuButtons())
                .build();
        sendMessage(message);
    }

    private void editMessageToMainMenu(CallbackQuery cq) {
        EditMessageText editMessage = EditMessageText.builder()
                .chatId(cq.getMessage().getChatId())
                .messageId(cq.getMessage().getMessageId())
                .text("Вас вітає InfoBot! Виберіть необхідну команду:")
                .replyMarkup(buildMainMenuButtons())
                .build();
        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
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

    private void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void editMessageWithBack(CallbackQuery cq, String text) {
        EditMessageText editMessage = EditMessageText.builder()
                .chatId(cq.getMessage().getChatId())
                .messageId(cq.getMessage().getMessageId())
                .text(text)
                .replyMarkup(buildBackButton())
                .build();
        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup buildBackButton() {
        InlineKeyboardButton backButton = InlineKeyboardButton.builder()
                .text("⬅️ Back")
                .callbackData("back_to_menu")
                .build();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(List.of(backButton));
        return new InlineKeyboardMarkup(rows);
    }

    private InlineKeyboardMarkup buildMainMenuButtons() {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(List.of(InlineKeyboardButton.builder().text("Student").callbackData("student_info").build()));
        rows.add(List.of(InlineKeyboardButton.builder().text("IT-technologies").callbackData("it_info").build()));
        rows.add(List.of(InlineKeyboardButton.builder().text("Contacts").callbackData("contacts").build()));
        rows.add(List.of(InlineKeyboardButton.builder().text("ChatGPT").callbackData("chat_gpt").build()));
        return new InlineKeyboardMarkup(rows);
    }
}