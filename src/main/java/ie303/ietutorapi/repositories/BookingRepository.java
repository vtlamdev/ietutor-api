package ie303.ietutorapi.repositories;

import ie303.ietutorapi.models.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {
    // find bookings of an instructor

    List<Booking> findByInstructorId(String userId);

    // find bookings that have status of "approved" of a specific user
    List<Booking> findByInstructorIdAndStatus(String userId, String status);

    // find bookings of a student
    List<Booking> findByStudentId(String userId);

    // find bookings that have status of "approved" of a student
    List<Booking> findByStudentIdAndStatus(String userId, String status);

}
