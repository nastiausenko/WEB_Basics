package org.example.lab.data.contacts;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ContactService {
    private final Map<Long, Contacts> contactsData = new ConcurrentHashMap<>();

    public void saveContactsData(Long chatId, String number, String email) {
        contactsData.put(chatId, new Contacts(number, email));
    }

    public Contacts getContacts(Long chatId) {
        return contactsData.get(chatId);
    }

    public boolean hasContactsData(Long chatId) {
        return contactsData.containsKey(chatId);
    }
}
