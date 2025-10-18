package org.example.lab.data.it;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ITService {
    private final Map<Long, List<String>> technologies = new ConcurrentHashMap<>();

    public void saveTechnology(Long chatId, String technologyName) {
        technologies.computeIfAbsent(chatId, k -> new ArrayList<>())
                .add(technologyName);
    }

    public List<String> getTechnologies(Long chatId) {
        return technologies.getOrDefault(chatId, new ArrayList<>());
    }

    public boolean hasTechnologies(Long chatId) {
        return technologies.containsKey(chatId);
    }
}
