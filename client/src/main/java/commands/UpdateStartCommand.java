package commands;

import dto.EventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import utils.SendMessageUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
@Component
@Slf4j
public class UpdateStartCommand extends AbstractUpdateCommand {
    public UpdateStartCommand() {
        super(CommandId.UPDATE_START,
                "Update start date time to [date] and time to [time] of an event: \n"
                        +CommandId.UPDATE_START+" \"{date} {time}\"" +
                "Example: /"+CommandId.UPDATE_START +" 2023-01-01 12:00");
    }

    @Override
    protected EventDto newEvent(String newData) {
        var newStart = LocalDateTime.parse(newData, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        var event = new EventDto();
        event.setStart(newStart);
        return event;
    }

    @Override
    protected void onSuccess(TelegramClient client, User user, Chat chat, EventDto event) {
        SendMessageUtils.sendEventToChat(client, chat.getId(), event);
        log.info("Change start of event " + event.getId() + " to " + event.getStart());
    }

    @Override
    protected void onError(TelegramClient client, User user, Chat chat, EventDto event) {
        SendMessageUtils.prepareAndSendMessage(client, chat.getId(), "Cannot update start of event with id " + event.getId());

    }
}
