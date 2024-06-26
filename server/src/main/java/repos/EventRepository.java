package repos;

import entity.Event;
import entity.RepeatType;
import entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    //    JPQL, "user" keyword doesn't affect (will be mapped into user_id later)
    @Query(value = "SELECT e FROM #{#entityName} AS e WHERE e.user = :user")
    List<Event> findEvents(@Param("user") User user);

    @Query(value = "SELECT e FROM #{#entityName} AS e WHERE e.user = :user and e.start >= :start and e.start <= :end")
    List<Event> findEventsFromTo(@Param("user") User user, @Param("start")LocalDateTime start, @Param("end") LocalDateTime end);

    @Modifying
    @Transactional
    @Query(value = "UPDATE #{#entityName} SET summary = :summary WHERE id = :id")
    void updateSummary(@Param("id") long eventId, @Param("summary") String summary);
    @Modifying
    @Transactional
    @Query(value = "UPDATE #{#entityName} SET start = :start WHERE id = :id")
    void updateStart(@Param("id") long eventId, @Param("start") LocalDateTime start);
    @Modifying
    @Transactional
    @Query(value = "UPDATE #{#entityName} SET duration = :duration WHERE id = :id")
    void updateDuration(@Param("id") long eventId, @Param("duration") Duration duration);
    @Modifying
    @Transactional
    @Query(value = "UPDATE #{#entityName} SET repeat = :repeat WHERE id = :id")
    void updateRepeat(@Param("id") long eventId, @Param("repeat") RepeatType repeat);

}
