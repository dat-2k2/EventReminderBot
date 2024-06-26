package utils;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public final class RequestFactory {
    private static final String baseUrl = "http://server:8080/api";
    public static RestClient.RequestBodySpec buildPost(String uri){
        return RestClient.create(baseUrl).post().uri(uri).contentType(MediaType.APPLICATION_JSON);
    }

    public static RestClient.RequestHeadersSpec<?> buildGet(String uri){
        return RestClient.create(baseUrl).get().uri(uri);
    }
    public static RestClient.RequestHeadersSpec<?> buildDelete(String uri){
        return RestClient.create(baseUrl).delete().uri(uri);
    }
    public static RestClient.RequestBodySpec buildPut(String uri){
        return RestClient.create(baseUrl).put().uri(uri).contentType(MediaType.APPLICATION_JSON);
    };
    
}
