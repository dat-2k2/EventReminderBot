package commands;

import lombok.Setter;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.ICommandRegistry;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import utils.SendMessageUtils;


@Setter
public class HelpCommand extends BotCommand {
    private ICommandRegistry commandRegistry;
    public HelpCommand(ICommandRegistry commandRegistry) {
        super(CommandId.HELP,"Show all commands and usage.");
        this.commandRegistry = commandRegistry;
    }


    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] strings) {
        var messageBuilder = new StringBuilder();

        for (IBotCommand botCommand: commandRegistry.getRegisteredCommands()){
            messageBuilder.append(botCommand.toString()).append("\n\n");
        }

        SendMessage msg = new SendMessage(String.valueOf(chat.getId()) , messageBuilder.toString());
        msg.enableHtml(true);

        SendMessageUtils.sendMessage(telegramClient, msg);
    }
}
