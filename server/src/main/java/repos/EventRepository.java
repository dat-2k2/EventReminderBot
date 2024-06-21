package repos;

import entity.Event;
import entity.RepeatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
//    JPQL, "user" keyword doesn't affect (will be mapped into user_id later)
    @Query(value = "SELECT e FROM #{#entityName} AS e WHERE e.user = :userId order by start")
    List<Event> getEventsByUserId(@Param("userId") long userId);

    @Query(value = "UPDATE #{#entityName} e set e.repeat = :#{#repeatType.name()} where e.id = :eventId")
    void changeEventRepeatType(@Param("eventId") long eventId, @Param("repeatType") RepeatType repeatType);

}
