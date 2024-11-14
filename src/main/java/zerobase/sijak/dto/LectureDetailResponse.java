package zerobase.sijak.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zerobase.sijak.persist.domain.Image;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LectureDetailResponse {

    private Integer id;

    private String name;

    private String description;

    private String price;

    private String tel;

    @JsonProperty("day_of_week")
    private String dayOfWeek;

    private String time;

    private Integer capacity;

    private String link;

    private String location;

    private boolean status;

    private String thumbnail;

    private boolean heart;

    private String address;

    @JsonProperty("hosted_by")
    private String hostedBy;

    private List<PeriodInfo> period;

    private String division;

    @JsonProperty("d_day")
    private long dDay;

    @JsonProperty("heart_count")
    private int heartCount;

    private String category;

    private String condition;

    private String detail;

    private String certification;

    @JsonProperty("text_book_name")
    private String textBookName;

    @JsonProperty("text_book_price")
    private String textBookPrice;

    private String need;

    @JsonProperty("instructor_name")
    private List<TeacherInfo> instructorName;

//    private String distance;
//
//    private String estimatedTime;

    private double latitude;

    private double longitude;

    private List<Image> images;

    private Map<Integer, String> plan;

}
