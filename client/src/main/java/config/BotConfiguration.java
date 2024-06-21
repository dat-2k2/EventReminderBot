package config;

import bot.BotService;
import commands.HelloCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Class to retrieve private data from resource.
 */
@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = "commands")
public class BotConfiguration {
    @Autowired
    Environment env;
    // Commands are singleton, so they are beans.
    @Autowired
    HelloCommand helloCommand;
    @Bean
    public BotSession startBotSession(TelegramBotsLongPollingApplication botApplication, BotService bot) throws TelegramApiException {
//        register commands here
        bot.register(helloCommand);
        return botApplication.registerBot(env.getProperty("bot.token"), bot);
    }

    /**
     * Provide a Bean instance for bot application
     */
    @Bean
    public TelegramBotsLongPollingApplication application() {
        return new TelegramBotsLongPollingApplication();
    }

    /**
     * Provide a Bean instance for bot handler
     */
    @Bean
    public BotService handler() {
        return new BotService(env.getProperty("bot.token"), env.getProperty("bot.name"));
    }
}
