package commands;

import dto.EventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import utils.MessageHelpers;
import utils.TimeHelpers;

@Component
@Slf4j
public class UpdateDurationCommand extends AbstractUpdateCommand{
//    TODO: add rule for duration
    public UpdateDurationCommand() {
        super(CommandId.UPDATE_DURATION,
                "Update new duration for event. Duration needs to follow ISO 8061. \n"
                        +"Use: /"+CommandId.UPDATE_DURATION + " {id of event} {new duration}.\n"
        + "Example: /"+CommandId.UPDATE_DURATION + " 1 PT10H");
    }

    @Override
    protected EventDto newEvent(String newData) {
        var event = new EventDto();
        event.setDuration(TimeHelpers.parse(newData));
        return event;
    }

    @Override
    protected void onSuccess(TelegramClient client, User user, Chat chat, EventDto event) {
        MessageHelpers.prepareAndSendMessage(client, chat.getId(), event.toString());
        log.info("Change duration of event " + event.getId() + " to " + event.getDuration());
    }

    @Override
    protected void onError(TelegramClient client, User user, Chat chat, EventDto event) {

    }

}
