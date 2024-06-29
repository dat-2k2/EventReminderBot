package callback;

import dto.RepeatTypeDto;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import utils.MessageHelpers;

import java.util.List;

@Slf4j
public record SendScheduleOptionCallback(long chatId, long eventId) implements CallbackData{
    @Override
    public String callbackPhrase() {
        return "schedule " +chatId +" "+eventId;
    }

    @Override
    public void execute(TelegramClient client, User user){
        InlineKeyboardButton noneChoice = new InlineKeyboardButton(RepeatTypeDto.NONE.toString());
        noneChoice.setCallbackData(new RescheduleCallback(chatId,eventId, RepeatTypeDto.NONE).callbackPhrase());
        InlineKeyboardButton hourlyChoice = new InlineKeyboardButton(RepeatTypeDto.HOURLY.toString());
        hourlyChoice.setCallbackData(new RescheduleCallback(chatId,eventId, RepeatTypeDto.HOURLY).callbackPhrase());
        InlineKeyboardButton dailyChoice = new InlineKeyboardButton(RepeatTypeDto.DAILY.toString());
        dailyChoice.setCallbackData(new RescheduleCallback(chatId,eventId, RepeatTypeDto.DAILY).callbackPhrase());
        InlineKeyboardButton weeklyChoice = new InlineKeyboardButton(RepeatTypeDto.WEEKLY.toString());
        weeklyChoice.setCallbackData(new RescheduleCallback(chatId,eventId, RepeatTypeDto.WEEKLY).callbackPhrase());
        InlineKeyboardButton monthlyChoice = new InlineKeyboardButton(RepeatTypeDto.MONTHLY.toString());
        monthlyChoice.setCallbackData(new RescheduleCallback(chatId,eventId, RepeatTypeDto.MONTHLY).callbackPhrase());

        InlineKeyboardRow row1 = new InlineKeyboardRow(noneChoice, hourlyChoice, dailyChoice);
        InlineKeyboardRow row2 = new InlineKeyboardRow(weeklyChoice, monthlyChoice);

        SendMessage message = new SendMessage(String.valueOf(chatId), "Choose schedule type:");
        message.enableHtml(true);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(row1,row2));
        message.setReplyMarkup(markup);

        MessageHelpers.sendMessage(client, message);
    }
}
