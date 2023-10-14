package ie303.ietutorapi.controllers;

import ie303.ietutorapi.models.Review;
import ie303.ietutorapi.repositories.ReviewRepository;
import ie303.ietutorapi.repositories.UserRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class ReviewController {
    @Autowired
    ReviewRepository reviewRepo;

    @Autowired
    UserRepository userRepo;

    // get average rating of an instructor
    @GetMapping("/ratings/average-rating/{email}")
    public ResponseEntity<?> getAverageRating(@PathVariable("email") String email) {
//         find the instructor by email
        String id = userRepo.findUsersByEmail(email).get().getId();


        // get all reviews of the instructor
        var reviews = reviewRepo.findByInstructorId(id);

        double avg = 0;
        if (reviews.isEmpty())
            return ResponseEntity.ok(avg);


        // get expanded reviews
        ArrayList<ExpandedReview> expandedReviews = new ArrayList<>();

        for (var review : reviews) {
            avg += review.getRating();

            var student = userRepo.findUsersById(review.getStudentId()).get();
            ExpandedReview expandedReview = new ExpandedReview();
            expandedReview.setStudentName(student.getUsername());
            expandedReview.setPicture(student.getPicture());
            expandedReview.setRating(review.getRating());
            expandedReview.setComment(review.getComment());
            expandedReview.setCreatedAt(review.getCreatedAt());
            expandedReview.setInstructorId(review.getInstructorId());
            expandedReview.setStudentId(review.getStudentId());
            expandedReview.setId(review.getId());

            System.out.println(student.getUsername() + " " + student.getPicture() + " " + review.getRating() + " " + review.getComment() + " " + review.getCreatedAt() + " " + review.getInstructorId() + " " + review.getStudentId() + " " + review.getId());

            expandedReviews.add(expandedReview);

        }
        avg /= reviews.size();


        // 2 decimal places
        avg = Math.round(avg * 100.0) / 100.0;

        JSONResponse response = new JSONResponse();
        response.setReviews(expandedReviews);
        response.setAverageRating(avg);


        return ResponseEntity.ok(response);
    }

    // get average rating of an instructor
    public double calcAvgRating(String insId) {
        double avg = 0;
        var reviews = reviewRepo.findByInstructorId(insId);

        if (reviews.isEmpty())
            return avg;

        for (var review : reviews)
            avg += review.getRating();

        // 2 decimal places
        avg = Math.round(avg * 100.0) / 100.0;

        return avg;
    }

    @Getter
    @Setter
    static class JSONResponse {
        double averageRating;
        List<ExpandedReview> reviews;
    }

    @Getter
    @Setter
    static class ExpandedReview extends Review {
        String studentName;
        String picture;
    }

   /* @Getter
    @Setter
    static class InsJSON {
        String id;
        double rating;
        String email;
        String address;
        String bio;
        String username;
        String phone;
        String picture;
        int role;
        double hourlyWage;
    } */
}
