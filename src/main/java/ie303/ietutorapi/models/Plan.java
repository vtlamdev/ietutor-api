package ie303.ietutorapi.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "plans")
public class Plan {
    @Id
    private String id;

    private String type;

    private Double price;

    private Integer duration;

    @Field("bg_color")
    private String bgColor;


}
