package com.example.UniversityBot.service;

import com.example.UniversityBot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static java.util.Objects.nonNull;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {


    final BotConfig config;

    public TelegramBot(BotConfig config) {
        this.config = config;
        log.info("Bot config bean is created " + this.getClass().toString());
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            String text = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if(nonNull(text) && nonNull(chatId)){
                switch (text){
                    case "/start":
                        startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                        break;
                    default: sendMessage(chatId, "Команда не поддерживается, обратитесь к администратору(или старосте)");
                }
            }
        }
    }

    private void startCommandReceived(long chatId, String firstName){
            String answer = "Добро пожаловать в наш уютный ботик, " + firstName + "!";
            sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textMessage){
        if(nonNull(chatId) && nonNull(textMessage)){
            SendMessage message = new SendMessage(String.valueOf(chatId),textMessage);

            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error("execute method error: " + e.getMessage() +
                        ". Class: " + this.getClass().getName());
            }
        }
    }
}
