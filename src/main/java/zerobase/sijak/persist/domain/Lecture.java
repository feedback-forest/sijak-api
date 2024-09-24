package zerobase.sijak.persist.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zerobase.sijak.dto.crawling.LectureCreateRequest;

import java.util.ArrayList;
import java.util.HashMap;
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

    private String description;

    private String price;

    @OneToMany(mappedBy = "lecture")
    private List<Teacher> teachers = new ArrayList<>();

    private Integer capacity;

    @Column(name = "day_of_week")
    private String dayOfWeek;

    private String time;


    @Column(name = "image_url")
    @OneToMany(mappedBy = "lecture")
    private List<Image> imageUrls = new ArrayList<>();

    private String link;

    private String location;

    private String target;

    private String status;

    private Integer view = 0;

    private String category = "미정";

    @Column(name = "center_name", nullable = false)
    private String centerName;

    private String address;

    private Double latitude;

    private Double longitude;

    @OneToMany(mappedBy = "lecture")
    private List<Heart> hearts = new ArrayList<>();

    @OneToMany(mappedBy = "lecture")
    private List<Review> reviews = new ArrayList<>();

    public Lecture(LectureCreateRequest lectureCreateRequest) {
        this.name = lectureCreateRequest.getName();
        this.description = lectureCreateRequest.getDescription();
        this.price = lectureCreateRequest.getPrice();
        this.capacity = lectureCreateRequest.getCapacity();
        this.dayOfWeek = lectureCreateRequest.getDayOfWeek();
        this.time = lectureCreateRequest.getTime();
        this.link = lectureCreateRequest.getLink();
        this.location = lectureCreateRequest.getLocation();
        this.status = lectureCreateRequest.getStatus();
        this.hearts = lectureCreateRequest.getHearts();
        this.reviews = lectureCreateRequest.getReviews();
        this.imageUrls = lectureCreateRequest.getImageUrls();
        this.centerName = lectureCreateRequest.getCenterName();
        this.address = lectureCreateRequest.getAddress();
        this.latitude = lectureCreateRequest.getLatitude();
        this.longitude = lectureCreateRequest.getLongitude();
    }
}
