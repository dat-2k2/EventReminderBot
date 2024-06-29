package commands;

import dto.EventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import utils.MessageHelpers;
import utils.RequestFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
@Component
@Slf4j
public class GetTodayEventsCommand extends BotCommand {
    public GetTodayEventsCommand() {
        super(CommandId.TODAY, "Get all your events today.");
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] strings) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        var events = RequestFactory.buildGet("/event/find?userId="+user.getId()+"&date="+today)
                .retrieve()
                .body(EventDto[].class);

        if (events.length == 0){
            MessageHelpers.prepareAndSendMessage(telegramClient, chat.getId(),
                    "You have no events today.");
        }

        for (var event: events){
            MessageHelpers.sendEventToChat(telegramClient, chat.getId(), event);
        }
    }
}
