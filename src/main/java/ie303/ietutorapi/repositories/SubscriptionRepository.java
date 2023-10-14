package ie303.ietutorapi.repositories;

import ie303.ietutorapi.models.Subscription;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface SubscriptionRepository extends MongoRepository<Subscription, String> {
    List<Subscription> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
    List<Subscription> findByCreatedAtBetween(LocalDate startDate, LocalDate endDate);
}
