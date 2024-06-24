package config;

import bot.BotService;
import commands.AddCommand;
import commands.LoginCommand;
import commands.RegisterCommand;
import commands.StartCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Class to retrieve private data from resource.
 */
@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = "commands")
@EnableScheduling
public class BotConfiguration {
    @Autowired
    Environment env;
    // Commands are singleton, so they are beans.
    @Autowired
    LoginCommand loginCommand;
    @Autowired
    StartCommand startCommand;
    @Autowired
    AddCommand addCommand;
    @Autowired
    RegisterCommand registerCommand;

    @Bean
    public BotSession startBotSession(TelegramBotsLongPollingApplication botApplication, BotService bot) throws TelegramApiException {
//        register commands here
        bot.registerAll(
                loginCommand,
                registerCommand,
                startCommand,
                addCommand
        );
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
