package config;

import bot.BotService;
import commands.*;
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
    AddCommand addCommand;
    @Autowired
    RegisterCommand registerCommand;
    @Autowired
    GetCommand getCommand;
    @Autowired
    GetEventsWeekCommand getEventsWeekCommand;
    @Autowired
    GetTodayEventsCommand getTodayEventsCommand;
    @Autowired
    UpdateStartCommand updateStartCommand;
    @Autowired
    UpdateSummaryCommand updateSummaryCommand;
    @Autowired
    UpdateDurationCommand updateDurationCommand;
    HelpCommand helpCommand;
    @Bean
    public BotSession startBotSession(TelegramBotsLongPollingApplication botApplication, BotService bot) throws TelegramApiException {
        this.helpCommand = new HelpCommand(bot);
//        register commands here
        bot.registerAll(
//                loginCommand,
                registerCommand,
                addCommand,
                getCommand,
                helpCommand,
                getTodayEventsCommand,
                getEventsWeekCommand,
                updateStartCommand,
                updateSummaryCommand,
                updateDurationCommand
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
