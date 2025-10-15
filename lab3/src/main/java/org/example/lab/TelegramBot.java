package org.example.lab;

import io.github.cdimascio.dotenv.Dotenv;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import static org.example.lab.utils.Log.GLOBAL;

public class TelegramBot {
    public static void main(String[] args) throws TelegramApiException {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        String telegramToken = System.getenv("BOT_TOKEN");
        if (telegramToken == null || telegramToken.isEmpty()) {
            telegramToken = dotenv.get("BOT_TOKEN");
        }

        if (telegramToken == null || telegramToken.isEmpty()) {
            GLOBAL.error("Bot token is not set! Please set BOT_TOKEN in environment variables or .env file.");
            return;
        }

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            GLOBAL.info("Registering bot...");
            telegramBotsApi.registerBot(new InfoBot(telegramToken));
        } catch (TelegramApiRequestException e) {
            GLOBAL.error("Failed to register bot (check internet connection / bot token or make sure only one instance of bot is running).", e);
        }
        GLOBAL.info("Telegram bot is ready to accept updates from user......");
    }
}
