package commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import utils.RequestFactory;

import java.util.List;

import static utils.MessageHelpers.prepareAndSendMessage;
import static utils.MessageHelpers.sendMessage;


@Slf4j
@Component
public class LoginCommand extends BotCommand {
    public LoginCommand() {
        super(CommandId.LOGIN, "Login");
    }
    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] args) {
        StringBuilder messageTextBuilder = new StringBuilder();
        String finalUserName = user.getUserName();

        RequestFactory.buildGet("/login/"+user.getId())
            .retrieve()
            .onStatus(httpStatusCode -> httpStatusCode != HttpStatus.OK,
                    ((request, response) -> {
                        messageTextBuilder
                                .append("Login failed. Try using ")
                                .append(CommandId.SIGNUP)
                                .append(" to register");
                        log.error("Login failed: "+ user.getId());
                    }))
            .onStatus(HttpStatus.OK::equals,
                    (((request, response) -> {
                        messageTextBuilder.append("Hello, ").append(finalUserName);
                        log.info("UserDto " + finalUserName + " logged in.");
                    }))).toBodilessEntity();

        var msg = new SendMessage(String.valueOf(chat.getId()), messageTextBuilder.toString());
        InlineKeyboardButton testButton = new InlineKeyboardButton("login again");
        testButton.setCallbackData("/login");
        InlineKeyboardRow row = new InlineKeyboardRow(testButton);
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(List.of(row));
        msg.setReplyMarkup(keyboardMarkup);

        sendMessage(telegramClient, msg);
        prepareAndSendMessage(telegramClient, chat.getId(), messageTextBuilder.toString());
    }

}
