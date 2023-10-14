package ie303.ietutorapi.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "reviews")
public class Review {
    /*
    Reviews Collection:
    {
      _id: ObjectId,
      instructor_id: ObjectId, // reference to the instructor being reviewed
      student_id: ObjectId, // reference to the student who wrote the review
      rating: Number, // the rating (out of 5) given by the student
      comment: String // the comment written by the student
      created_at: Date // the date on which the review was submitted
    }
    * */
    @Id
    private String id;


    @Field("instructor_id")
    private String instructorId;

    @Field("student_id")
    private String studentId;
    private int rating;
    private String comment;

    @Field("created_at")
    private Date createdAt;
}