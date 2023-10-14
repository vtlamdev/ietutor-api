package ie303.ietutorapi.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "bookings")
public class Booking {
    @Id
    private String id;

    @Field("instructor_id")
    private String instructorId;

    @Field("student_id")
    private String studentId;

    @Field("start_date")
    private Date startDate;

    @Field("end_date")
    private Date endDate;

    private String status;

    private String title;
    private String notes;

    @Field("Created_at")
    private Date createdAt; // date of the booking
//    @Getter
//    @Setter
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class Recurrence {
//
//        @Field("frequency")
//        private String frequency;
//        @Field("interval")
//        private int interval;
//
//        @Field("start_date")
//        private Date startDate;
//
//        @Field("end_date")
//        private Date endDate;
//
//        // ...
//    }
}
