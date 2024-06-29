package dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import utils.TimeHelpers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
public class EventDto {
    private long id;
    private String summary;
    private LocalDateTime start;
    private Duration duration;
    private RepeatTypeDto repeat = RepeatTypeDto.NONE;
    private UserDto user;

    public String toMessage(){
        return "Event ID: " + id + "\n" +
                "Summary: " + summary + "\n" +
                "Start at " + start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "\n" +
                "Duration: " + TimeHelpers.beautify(duration) +
                "\n" +
                "Repeat: " + repeat;
    }

    @Override
    public boolean equals(Object o){
        return (o.getClass().equals(EventDto.class) && ((EventDto) o).id == this.id);
    }

}
