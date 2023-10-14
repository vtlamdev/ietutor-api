package ie303.ietutorapi.repositories;

import ie303.ietutorapi.models.Plan;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlanRepository extends MongoRepository<Plan, String> {
}
