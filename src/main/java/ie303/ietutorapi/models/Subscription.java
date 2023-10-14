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
@Document(collection = "subscriptions")
public class Subscription {
    @Id
    private String id;

    @Field("user_id")
    private String userId;

    @Field("email")
    private String email;

    @Field("plan_id")
    private String planId;

    private Integer duration;

    @Field("payment_method_id")
    private String paymentMethodId;

    private String status;

    private Double total;
    private String type;

    @Field("created_at")
    private Date createdAt;

    @Field("start_date")
    private Date startDate;

    @Field("end_date")
    private Date endDate;
}
