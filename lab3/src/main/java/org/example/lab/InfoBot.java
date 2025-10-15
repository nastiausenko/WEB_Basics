package org.example.lab;

import io.github.cdimascio.dotenv.Dotenv;
import org.example.lab.data.chat_gpt.ChatGptServiceGroq;
import org.example.lab.data.contacts.ContactService;
import org.example.lab.handlers.*;
import org.example.lab.data.it.ITService;
import org.example.lab.data.student.StudentService;
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
//    private final ChatGptService chatGptService; //OpenAI demonstration
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

//        chatGptService = new ChatGptService();
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
            long chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText();

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
        if (studentService.hasStudentData(chatId)) {
            editMessageWithBack(cq, studentService.getStudent(chatId).toString());
        } else {
            editMessageWithBack(cq, "Будь ласка, введіть своє ім'я та групу у форматі:\nПрізвище І.П., Група");
            userStates.put(chatId, Query.STUDENT);
        }
    }

    private void processUserInput(Long chatId, String text, Query query) {
        UserInputHandler handler = handlers.get(query);
        if (handler != null) {
            handler.handle(chatId, text, this);
        } else {
            sendMessage(chatId, "Невідомий тип введення");
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
        long chatId = cq.getMessage().getChatId();
        editMessageWithBack(cq, "Введіть ваш запит для ChatGPT:");
        userStates.put(chatId, Query.CHATGPT);
    }

    public void sendMainMenu(Long chatId, String text) {
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

    public void sendMessage(Long chatId, String text) {
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

    public void editMessageWithBack(CallbackQuery cq, String text) {
        long chatId = cq.getMessage().getChatId();
        int messageId = cq.getMessage().getMessageId();

        EditMessageText editMessage = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(text)
                .replyMarkup(buildBackButton())
                .build();
        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageWithBack(Long chatId, String text) {
        SendMessage editMessage = SendMessage.builder()
                .chatId(chatId)
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

    public void clearUserState(Long chatId) {
        userStates.remove(chatId);
    }
}