package entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "event")
@Data
@NoArgsConstructor
public class Event {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String summary;

    @Column(nullable = false)
    private LocalDateTime start;

    @Column(nullable = false)
    private Duration duration;

    @Enumerated(EnumType.ORDINAL)
    private RepeatType repeat = RepeatType.NONE;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
