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
@Document(collection = "notifications")
public class Notification {
    /*
    Notifications Collection:
    {
      _id: ObjectId,
      user_id: ObjectId, // reference to the user who received the notification
      message: String, // the content of the notification
      is_read: Boolean, // whether the notification has been read by the user
      created_at: Date // the date and time when the notification was created
    }

    */
    @Id
    private String id;

    @Field("user_id")
    private String userId;

    private String message;

    @Field("is_read")
    private boolean isRead;

    @Field("created_at")
    private Date createdAt;

    public Notification(String studentId, String message) {
        this.userId = studentId;
        this.message = message;
        this.isRead = false;
        this.createdAt = new Date();
    }
}
