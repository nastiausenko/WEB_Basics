package org.example.lab.data.chat_gpt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.example.lab.utils.Log.GLOBAL;

public class ChatGptServiceGroq implements ChatService {

    private static final String GROQ_ENDPOINT = "https://api.groq.com/openai/v1/chat/completions";
    private final String apiKey;
    private final HttpClient http;
    private final ObjectMapper mapper;
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public ChatGptServiceGroq(String apiKey) {
        this.apiKey = apiKey;
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.mapper = new ObjectMapper();
    }

    @Override
    public String getResponse(String prompt) {
        if (prompt == null || prompt.isBlank()) return "";

        String hash = Integer.toString(prompt.hashCode());
        if (cache.containsKey(hash)) {
            return cache.get(hash);
        }

        try {
            String bodyJson = mapper.writeValueAsString(Map.of(
                    "model", "openai/gpt-oss-20b",
                    "messages", java.util.List.of(Map.of("role", "user", "content", prompt)),
                    "max_tokens", 400
            ));

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(GROQ_ENDPOINT))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(bodyJson, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

            int status = resp.statusCode();
            String respBody = resp.body();

            if (status >= 200 && status < 300) {
                JsonNode root = mapper.readTree(respBody);
                JsonNode msgNode = root
                        .path("choices")
                        .get(0)
                        .path("message")
                        .path("content");
                String text = msgNode.isMissingNode() ? root.toString() : msgNode.asText();

                cache.put(hash, text);
                return text;
            } else if (status == 429) {
                return "Помилка: перевищено ліміт запитів (429). Спробуйте пізніше.";
            } else {
                return "Помилка від API (status=" + status + "): " + respBody;
            }
        } catch (Exception e) {
            GLOBAL.error("Сталася помилка при отриманні відповіді від Groq: {}", e.getMessage(), e);
            return "Сталася помилка при отриманні відповіді від Groq: " + e.getMessage();
        }
    }

    public void clearCache() {
        cache.clear();
    }
}