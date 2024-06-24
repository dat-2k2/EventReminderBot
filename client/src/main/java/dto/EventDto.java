package dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class EventDto {
    private long id;
    private String summary;
    private LocalDateTime start;
    private Duration duration;
    private RepeatType repeat = RepeatType.NONE;
    private UserDto user;
}
