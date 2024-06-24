package commands;

import utils.RequestFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static utils.SendMessageUtils.prepareAndSendMessage;


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

        prepareAndSendMessage(telegramClient, chat.getId(), messageTextBuilder.toString());
    }

}
