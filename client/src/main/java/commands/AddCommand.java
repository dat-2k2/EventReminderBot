package commands;

import dto.EventDto;
import dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import utils.MessageHelpers;
import utils.RequestFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static utils.MessageHelpers.prepareAndSendMessage;

@Component
@Slf4j
public class AddCommand extends BotCommand {
    public AddCommand() {
        super(CommandId.ADD, "Add an event with these fields, separated by whitespace:\n"
        + "{Summary (no whitespace) {Date(yyyy-MM-dd)} {Time(HH:mm)} {Duration(ISO-8601)}.\n"
        + "Example: /" + CommandId.ADD+" test-event 2024-06-26 12:31 PT10H\n"
        +   "/" + CommandId.ADD+" test-event 2024-06-26 23:11 P1DT2H.");
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] strings) {
        EventDto event = new EventDto();
        var messageTextBuilder = new StringBuilder();
        var user_ = RequestFactory.buildGet("/login/"+user.getId())
                        .retrieve()
                        .onStatus(HttpStatusCode::isError,
                                (request, response) -> {
                                    messageTextBuilder.append("Unauthenticated user.\n");
                                    log.error("Unauthenticated user");
                                    prepareAndSendMessage(telegramClient, chat.getId(), messageTextBuilder.toString());
                                })
                        .onStatus(HttpStatusCode::is2xxSuccessful,
                                (request, response) -> {

                                })
                        .body(UserDto.class);

        event.setUser(user_);
        event.setSummary(strings[0]);
        event.setStart(LocalDateTime.parse(strings[1]+" "+strings[2], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        event.setDuration(Duration.parse(strings[3]));

        var newEvent = RequestFactory.buildPost("/event")
                .body(event)
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        (request, response) -> {
                            prepareAndSendMessage(telegramClient, chat.getId(), "Unknown error.\n");
                        })
                .onStatus(HttpStatusCode::is2xxSuccessful,
                        (request, response) -> {
                            prepareAndSendMessage(telegramClient, chat.getId(), "Successfully added new event: \n");
                        })
                .body(EventDto.class);

        MessageHelpers.sendEventToChat(telegramClient, chat.getId(), newEvent);
    }
    

}
