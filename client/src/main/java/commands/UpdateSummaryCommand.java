package commands;

import dto.EventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import utils.MessageHelpers;

@Component
@Slf4j
public class UpdateSummaryCommand extends AbstractUpdateCommand {
    public UpdateSummaryCommand() {
        super(CommandId.UPDATE_SUMMARY, "Update summary of an event. Summary needs to be without whitespace.\n" +
                "Example /"+CommandId.UPDATE_SUMMARY + " 252 A-new-summary\"");
    }

    @Override
    protected EventDto newEvent(String arg) {
        var event = new EventDto();
        event.setSummary(arg);
        return event;
    }

    @Override
    public void onSuccess(TelegramClient client, User user, Chat chat, EventDto event) {
        MessageHelpers.sendEventToChat(client, chat.getId(), event);
        log.info("Change summary of event " + event.getId() + " to " + event.getSummary());
    }

    public void onError(TelegramClient client, User user, Chat chat, EventDto event) {
        MessageHelpers.prepareAndSendMessage(client, chat.getId(), "Cannot update event " + event.getId());
        log.error("Cannot change repeat type of event " + event.getId() + " to " + event.getRepeat());
    }

}
