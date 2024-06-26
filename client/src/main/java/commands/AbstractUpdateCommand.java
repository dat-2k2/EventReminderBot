package commands;

import dto.EventDto;
import org.springframework.http.HttpStatusCode;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import utils.RequestFactory;
import utils.SendMessageUtils;

public abstract class AbstractUpdateCommand extends BotCommand {
    public AbstractUpdateCommand(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }

    protected abstract EventDto newEvent(String newData);
    protected abstract void onSuccess(TelegramClient client, User user, Chat chat, EventDto event);
    protected abstract void onError(TelegramClient client, User user, Chat chat, EventDto event);

    @Override
    public void execute(TelegramClient client, User user, Chat chat, String[] strings) {
        EventDto _event;

        try {
            Long.parseLong(strings[0]);
        }
        catch (Exception e){
            SendMessageUtils.prepareAndSendMessage(client, chat.getId(),"Require event id ");
        }

        try {
             _event = newEvent((strings.length == 2)?strings[1]:(strings[1]+" " + strings[2]));
        }
        catch (Exception e){
            SendMessageUtils.prepareAndSendMessage(client, chat.getId(),"Cannot parse new data field "+strings[1]+ "\n" +
                    "For start, requires \"yyyy-MM-dd HH:mm\"; for duration, requires ISO 8601 format.");
            return;
        }

        var newEvent = RequestFactory.buildPut("/event/"+strings[0])
                .body(_event)
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        (request, response) -> onError(client, user, chat, _event))
                .body(EventDto.class);
        onSuccess(client, user, chat, newEvent);
    }

}
