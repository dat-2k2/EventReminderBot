package exception;

import lombok.Getter;

@Getter
public class UserNotFound extends Exception {
    private final long  userId;
    public UserNotFound(String message, long userId) {
        super(message);
        this.userId = userId;
    }
    public UserNotFound(long userId) {
        super();
        this.userId = userId;
    }
}
