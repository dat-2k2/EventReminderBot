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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class GetEventsWeekCommand extends BotCommand {
    public GetEventsWeekCommand() {
        super(CommandId.WEEK,
                "Get all events in this week\n");
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] strings) {

         // first day
        LocalDate start = LocalDate.now(ZoneId.of("Europe/Moscow")).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)); ;
        LocalDate end = LocalDate.now(ZoneId.of("Europe/Moscow")).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)); ;;

        LocalDate tmp = start;
        List<EventDto> all = new ArrayList<>();

        while(tmp.isBefore(end.plusDays(1))){
            var events = RequestFactory.buildGet(
                            String.format("/event/find?userId=%d&date=%s", user.getId(), tmp))
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
            if (events==null)
                continue;

            all.addAll(Arrays.stream(events).filter(eventDto -> !all.contains(eventDto)).toList());
            tmp = tmp.plusDays(1);
        }

        if (all.isEmpty()){
            MessageHelpers.prepareAndSendMessage(
                    telegramClient,chat.getId(), "You don't have any events between these days.");
        }

        all.forEach(event ->{
            MessageHelpers.sendEventToChat(telegramClient, chat.getId(), event);
        });
    }
}
