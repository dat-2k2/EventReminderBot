package entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

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
    private Instant start;

    @Column(nullable = false)
    Long duration;

    @Enumerated(EnumType.ORDINAL)
    private RepeatType repeat = RepeatType.NONE;

    private String summary;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
