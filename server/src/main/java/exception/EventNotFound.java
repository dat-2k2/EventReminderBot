package exception;

import lombok.Getter;

@Getter
public class EventNotFound extends Exception {
    private final long eventId;
    public EventNotFound(String message, long eventId) {
        super(message);
        this.eventId = eventId;
    }

    public EventNotFound(long eventId) {
        super();
        this.eventId = eventId;
    }
}
