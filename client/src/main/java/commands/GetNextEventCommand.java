package commands;

import dto.EventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import utils.MessageHelpers;
import utils.RequestFactory;
import utils.TimeHelpers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;

import static utils.TimeHelpers.getNextRecurrenceTime;

@Component
@Slf4j
public class GetNextEventCommand extends BotCommand {
    public GetNextEventCommand() {
        super(CommandId.NEXT,
                "Get next event\n");
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] strings) {

        // first day
        LocalDateTime now = LocalDateTime.now(TimeHelpers.ZONE);

        EventDto[] all = RequestFactory.buildGet(
                        String.format("/event/find?userId=%d", user.getId()))
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals,
                        (request, response) -> {
                            MessageHelpers.prepareAndSendMessage(
                                    telegramClient,chat.getId(), "Unauthenticated user");
                        })
                .onStatus(HttpStatusCode::isError,
                        (request, response) -> {
                            MessageHelpers.prepareAndSendMessage(
                                    telegramClient,chat.getId(), "Server error: cannot get the event");
                        })
                .body(EventDto[].class);

        assert all != null;
        var nearest = Arrays.stream(all)
                .filter(eventDto -> getNextRecurrenceTime(eventDto, now) != null)
                .min(Comparator.comparing(eventDto -> getNextRecurrenceTime(eventDto, now)));

        if (nearest.isEmpty()){
            MessageHelpers.prepareAndSendMessage(telegramClient, chat.getId(), "You don't have any coming event");
            return;
        }

        MessageHelpers.sendEventToChat(telegramClient, chat.getId(), nearest.get());
    }


}
