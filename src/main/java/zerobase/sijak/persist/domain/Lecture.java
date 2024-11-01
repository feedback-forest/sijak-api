package zerobase.sijak.persist.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecture_id")
    private Integer id;

    private String name;

    @Size(max = 1000)
    private String description;

    private String tel;

    private String price;

    @OneToMany(mappedBy = "lecture")
    @Builder.Default
    private List<Teacher> teachers = new ArrayList<>();

    private Integer capacity;

    @Column(name = "day_of_week")
    @JsonProperty("day_of_week")
    private String dayOfWeek;

    private String time;

    private String thumbnail;

    @JsonProperty("start_date")
    private String startDate;

    @JsonProperty("end_date")
    private String endDate;

    private Integer total;

    @Column(name = "image_url")
    @OneToMany(mappedBy = "lecture")
    @JsonProperty("image_url")
    @Builder.Default
    private List<Image> imageUrls = new ArrayList<>();

    private String link;

    private String location;

    @JsonProperty("text_book_name")
    @Column(name = "text_book_name")
    private String textBookName;

    @JsonProperty("text_book_price")
    @Column(name = "text_book_price")
    private String textBookPrice;

    private String division;

    @Size(max = 1000)
    private String certification;

    private String target;

    private boolean status;

    private String need;

    private Integer view;

    @Builder.Default
    private String category = "미정";

    @Column(name = "center_name", nullable = false)
    @JsonProperty("center_name")
    private String centerName;

    private String address;

    private Double latitude;

    private Double longitude;

    private LocalDateTime deadline;


    @OneToMany(mappedBy = "lecture")
    @Builder.Default
    private List<Heart> hearts = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "lecture")
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "lecture")
    @Builder.Default
    private List<Educate> educates = new ArrayList<>();

}
