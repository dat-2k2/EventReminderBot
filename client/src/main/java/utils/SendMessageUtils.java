package utils;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

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
}
