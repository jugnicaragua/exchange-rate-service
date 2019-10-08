package ni.org.jug.exchangerate.repository;

import ni.org.jug.exchangerate.model.UserSubscription;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UserSubscriptionRepository extends CrudRepository<UserSubscription, Integer> {

    UserSubscription findByEmail(String email);

    @Query("SELECT u FROM UserSubscription u WHERE u.active = true AND NOT EXISTS(SELECT task FROM EmailTask task WHERE " +
            "task.userSubscription = u AND date >= :start AND date < :end)")
    List<UserSubscription> findActiveSubsciptionsWithNoEmailSent(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}
