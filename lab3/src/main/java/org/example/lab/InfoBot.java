package org.example.lab;

import io.github.cdimascio.dotenv.Dotenv;
import org.example.lab.contacts.ContactService;
import org.example.lab.it.ITService;
import org.example.lab.student.StudentService;
import org.example.lab.utils.Query;
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
    private final ContactService contactService = new ContactService();
    private final ITService itService = new ITService();
    private final Map<Long, Query> userStates = new ConcurrentHashMap<>();
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

            Query state = userStates.getOrDefault(chatId, Query.NONE);

            if (state != Query.NONE) {
                processUserInput(chatId, text, state);
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
            userStates.put(chatId, Query.STUDENT);
        }
    }

    private void processUserInput(Long chatId, String text, Query query) {
        switch (query) {
            case STUDENT -> processStudentData(chatId, text);
            case IT -> processItData(chatId, text);
            case CONTACTS -> processContactsData(chatId, text);
            case CHAT_GPT -> sendMessage(chatId, "Запит відправлено до ChatGPT: " + text);
            default -> sendMessage(chatId, "Невідомий тип введення");
        }
    }

    private void processItData(Long chatId, String text) {
        String[] techs = text.split(",");
        for (String tech : techs) {
            itService.saveTechnology(chatId, tech.trim());
        }
        sendMainMenu(chatId, "IT-технології збережено ✅\nВиберіть необхідну команду:");
        userStates.remove(chatId);
    }

    private void processContactsData(Long chatId, String text) {
        String[] parts = text.split(",");
        if (parts.length == 2) {
            String phone = parts[0].trim();
            String email = parts[1].trim();
            contactService.saveContactsData(chatId, phone, email);
            sendMainMenu(chatId, "Контактні дані збережено ✅\nВиберіть необхідну команду:");
            userStates.remove(chatId);
        } else {
            sendMessage(chatId, "Невірний формат. Використовуйте:\n050-555-55-55, email@example.com");
        }
    }

    private void processStudentData(Long chatId, String text) {
        String[] parts = text.split(",");
        if (parts.length == 2) {
            studentService.saveStudentData(chatId, parts[0].trim(), parts[1].trim());
            sendMainMenu(chatId, "Дані збережено ✅\nВиберіть необхідну команду:");
            userStates.remove(chatId);
        } else {
            sendMessage(chatId, "Невірний формат. Використовуйте:\nПрізвище І.П., Група");
        }
    }

    private void handleItQuery(CallbackQuery cq) {
        long chatId = cq.getMessage().getChatId();
        if (itService.hasTechnologies(chatId)) {
            List<String> techs = itService.getTechnologies(chatId);
            editMessageWithBack(cq, "IT-технології:\n" + String.join(", ", techs));
        } else {
            editMessageWithBack(cq, "Введіть IT-технології через кому (наприклад: Java, Spring, Docker)");
            userStates.put(chatId, Query.IT);
        }
    }

    private void handleContactsQuery(CallbackQuery cq) {
        long chatId = cq.getMessage().getChatId();
        if (contactService.hasContactsData(chatId)) {
            editMessageWithBack(cq, contactService.getContacts(chatId).toString());
        } else {
            editMessageWithBack(cq, "Будь ласка, введіть телефон та пошту у форматі:\n050-555-55-55, email@example.com");
            userStates.put(chatId, Query.CONTACTS);
        }
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