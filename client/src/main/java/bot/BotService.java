package bot;

import callback.CallbackData;
import commands.CommandId;
import dto.EventDto;
import dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.Scheduled;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.extensions.bots.commandbot.CommandLongPollingTelegramBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import utils.TimeHelpers;
import utils.RequestFactory;
import utils.MessageHelpers;

import java.sql.Time;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static utils.MessageHelpers.prepareAndSendMessage;


/**
 * The server holds the request open till new data comes.
 */
@Slf4j
public class BotService extends CommandLongPollingTelegramBot {
    static Clock clock = Clock.system(ZoneId.of("Europe/Moscow"));
    private static Map<Long, LocalDateTime> history = new HashMap<>();
    private static final String ERROR_TEXT = "Error when sending message: ";
    //    services
//    private final EventService events;

    public BotService(String botToken, String botUsername) {
        super(new OkHttpTelegramClient(botToken),
                true,
                () -> botUsername);
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        if (update.hasCallbackQuery()){
            var callbackQueryData = update.getCallbackQuery().getData();
            System.out.println(callbackQueryData);
            CallbackData.parse(callbackQueryData).execute(telegramClient, update.getCallbackQuery().getFrom());
        }

    }

    @Override
    public void processInvalidCommandUpdate(Update update) {
        MessageHelpers.prepareAndSendMessage(telegramClient, update.getMessage().getChatId(), "Invalid comment. See " + CommandId.HELP);
    }

    /**
     * This method will create an Executor to execute the function.
     */
    @Scheduled(fixedRate = 120)
    private void notice(){
        LocalDateTime currentDateTime = LocalDateTime.now(ZoneId.of("Europe/Moscow"));
        LocalDateTime endDateTime = currentDateTime.plusHours(2);
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        var allUsers = RequestFactory.buildGet("/users")
                .retrieve()
                .onStatus(HttpStatusCode::is2xxSuccessful,
                        (request, response) -> {

                        })
                .onStatus(HttpStatusCode::isError,
                        ((request, response) -> {
                            log.error("Cannot retrieve all users: "  + response.getStatusText());
                        }))
                .body(UserDto[].class);
        if (allUsers == null)
            return;

        Arrays.stream(allUsers).forEach((user) ->{
            var messageBuilder = new StringBuilder();
            var eventsNow = RequestFactory
                    .buildGet(String.format("/event/find?userId=%d"
                            ,user.getId()))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError,
                            (request, response) -> {
                                log.error("Cannot get all events of user with id " + user.getId() + " from "+ currentDateTime + " to " + endDateTime);
                            })
                    .body(EventDto[].class);
            if (eventsNow == null)
                return ;
            messageBuilder.append("You have upcoming events:\n");
            var upcoming = Arrays.stream(eventsNow)
                    .filter(eventDto -> {
                        var next = TimeHelpers.getNextRecurrenceTime(eventDto, currentDateTime);
                        //If the next recurrence is after the endDateTime, no need to remind yet
                        if(next == null || next.isAfter(endDateTime)){
                            history.remove(eventDto.getId());
                        }
                        return (next != null && next.isAfter(currentDateTime)) &&
                                (next.isBefore(endDateTime) || next.isEqual(endDateTime));

                    })
                    .filter(eventDto ->
//                    Either not remind yet, or the current schedule is coming
                            !history.containsKey(eventDto.getId()) ||
                                    history.get(eventDto.getId()).isEqual(currentDateTime)
                    )
                    .map(event -> {
                        messageBuilder
                                .append("At ")
                                .append(
                                        TimeHelpers.getRecurrenceTimeNearThisDay(event, currentDateTime).format(formatter)
                                )
                                .append(" in ")
                                .append(TimeHelpers.beautify(event.getDuration()))
                                .append(": ")
                                .append(event.getSummary())
                        ;
//                        Mark this event to be noticed after 15 minutes
                        history.put(event.getId(), currentDateTime.plusMinutes(15));
//                        If the next recurrence happens before the next scheduled remind, set it as the current
                        var next = TimeHelpers.getNextRecurrenceTime(event, currentDateTime);

                        if(next != null && next.isBefore(history.get(event.getId()))){
                            history.put(event.getId(), next);
                        }

                        return event;
                    }).toList();
            if (!upcoming.isEmpty())
                prepareAndSendMessage(this.telegramClient, user.getId(), messageBuilder.toString());
        });
    }
}
