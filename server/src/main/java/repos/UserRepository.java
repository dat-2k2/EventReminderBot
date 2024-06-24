package repos;

import entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
//    Bind directly, no need to reload domain class every time using query.
//    @Query takes precedence over queries tagged with @NamedQuery.
}
