package repos;

import entity.Event;
import entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    //    JPQL, "user" keyword doesn't affect (will be mapped into user_id later)
    @Query(value = "SELECT e FROM #{#entityName} AS e WHERE e.user = :user")
    List<Event> findEvents(@Param("user") User user);

    @Query(value = "SELECT e FROM #{#entityName} AS e WHERE e.user = :user and e.start >= :start and e.start <= :end")
    List<Event> findEventsFromTo(@Param("user") User user, @Param("start")LocalDateTime start, @Param("end") LocalDateTime end);
}
