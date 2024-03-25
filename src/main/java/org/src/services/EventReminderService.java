package org.src.services;

import org.springframework.stereotype.Component;
import org.src.config.BotConfiguration;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

/**
 * The server holds the request open till new data comes.
 */
@Component
public class EventReminderService extends TelegramLongPollingBot {

    private static final String START = "/start";
    private static final String CREATE = "/create";
    private static final String READ = "/read";
    private static final String UPDATE = "/update";
    private static final String DELETE = "/delete";


    /**
     * Config data for bot
     */
    final BotConfiguration config;

    public EventReminderService(BotConfiguration config){
        this.config = config;

//        router for telegram commands
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand(START, "get a welcome message"));

//        TODO: add more commands
//        listOfCommands.add(new BotCommand("/mydata", "get your data stored"));
//        listOfCommands.add(new BotCommand("/deletedata", "delete my data"));
//        listOfCommands.add(new BotCommand("/help", "info how to use this bot"));
//        listOfCommands.add(new BotCommand("/settings", "set your preferences"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
//            TODO: Log
//            log.error("Error setting bot's command list: " + e.getMessage());
        }

    }

    /**
     * Message handler
     * @param update the update event of coming message.
     */
    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

//TODO: add send message
//            if(messageText.contains("/send") && config.getOwnerId() == chatId) {
//                var textToSend = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
//                var users = userRepository.findAll();
//                for (User user: users){
//                    prepareAndSendMessage(user.getChatId(), textToSend);
//                }
//            }

            //TODO: add command handlers

            switch (messageText){
                //TODO: add start handlers
                case START :
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;

                default:
                    sendMessage(chatId, "Very sorry, we don't serve this request");
            }
        } else if (update.hasCallbackQuery()){ // handling callback query (for example from a button)
            String callbackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if(callbackData.equals(YES_REGISTER_BUTTON)){
                String text = "You pressed YES button";
                executeEditMessageText(text, chatId, messageId);
            }
            else if(callbackData.equals(NO_REGISTER_BUTTON)){
                String text = "You pressed NO button";
                executeEditMessageText(text, chatId, messageId);
            }
        }
    }

    private void executeEditMessageText(String text, long chatId, long messageId){
        EditMessageText message = new EditMessageText();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setMessageId((int) messageId);

        try {
            this.execute(message);
        } catch (TelegramApiException e) {
//            TODO: log
//            log.error(ERROR_TEXT + e.getMessage());
        }
    }


    /**
     * Handle starting command with chat id and first name of the sender
     * @param chatId id of the chat session (?)
     * @param name first name of the sender
     */
    private void startCommandReceived(long chatId, String name){
        String answer = "Hello, this bot is running";
        sendMessage(chatId, answer);
    }

    /**
     * Send a message with certain chat id and text
     * @param chatId
     * @param textToSend
     */
    private void sendMessage(long chatId, String textToSend){
        var message = SendMessage.builder().chatId(chatId).build();


        try {
            execute(message);
        }
        catch (TelegramApiException e){
            //TODO: handle message exception
        }
    }

    @Override
    public String getBotUsername() {
        return this.config.getBotName();
    }

//    TODO: move this to an AOP package
    void register(long chatId){
//        Prepare a response message
        var message = SendMessage.builder().chatId(chatId).build();
//        Make sure that the user wants to register
        message.setText("Do you really want to register?");

//        Create a button panel for choices

        var rowsInLine = getLists();
        var markupInLine = new InlineKeyboardMarkup(rowsInLine);


        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        executeMessage(message);
    }

    private void executeMessage(SendMessage message){
        try {
            execute(message);
        } catch (TelegramApiException e) {
//            TODO: add log
//            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    private List<List<InlineKeyboardButton>> getLists() {
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var yesButton = new InlineKeyboardButton("Yes");

        yesButton.setCallbackData(YES_REGISTER_BUTTON);

        var noButton = new InlineKeyboardButton("No");
        noButton.setCallbackData(NO_REGISTER_BUTTON);

        rowInLine.add(yesButton);
        rowInLine.add(noButton);

        rowsInLine.add(rowInLine);
        return rowsInLine;
    }

    private final String YES_REGISTER_BUTTON = "YES_BUTTON";
    private final String NO_REGISTER_BUTTON = "NO_BUTTON";

    @Override
    public String getBotToken() {
        return config.getToken();
    }


    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }
}
