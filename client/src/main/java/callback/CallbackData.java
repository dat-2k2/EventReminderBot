package callback;

import dto.RepeatTypeDto;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public sealed interface CallbackData permits DeleteCallback, RescheduleCallback, SendScheduleOptionCallback {

    String callbackPhrase();
    void execute(TelegramClient client, User user);
    static CallbackData parse(String s){
        var parts = s.split(" ");
        switch (parts[0]){
            case "reschedule":  return new RescheduleCallback(Long.parseLong(parts[1]), Long.parseLong(parts[2]), RepeatTypeDto.valueOf(parts[3]));
            case "schedule": return new SendScheduleOptionCallback(Long.parseLong(parts[1]),Long.parseLong(parts[2]));
            case "delete": return new DeleteCallback(Long.parseLong(parts[1]),Long.parseLong(parts[2]));
            default: throw new IllegalArgumentException("Cannot parse " + s + "to callback query");
        }
    };
}
