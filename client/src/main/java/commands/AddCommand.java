package commands;

import dto.EventDto;
import dto.UserDto;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import utils.RequestFactory;
import utils.SendMessageUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static utils.SendMessageUtils.prepareAndSendMessage;

@Component
@Slf4j
public class AddCommand extends BotCommand {
    public AddCommand() {
        super(CommandId.ADD, "Add an event with these fields, separated by whitespace:\n"
        + "[Summary of event] [Date of event (yyyy-MM-dd)] [Time of event (HH:mm)] [Duration of event (ISO-8601)]");
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

        var newEvent = RequestFactory.buildPost("/event/add")
                .body(event)
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        (request, response) -> {
                            messageTextBuilder.append("Unknown error.\n");
                            prepareAndSendMessage(telegramClient, chat.getId(), messageTextBuilder.toString());
                        })
                .onStatus(HttpStatusCode::is2xxSuccessful,
                        (request, response) -> {
                            messageTextBuilder.append("Successfully added new event: \n");
                        })
                .body(EventDto.class);

        messageTextBuilder.append(newEvent);
        prepareAndSendMessage(telegramClient, chat.getId(), messageTextBuilder.toString());
    }

}
