package commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@Slf4j
public class HelloCommand extends BotCommand {
    private static final String LOGTAG = "HELLOCOMMAND";

    public HelloCommand() {
        super(CommandId.HELLO, "starting bot");
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] args) {
        String userName = chat.getUserName();

        if (userName == null || userName.isEmpty()) {
            userName = user.getFirstName() + " " + user.getLastName();
        }

        StringBuilder messageTextBuilder = new StringBuilder("Hello ").append(userName);


        if (args != null && args.length > 0) {
            messageTextBuilder.append("\n");
            messageTextBuilder.append("Thank you so much for your kind words:\n");
            messageTextBuilder.append(String.join(" ", args));
        }

        var answer = new SendMessage(chat.getId().toString(), messageTextBuilder.toString());

        try {
            telegramClient.execute(answer);
        } catch (TelegramApiException e) {
            log.error("Error", e);
        }

        var result =
                RestClient.builder().baseUrl("http://localhost:8080/api/")
                .build();
        System.out.println(result);

    }
}
