package com.example.Crypto;

import com.example.Crypto.api.CoindeskNews;
import com.example.Crypto.config.AppConfig;
import com.example.Crypto.logic.CryptoPrice;
import com.example.Crypto.models.User;
import com.example.Crypto.models.UserRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.*;

import static com.example.Crypto.models.Command.*;

@Component
public class Bot extends TelegramLongPollingBot {


    @Autowired
    AppConfig appConfig;
    @Autowired
    UserRepository userRepository;

    @Autowired
    private CryptoPrice cryptoPrice;
    @Autowired
    CoindeskNews coindeskNews;


    @Override
    public String getBotToken() {
        return appConfig.getToken();
    }

    @Override
    public String getBotUsername() {
        return appConfig.getBotName();
    }

    public Bot(AppConfig config) {
        this.appConfig = config;
        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", "Start using  "));
        listofCommands.add(new BotCommand("/best", "returns BTC,ETH,XRP to USDT price"));
        listofCommands.add(new BotCommand("/enter", "enter crypto short name"));
        listofCommands.add(new BotCommand("/news", "send you news"));
        listofCommands.add(new BotCommand("/balance", "shows your balance"));
        try {
            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        new Bot(appConfig);
        long chatId = update.getMessage().getChatId();
        User user = userRepository.findByChatId(chatId);

        if (user == null) {
            user = new User();
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            if (update.getMessage().getText().startsWith("/")) {
                switch (update.getMessage().getText().trim()) {
                    case "/start":
                        user.setChatId(chatId);
                        user.setName(update.getMessage().getChat().getFirstName());
                        user.setPreviousCommand(KEY);
                        user.setWaitingForInput(true);
                        userRepository.save(user);
                        sendMessage(chatId, "Hi " + user.getName() +
                                "Enter your apiKey:");

                        break;
                    case "/best":
                        sendMessage(chatId, cryptoPrice.reportCurrentBtcUsdtPrice());
                        break;
                    case "/enter":
                        user.setPreviousCommand(ENTER);
                        user.setWaitingForInput(true);
                        userRepository.save(user);
                        sendMessage(chatId, "Введите название криптовалюты:");
                        break;
                    case "/news":
                        sendMessage(chatId, coindeskNews.getCoindeskNews());
                        break;
                    case "/balance":
                        sendMessage(chatId, cryptoPrice.getBalance());
                        break;
                    default:
                        sendMessage(chatId, "Such command doesn't exist");
                }

            } else if (user.isWaitingForInput()) {

                switch (user.getPreviousCommand()) {
                    case ENTER:
                        sendMessage(chatId, cryptoPrice.reportCurrentCryptoPrice(update.getMessage()));
                        user.setWaitingForInput(false);
                        userRepository.save(user);
                        break;
                    case KEY:

                        user.setPreviousCommand(SECRET);
                        user.setWaitingForInput(true);
                        user.setApiKey(update.getMessage().getText());
                        userRepository.save(user);
                        cryptoPrice.registerApiKey(user.getApiKey());
                        sendMessage(chatId, "OK, now enter your secret KEY");
                        break;
                    case SECRET:
                        user.setWaitingForInput(false);
                        user.setSecretKey(update.getMessage().getText());
                        userRepository.save(user);
                        cryptoPrice.registerSecretKey(user.getSecretKey());
                        sendMessage(chatId, "You was successfully authorized!!!");
                        break;
                }

            }

        }

    }

    public void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 0 14 * * ?")
    public void sendNews() throws IOException {
        for (User user : userRepository.findAll()
        ) {
            sendMessage(user.getChatId(), coindeskNews.getCoindeskNews());
        }
    }

}
