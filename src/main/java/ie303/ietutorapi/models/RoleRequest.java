package ie303.ietutorapi.models;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "role_requests")
public class RoleRequest {

    @Id
    private String id;

    @Field("user_id")
    private String userId;

    @Field("hourly_wage")
    private int hourlyWage;

    @Field("is_approved")
    private boolean isApproved;

    @Field("created_at")
    private Date createdAt;

      /*
        RoleRequests Collection:
        {
            _id: ObjectId,
            user_id: ObjectId, // reference to the user who submitted the request
            is_approved: Boolean, // whether the request has been approved
            hourly_wage: Number, // the hourly wage the user is requesting
            created_at: Date // the date on which the request was submitted
        }
     */

}

