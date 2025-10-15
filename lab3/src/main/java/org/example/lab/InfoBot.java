package org.example.lab;

import io.github.cdimascio.dotenv.Dotenv;
import org.example.lab.data.chat_gpt.ChatGptServiceGroq;
import org.example.lab.data.contacts.ContactService;
import org.example.lab.data.it.ITService;
import org.example.lab.data.student.StudentService;
import org.example.lab.handlers.*;
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

import static org.example.lab.utils.Log.GLOBAL;

public class InfoBot extends TelegramLongPollingBot {
    private final StudentService studentService = new StudentService();
    private final ContactService contactService = new ContactService();
    private final ITService itService = new ITService();
    private final ChatGptServiceGroq chatGptServiceGroq;

    private final Map<Long, Query> userStates = new ConcurrentHashMap<>();
    private final Map<Query, UserInputHandler> handlers;
    private final String botUsername;

    public InfoBot(String botToken) {
        super(botToken);

        Dotenv dotenv = Dotenv.load();
        this.botUsername = System.getenv("BOT_USERNAME") != null
                ? System.getenv("BOT_USERNAME")
                : dotenv.get("BOT_USERNAME", "InfoBot");

        String chatgptToken = System.getenv("GROQ_API_KEY") != null
                ? System.getenv("GROQ_API_KEY")
                : dotenv.get("GROQ_API_KEY");

        chatGptServiceGroq = new ChatGptServiceGroq(chatgptToken);

        handlers = Map.of(
                Query.STUDENT, new StudentHandler(studentService),
                Query.CONTACTS, new ContactHandler(contactService),
                Query.IT, new ITHandler(itService),
                Query.CHATGPT, new ChatGptHandler(chatGptServiceGroq)
        );
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleMessage(update.getMessage().getChatId(), update.getMessage().getText());
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
        }
    }

    private void handleMessage(Long chatId, String text) {
        if (text.equals("/start")) {
            sendMainMenu(chatId, "Вас вітає InfoBot! Виберіть необхідну команду:");
            userStates.remove(chatId);
            return;
        }

        Query state = userStates.getOrDefault(chatId, Query.NONE);

        if (state != Query.NONE) {
            processUserInput(chatId, text, state);
        } else {
            sendMessage(chatId, "Натисніть /start, щоб відкрити меню");
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
                "back_to_menu", callback -> {
                    clearUserState(chatId);
                    editMessageToMainMenu(callback);
                }
        );

        actions.getOrDefault(data, callback -> sendMessage(chatId, "Невідома команда!"))
                .accept(cq);
    }

    private void handleStudentQuery(CallbackQuery cq) {
        long chatId = cq.getMessage().getChatId();
        String msg;
        if (studentService.hasStudentData(chatId)) {
            msg = studentService.getStudent(chatId).toString();
        } else {
            msg = "Будь ласка, введіть своє ім'я та групу у форматі:\nПрізвище І.П., Група";
            userStates.put(chatId, Query.STUDENT);
        }
        editMessage(cq, msg);
    }

    private void handleItQuery(CallbackQuery cq) {
        long chatId = cq.getMessage().getChatId();
        String msg;
        if (itService.hasTechnologies(chatId)) {
            msg = "IT-технології:\n" + String.join(", ", itService.getTechnologies(chatId));
        } else {
            msg = "Введіть IT-технології через кому (наприклад: Java, Spring, Docker)";
            userStates.put(chatId, Query.IT);
        }
        editMessage(cq, msg);
    }

    private void handleContactsQuery(CallbackQuery cq) {
        long chatId = cq.getMessage().getChatId();
        String msg;
        if (contactService.hasContactsData(chatId)) {
            msg = contactService.getContacts(chatId).toString();
        } else {
            msg = "Будь ласка, введіть телефон та пошту у форматі:\n050-555-55-55, email@example.com";
            userStates.put(chatId, Query.CONTACTS);
        }
        editMessage(cq, msg);
    }

    private void handleChatGptQuery(CallbackQuery cq) {
        long chatId = cq.getMessage().getChatId();
        editMessage(cq, "Введіть ваш запит для ChatGPT:");
        userStates.put(chatId, Query.CHATGPT);
    }

    private void processUserInput(Long chatId, String text, Query query) {
        UserInputHandler handler = handlers.get(query);
        if (handler != null) {
            handler.handle(chatId, text, this);
        } else {
            sendMessage(chatId, "Невідомий тип введення");
        }
    }

    public void sendMainMenu(Long chatId, String text) {
        sendMessage(chatId, text, buildMainMenuButtons());
    }

    public void sendMessage(Long chatId, String text) {
        sendMessage(chatId, text, null);
    }

    public void sendMessageWithBack(Long chatId, String text) {
        sendMessage(chatId, text, buildBackButton());
    }

    private void sendMessage(Long chatId, String text, InlineKeyboardMarkup markup) {
        SendMessage msg = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(markup)
                .build();
        executeSafe(msg);
    }

    private void editMessage(CallbackQuery cq, String text) {
        EditMessageText editMessage = EditMessageText.builder()
                .chatId(cq.getMessage().getChatId())
                .messageId(cq.getMessage().getMessageId())
                .text(text)
                .replyMarkup(buildBackButton())
                .build();
        executeSafe(editMessage);
    }

    private void editMessageToMainMenu(CallbackQuery cq) {
        EditMessageText editMessage = EditMessageText.builder()
                .chatId(cq.getMessage().getChatId())
                .messageId(cq.getMessage().getMessageId())
                .text("Вас вітає InfoBot! Виберіть необхідну команду:")
                .replyMarkup(buildMainMenuButtons())
                .build();
        executeSafe(editMessage);
    }

    private void executeSafe(SendMessage msg) {
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            GLOBAL.error("Помилка при відправці повідомлення: {}", e.getMessage(), e);
        }
    }

    private void executeSafe(EditMessageText editMessage) {
        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            GLOBAL.error("Помилка при редагуванні повідомлення: {}", e.getMessage(), e);
        }
    }

    private InlineKeyboardMarkup buildBackButton() {
        InlineKeyboardButton backButton = InlineKeyboardButton.builder()
                .text("⬅️ Back")
                .callbackData("back_to_menu")
                .build();
        return new InlineKeyboardMarkup(List.of(List.of(backButton)));
    }

    private InlineKeyboardMarkup buildMainMenuButtons() {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(List.of(InlineKeyboardButton.builder().text("Student").callbackData("student_info").build()));
        rows.add(List.of(InlineKeyboardButton.builder().text("IT-technologies").callbackData("it_info").build()));
        rows.add(List.of(InlineKeyboardButton.builder().text("Contacts").callbackData("contacts").build()));
        rows.add(List.of(InlineKeyboardButton.builder().text("ChatGPT").callbackData("chat_gpt").build()));
        return new InlineKeyboardMarkup(rows);
    }

    public void clearUserState(Long chatId) {
        userStates.remove(chatId);
    }
}