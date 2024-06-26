package utils;

import callback.DeleteCallback;
import callback.SendScheduleOptionCallback;
import dto.EventDto;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Slf4j
public class SendMessageUtils {

    public static void prepareAndSendMessage(TelegramClient client, long chatId, String textToSend){
        SendMessage message = new SendMessage(String.valueOf(chatId), textToSend);
        try {
            client.execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public static void sendMessage(TelegramClient client, SendMessage message){
        try {
            client.execute(message);
        } catch (TelegramApiException e) {
            System.out.println("Cannot send "+message.getText());
            log.error(e.getMessage());
        }
    }

    public static void sendEventToChat(TelegramClient client, long chatId, EventDto event){
        InlineKeyboardButton reschedule = new InlineKeyboardButton("Reschedule");
        reschedule.setCallbackData(new SendScheduleOptionCallback(chatId, event.getId()).callbackPhrase());
        InlineKeyboardButton deleteButton = new InlineKeyboardButton("Delete");
        deleteButton.setCallbackData(new DeleteCallback(chatId, event.getId()).callbackPhrase());

        InlineKeyboardRow row = new InlineKeyboardRow(reschedule, deleteButton);

        var msg = new SendMessage(String.valueOf(chatId), event.toMessage());
        msg.setReplyMarkup(new InlineKeyboardMarkup(List.of(row)));

        SendMessageUtils.sendMessage(client, msg);
    }

    /**
     * Format 2D3H4M
     * @param input
     * @return
     */
//    public static void main(String[] args){
//
//        Duration duration;
//        try{
//            System.out.println(Duration.parse("3Y2M"));
//        }
//        catch (DateTimeParseException e){
//            System.out.println(Duration.parse("P3Y2M"));
//        }
//
//    }
//    public static String durationParser(String input){
//
//    }
}
