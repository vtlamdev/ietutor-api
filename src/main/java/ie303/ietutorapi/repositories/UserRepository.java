package ie303.ietutorapi.repositories;

import ie303.ietutorapi.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    // find a user by id and update the role to 1
//    User findByIdAndRole(String id, int role);
    Optional<User> findUsersByEmail(String email);

    Optional<User> findUsersById(String id);

    List<User> findAll();

    List<User> findAllByRole(int role);

    long countByRole(int role);
}
