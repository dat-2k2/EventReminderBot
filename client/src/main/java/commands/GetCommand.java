package commands;

import dto.EventDto;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import utils.RequestFactory;
import utils.MessageHelpers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
public class GetCommand extends BotCommand {
    public GetCommand() {
        super(CommandId.GET, "Find all of your events by date, by time or both. Recurred events is showed as the first recurrence.\n" +
                "If no date or time is provided, get all of your events. \n"+
                "Use /" + CommandId.GET + " [date yyyy-MM-dd] + [time HH:mm] \n"+
                "Example: /find, /find 2024-06-26, /find 2024-06-26 12:00");
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] strings) {
        String date = "", time = "";
        try {
            date = LocalDate.parse(strings[0], DateTimeFormatter.ofPattern("yyyy-MM-dd")).toString();
            try {
                time = LocalTime.parse(strings[1], DateTimeFormatter.ofPattern("HH:mm")).toString();
            }
            catch (Exception e1){

            }
        }
        catch (Exception e){
            try {
                date = LocalDate.parse(strings[1], DateTimeFormatter.ofPattern("yyyy-MM-dd")).toString();
            }
            catch (Exception e1){
                try {
                    time = LocalTime.parse(strings[0], DateTimeFormatter.ofPattern("HH:mm")).toString();
                }
                catch (Exception e2){

                }
            };
        }

        var events = RequestFactory.buildGet("/event/find?userId="+user.getId()+"&date="+date+"&time="+time)
                .retrieve()
                .body(EventDto[].class);

        if (events.length == 0){
            MessageHelpers.prepareAndSendMessage(telegramClient, chat.getId(), "You don't have any events");
        }
        for (var event: events){
            MessageHelpers.sendEventToChat(telegramClient, chat.getId(), event);
        }
    }
}
