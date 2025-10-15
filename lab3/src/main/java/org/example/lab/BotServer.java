package org.example.lab;

import static org.example.lab.utils.Log.GLOBAL;
import static spark.Spark.*;

public class BotServer {
    public static void main(String[] args) {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        port(port);

        get("/", (req, res) -> "OK");

        String botToken = System.getenv("BOT_TOKEN");
        if (botToken != null && !botToken.isEmpty()) {
            new Thread(() -> {
                try {
                    TelegramBot.main(new String[]{});
                } catch (Exception e) {
                    GLOBAL.error(e.getMessage());
                }
            }).start();
        }
    }
}