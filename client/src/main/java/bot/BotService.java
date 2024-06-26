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
import utils.DurationParser;
import utils.RequestFactory;
import utils.SendMessageUtils;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static utils.SendMessageUtils.prepareAndSendMessage;


/**
 * The server holds the request open till new data comes.
 */
@Slf4j
public class BotService extends CommandLongPollingTelegramBot {
    static Clock clock = Clock.system(ZoneId.of("Europe/Moscow"));

    private Map<Long, LocalDateTime> history = new HashMap<>();
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
        SendMessageUtils.prepareAndSendMessage(telegramClient, update.getMessage().getChatId(), "Invalid comment. See " + CommandId.HELP);
    }

    /**
     * This method will create an Executor to execute the function.
     */
    @Scheduled(fixedRate = 997)
    private void notice(){

        LocalDateTime currentDateTime = LocalDateTime.now();
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
        System.out.println(Arrays.toString(allUsers));
        if (allUsers == null)
            return;

        Arrays.stream(allUsers).forEach((user) ->{
            var messageBuilder = new StringBuilder();
            var eventsNow = RequestFactory
                    .buildGet(String.format("/event/range?userId=%d&start=%s&end=%s"
                            ,user.getId(),currentDateTime, endDateTime))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError,
                            (request, response) -> {
                                log.error("Cannot get all events of user with id " + user.getId() + " from "+ currentDateTime + " to " + endDateTime);
                            })
                    .body(EventDto[].class);
            if (eventsNow == null)
                return ;

            messageBuilder.append("You have up comming events:\n");
            var upcoming = Arrays.stream(eventsNow).filter(eventDto ->
                            !history.containsKey(eventDto.getId()) ||
                                    history.get(eventDto.getId()).isBefore(currentDateTime))
                    .map(event -> {
                        messageBuilder
                                .append("At ")
                                .append(event.getStart().format(formatter))
                                .append(" in ")
                                .append(DurationParser.beautify(event.getDuration()))
                                .append(": ")
                                .append(event.getSummary())
                        ;
//                        Mark this event to be noticed after 1 hour
                        history.put(event.getId(), event.getStart().plusHours(1));
                        return event;
                    }).toList();
            if (!upcoming.isEmpty())
                prepareAndSendMessage(this.telegramClient, user.getId(), messageBuilder.toString());
        });
    }


}
