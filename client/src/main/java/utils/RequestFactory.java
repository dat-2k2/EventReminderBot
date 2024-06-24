package utils;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public final class RequestFactory {
    private static final RestClient client = RestClient.create("http://localhost:8080/api");

    private RequestFactory(){
    }

    public static RestClient.RequestBodySpec buildPost(String uri){
        return client.post().uri(uri).contentType(MediaType.APPLICATION_JSON);
    }

    public static RestClient.RequestHeadersSpec<?> buildGet(String uri){
        return client.get().uri(uri);
    }
}
