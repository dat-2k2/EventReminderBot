package commands;

import dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import utils.RequestFactory;

@Slf4j
@Component
public class RegisterCommand extends BotCommand {
    public RegisterCommand() {
        super(CommandId.SIGNUP, "Sign up. You will be logged in automatically.");
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] strings) {
        String userName = chat.getUserName();

        if (userName == null || userName.isEmpty()) {
            userName = user.getFirstName() + " " + user.getLastName();
        }

        StringBuilder messageTextBuilder = new StringBuilder();

        final String finalUserName = userName;
        RequestFactory
            .buildPost("/signup")
            .body(
                    new UserDto(user.getId(), userName, chat.getId())
            )
            .retrieve()
            .onStatus(httpStatusCode -> httpStatusCode != HttpStatus.OK,
                    (request, response) -> {
                        log.error(response.getStatusText());
                        messageTextBuilder
                                .append("Register failed.");
                        log.error("Register failed: "+ user.getId());
                    })
            .onStatus(httpStatusCode -> httpStatusCode == HttpStatus.OK,
                    (request, response) -> {
                        messageTextBuilder
                                .append("Successfully registered.\n")
                                .append("Hello, ")
                                .append(finalUserName);
                        log.info("UserDto " + finalUserName + " registered in.");
                        log.info("UserDto " + finalUserName + " logged in");
                    })
                .toBodilessEntity();
        var answer = new SendMessage(chat.getId().toString(), messageTextBuilder.toString());

        try {
            telegramClient.execute(answer);
        } catch (TelegramApiException e) {
            log.error("Error", e);
        }
    }
}
