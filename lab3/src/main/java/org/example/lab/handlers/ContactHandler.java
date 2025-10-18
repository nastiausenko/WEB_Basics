package org.example.lab.handlers;

import org.example.lab.InfoBot;
import org.example.lab.data.contacts.ContactService;

public class ContactHandler implements UserInputHandler {

    private final ContactService contactService;

    public ContactHandler(ContactService contactService) {
        this.contactService = contactService;
    }

    @Override
    public void handle(Long chatId, String input, InfoBot bot) {
        String[] parts = input.split(",");
        if (parts.length == 2) {
            contactService.saveContactsData(chatId, parts[0].trim(), parts[1].trim());
            bot.sendMainMenu(chatId, "Контактні дані збережено ✅\nВиберіть необхідну команду:");
            bot.clearUserState(chatId);
        } else {
            bot.sendMessage(chatId, "Невірний формат. Використовуйте:\n050-555-55-55, email@example.com");
        }
    }
}