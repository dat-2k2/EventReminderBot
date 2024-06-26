package callback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import utils.RequestFactory;
import utils.SendMessageUtils;

@Slf4j
public record DeleteCallback(long chatId, long eventId) implements CallbackData{
    @Override
    public String callbackPhrase() {
        return "delete "+chatId+" "+ this.eventId;
    }
    @Override
    public void execute(TelegramClient client, User user) {
        RequestFactory.buildDelete("/event/"+eventId)
                .retrieve()
                .onStatus(HttpStatusCode::is2xxSuccessful,
                        (request, response) -> {
                            SendMessageUtils.prepareAndSendMessage(client, chatId, "Event " + eventId + " deleted");
                            log.error("Deleted " + eventId);
                        })
                .onStatus(HttpStatusCode::isError,
                        (request, response) -> {
                            SendMessageUtils.prepareAndSendMessage(client, chatId, "Cannot delete event " + eventId);
                            log.error("Cannot delete event " + eventId);
                        })
                .toBodilessEntity();
    }
}
