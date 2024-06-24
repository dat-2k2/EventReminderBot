package commands;

import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class ScheduleCommand extends BotCommand {
    public ScheduleCommand() {
        super(CommandId.ADD, "Add an event with these fields:\n"
                + "Start date: Date of the event");
    }
    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] strings) {

    }
}
