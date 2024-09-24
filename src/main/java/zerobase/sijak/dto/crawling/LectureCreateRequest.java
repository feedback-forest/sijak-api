package zerobase.sijak.dto.crawling;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zerobase.sijak.persist.domain.Heart;
import zerobase.sijak.persist.domain.Image;
import zerobase.sijak.persist.domain.Review;
import zerobase.sijak.persist.domain.Teacher;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LectureCreateRequest {

    private String name;

    private String description;

    private String price;

    private List<Teacher> teachers = new ArrayList<>();

    private Integer capacity;

    @Column(name = "day_of_week")
    @JsonProperty("day_of_week")
    private String dayOfWeek;

    private String time;

    @Column(name = "image_url")
    @JsonProperty("image_url")
    private List<Image> imageUrls = new ArrayList<>();

    private String link;

    private String location;

    private String target;

    private String status;

    private Integer view;

    private String category;

    @Column(name = "center_name", unique = true, nullable = false)
    private String centerName;

    private String address;

    private Double latitude;

    private Double longitude;

    private List<Heart> hearts = new ArrayList<>();

    private List<Review> reviews = new ArrayList<>();

}
