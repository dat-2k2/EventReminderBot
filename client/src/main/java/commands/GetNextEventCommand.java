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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

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
        LocalDateTime now = LocalDateTime.now();

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
        var valid = Arrays.stream(all).filter(eventDto -> getNextRecurrenceTime(eventDto, now) != null).toList();

        if (valid.isEmpty()){
            MessageHelpers.prepareAndSendMessage(telegramClient, chat.getId(), "You don't have any coming event");
            return;
        }

        EventDto nearest = valid.get(0);
        LocalDateTime recurredTime  = getNextRecurrenceTime(nearest, now);
        for (var e: all){
//            calculate the nearest moment of this event:
            LocalDateTime next = getNextRecurrenceTime(nearest, now);
            if (next.isAfter(now) && next.isBefore(nearest.getStart())){
                nearest = e;
                recurredTime = next;
            }
        }

        MessageHelpers.sendEventToChat(telegramClient, chat.getId(), nearest);
    }


}
