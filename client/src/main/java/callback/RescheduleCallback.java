package callback;

import dto.EventDto;
import dto.RepeatTypeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import utils.RequestFactory;
import utils.MessageHelpers;

@Slf4j
public record RescheduleCallback(long chatId, long eventId, RepeatTypeDto type) implements CallbackData {

    @Override
    public String callbackPhrase() {
        return "reschedule " +chatId+" " + eventId+" "+type;
    }

    public void onSuccess(TelegramClient client, EventDto event) {
        MessageHelpers.sendEventToChat(client, chatId, event);
        log.info("Change repeat type of event " + event.getId() + " to " + event.getRepeat());
    }

    @Override
    public void execute(TelegramClient client, User user) {
        var _event = new EventDto();
        _event.setRepeat(type);

        var newEvent = RequestFactory.buildPut("/event/" + eventId)
                .body(_event)
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        (request, response) -> {
                            MessageHelpers.prepareAndSendMessage(client, chatId, "Cannot update event with id" + eventId);
                            log.error("Cannot change repeat type of event " + eventId + " to " + type);
                        })
                .onStatus(HttpStatusCode::is2xxSuccessful,
                        (request, response) -> {
                            System.out.println(response);
                        })
                .body(EventDto.class);

        onSuccess(client, newEvent);
    }
}
