package commands;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.IOException;

@Component
@Slf4j
public class StartCommand extends BotCommand {

    public StartCommand() {
        super(CommandId.TEST, "test");
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] strings) {

//        var result = RestClient.builder()
//                .build()
//                .get()
//                .uri("http://localhost:8080/api/event/get?eventId=123")
//                .retrieve().onStatus(new ResponseErrorHandler() {
//                    @Override
//                    public boolean hasError(ClientHttpResponse response) throws IOException {
//                        return response.getStatusCode() != HttpStatus.OK;
//                    }
//
//                    @Override
//                    public void handleError(ClientHttpResponse response) throws IOException {
//                        System.out.println(response.getStatusText());
//                    }
//                });

        var result = RestClient.builder()
                .build()
                .post()
                .uri("http://localhost:8080/api/event/add")
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        new JSONObject()
                                .put("user-id", user.getId())
                                .put("date", "2023-01-01")
                                .put("time", "00:01")
                                .put("duration", 10)
                                .toString()
                ).retrieve().onStatus(new ResponseErrorHandler() {
                    @Override
                    public boolean hasError(ClientHttpResponse response) throws IOException {
                        return response.getStatusCode() != HttpStatus.OK;
                    }

                    @Override
                    public void handleError(ClientHttpResponse response) throws IOException {
                        System.out.println(response.getStatusText());
                        System.out.println(response.getBody());
                    }
                });

        System.out.println(result.body(String.class));
    }
}
