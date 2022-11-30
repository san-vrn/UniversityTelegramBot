package com.example.UniversityBot.service;

import com.example.UniversityBot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.DeleteMyCommands;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {


    final BotConfig config;

    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> botCommandList = new ArrayList<>();
        botCommandList.add(new BotCommand("/start", "Стартовать бот"));
        botCommandList.add(new BotCommand("/get_schedule", "Получить расписание"));
        botCommandList.add(new BotCommand("/get_homework", "Получить домашнее задание"));
        botCommandList.add(new BotCommand("/help", "Инструкция к командам бота"));

        try {
            this.execute(new SetMyCommands(botCommandList, new BotCommandScopeDefault(), "ru"));
        } catch (TelegramApiException e) {
            log.error("Ошибка в botCommandList метода execute. Текст ошибки: " + e.getMessage(), e);
        }
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
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String text = message.getText();
            long chatId = message.getChatId();

            if (nonNull(text) && nonNull(chatId)) {
                switch (text) {
                    case "/start":
                        startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                        break;
                    case "\uD83D\uDCCB Получить расписание":
                        sendMessage(chatId, "расписание тут");
                        break;
                    case "\uD83D\uDCD7 Получить домашнее задание":
                        sendMessage(chatId, "домашнее задание");
                        break;
                    default:
                        sendMessage(chatId, "Команда не поддерживается, обратитесь к администратору(или старосте)");
                }
            }
        }
    }

    private void startCommandReceived(long chatId, String firstName) {
        String answer = "Добро пожаловать в наш уютный ботик, " + firstName + "!";
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textMessage) {
        if (nonNull(chatId) && nonNull(textMessage)) {
            SendMessage message = new SendMessage(String.valueOf(chatId), textMessage);
            message.setReplyMarkup(keyboardBuilder());

            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error("execute method error: " + e.getMessage() +
                        ". Class: " + this.getClass().getName());
            }
        }
    }

    private ReplyKeyboardMarkup keyboardBuilder() {
        var getScheduleButton = KeyboardButton.builder()
                .text("\uD83D\uDCCB Получить расписание")
                .build();

        var getHomeworkButton = KeyboardButton.builder()
                .text("\uD83D\uDCD7 Получить домашнее задание")
                .build();

        ReplyKeyboardMarkup keyboard = ReplyKeyboardMarkup.builder()
                .keyboardRow(getKeyboardRow(getScheduleButton))
                .keyboardRow(getKeyboardRow(getHomeworkButton))
                .resizeKeyboard(true)
                .build();
        return keyboard;
    }

    private KeyboardRow getKeyboardRow(KeyboardButton keyboardButton) {
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(keyboardButton);
        return keyboardRow;
    }
}
