package org.example.lab.data.chat_gpt;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.example.lab.utils.Log.GLOBAL;

public class ChatGptService implements ChatService {
    private final OpenAIClient client;
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public ChatGptService() {
        this.client = OpenAIOkHttpClient.fromEnv();
    }

    @Override
    public String getResponse(String prompt) {
        String hashedPrompt = Integer.toString(prompt.hashCode());

        if (cache.containsKey(hashedPrompt)) {
            return cache.get(hashedPrompt);
        }

        try {
            ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                    .addUserMessage(prompt)
                    .model("gpt-3.5-turbo")
                    .build();

            ChatCompletion completion = client.chat().completions().create(params);

            String text = completion.choices().get(0).message().content().orElse("Немає відповіді");

            cache.put(hashedPrompt, text);

            return text;

        } catch (Exception e) {
            GLOBAL.error("Сталася помилка при отриманні відповіді від ChatGPT: {}", e.getMessage(), e);
            return "Сталася помилка при отриманні відповіді від ChatGPT";
        }
    }

    public void clearCache() {
        cache.clear();
    }
}