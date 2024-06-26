package utils;

import java.util.Optional;

public class JwtTokenizer {
    public boolean validate(String token) {
        return true;
    }

    public Optional<Object> get(String token) {
        return Optional.ofNullable(token);
    }
}
