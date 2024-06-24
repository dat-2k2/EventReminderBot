package commands;

import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import utils.RequestFactory;

public class GetCommand extends BotCommand {
    public GetCommand() {
        super(CommandId.GET, "find events");
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] strings) {
        RequestFactory.buildGet("/event/find")
                .retrieve();
    }
}
