package ie303.ietutorapi.controllers;

import ie303.ietutorapi.models.User;
import ie303.ietutorapi.repositories.ReviewRepository;
import ie303.ietutorapi.repositories.UserRepository;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class UserController {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private ReviewRepository reviewRepo;

    @GetMapping("/users")
    public List<User> getUsers() {
        List<User> user = userRepo.findAll();
        return user;
    }

    // Get all instructors
    @GetMapping("/instructors")
    public ResponseEntity<?> getInstructors() {
        // Get all user that have the role of 1 (instructor) from MongoDB database
        List<User> instructors = userRepo.findAllByRole(1);

        List<InsJSON> res = new ArrayList<>();

        for (var ins : instructors) {
            InsJSON insJSON = new InsJSON();

            insJSON.setId(ins.getId());
            insJSON.setRating(calcAvgRating(ins.getId()));
            insJSON.setEmail(ins.getEmail());
            insJSON.setAddress(ins.getAddress());
            insJSON.setBio(ins.getBio());
            insJSON.setUsername(ins.getUsername());
            insJSON.setPhone(ins.getPhone());
            insJSON.setPicture(ins.getPicture());
            insJSON.setRole(ins.getRole());
            insJSON.setHourlyWage(ins.getHourlyWage());

            res.add(insJSON);
        }

        return ResponseEntity.ok(res);
    }

    @GetMapping("/user/{email}")
    public Optional<User> getUserByID(@PathVariable String email) {
        Optional<User> user = userRepo.findUsersByEmail(email);
        return user;
    }

    @GetMapping("/userId/{id}")
    public Optional<User> getUseruserID(@PathVariable String id) {
        Optional<User> user = userRepo.findUsersById(id);
        return user;
    }


    // Change user's role to instructor
    @PutMapping("/users/{id}/become-instructor")
    public ResponseEntity<?> becomeInstructor(@PathVariable("id") ObjectId id) {
        // find the user by id
        User user = userRepo.findById(String.valueOf(id)).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        // update the user's role
        user.setRole(1);
        userRepo.save(user);

        return ResponseEntity.ok(user);
    }


    public double calcAvgRating(String insId) {
        double avg = 0;
        var reviews = reviewRepo.findByInstructorId(insId);

        if (reviews.isEmpty())
            return avg;

        for (var review : reviews)
            avg += review.getRating();

        avg /= reviews.size();

        // 2 decimal places
        avg = Math.round(avg * 100.0) / 100.0;

        return avg;
    }

    //Add new user
    @PostMapping("/adduser")
    public User addUser(@RequestBody User user) {
        return userRepo.save(user);
    }

    //Delete user by ID
    @DeleteMapping("/deleteuser/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable String id) {
        Optional<User> user = userRepo.findById(id);
        if (user.isPresent()) {
            userRepo.deleteById(id);
            return ResponseEntity.ok("User deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //Count number of user
    @GetMapping("/users/stats")
    public ResponseEntity<Map<String, Long>> getUserStats() {
        long totalUsers = userRepo.count();
        long usersWithRole0 = userRepo.countByRole(0);
        long usersWithRole1 = userRepo.countByRole(1);

        Map<String, Long> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("usersWithRole0", usersWithRole0);
        stats.put("usersWithRole1", usersWithRole1);

        return ResponseEntity.ok(stats);
    }


    @Getter
    @Setter
    static class InsJSON extends User {
        double rating;
    }
}
