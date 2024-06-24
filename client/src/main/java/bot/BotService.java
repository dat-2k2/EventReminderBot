package bot;

import dto.EventDto;
import dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.Scheduled;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.extensions.bots.commandbot.CommandLongPollingTelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import utils.RequestFactory;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static utils.SendMessageUtils.prepareAndSendMessage;


/**
 * The server holds the request open till new data comes.
 */
@Slf4j
public class BotService extends CommandLongPollingTelegramBot {
    static Clock clock = Clock.system(ZoneId.of("Europe/Moscow"));

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
        if (update.hasMessage()) {
            var message = update.getMessage();
            if (message.hasText()) {
                SendMessage echoMessage = new SendMessage(String.valueOf(message.getChatId()),
                        "Hey heres your message:\n" + message.getText());
                try {
                    telegramClient.execute(echoMessage);
                } catch (TelegramApiException e) {
                    log.error("Error processing non-command update", e);
                }
            }
        }
    }

    /**
     * This method will create an Executor to execute the function.
     */
    @Scheduled(fixedRate = 997)
    private void notice(){

        LocalDateTime currentDateTime = LocalDateTime.now().plusHours(30);
        System.out.println(currentDateTime);
        LocalDate currentDate = LocalDate.parse(currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        LocalTime currentTime = LocalTime.parse(currentDateTime.format(DateTimeFormatter.ofPattern("HH:mm")));

        RequestFactory.buildGet("http://localhost:8080/api/users").retrieve().toBodilessEntity();
        System.out.println("Running");
        var allUsers = RequestFactory.buildGet("/users")
                .retrieve()
                .onStatus(HttpStatusCode::is2xxSuccessful,
                        (request, response) -> System.out.println("Running"))
                .onStatus(HttpStatusCode::isError,
                        ((request, response) -> {
                            System.out.println("Error querying");
                            log.error("Cannot retrieve all users: "  + response.getStatusText());
                        }))
                .body(UserDto[].class);
        if (allUsers == null)
            return;

        Arrays.stream(allUsers).parallel().map((user) ->{
            var messageBuilder = new StringBuilder();
            var eventsNow = RequestFactory
                    .buildGet(String.format("/event/find?userId={%d}&date={%s}&time={%s}"
                            ,user.getId(),currentDate.toString(), currentTime.toString()))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError,
                            (request, response) -> {
                                System.out.println("error querying");
                                log.error("Cannot get all events of user with id " + user.getId() + " at "+ currentDate + currentTime);
                            })
                    .body(EventDto[].class);
            if (eventsNow == null)
                return user;

            for (var event: eventsNow){
                messageBuilder
                        .append("At ")
                        .append(event.getStart())
                        .append("in ")
                        .append(event.getDuration())
                        .append(": ")
                        .append(event.getSummary())
                        ;
                prepareAndSendMessage(this.telegramClient, user.getId(), messageBuilder.toString());
            }
            return user;
        });
    }



}
