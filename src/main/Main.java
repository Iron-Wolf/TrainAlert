package main;

import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.logging.BotLogger;
import org.telegram.telegrambots.logging.BotsFileHandler;

import java.io.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

public class Main {

    private static final String LOGTAG = "MAIN";

    public static void main(String[] args) {
        BotLogger.setLevel(Level.ALL);
        BotLogger.registerLogger(new ConsoleHandler());
        try {
            BotLogger.registerLogger(new BotsFileHandler());
        } catch (IOException e) {
            BotLogger.severe(LOGTAG, e);
        }


        try {
            System.out.println("init bot...");
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

            try {
                // register long polling bot
                TrainAlertHandler trainAlert = new TrainAlertHandler();
                telegramBotsApi.registerBot(trainAlert);
                System.out.println("Bot started");
                // start monitoring
                trainAlert.startMonitoring();
            } catch (TelegramApiException e) {
                BotLogger.error(LOGTAG, e);
            }

        }catch (Exception e) {
            BotLogger.error(LOGTAG, e);
        }
    }
}
