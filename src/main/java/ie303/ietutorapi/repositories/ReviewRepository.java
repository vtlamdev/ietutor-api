package ie303.ietutorapi.repositories;

import ie303.ietutorapi.models.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {
    // Tìm tất cả các review của một instructor
    List<Review> findByInstructorId(String instructorId);
}
