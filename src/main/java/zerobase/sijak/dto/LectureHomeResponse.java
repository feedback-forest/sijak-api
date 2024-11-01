package zerobase.sijak.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LectureHomeResponse {

    private Integer id;

    private String thumbnail;

    private String name;

    private String time;

    private String target;

    private boolean status;

    @JsonProperty("start_date")
    private String startDate;

    @JsonProperty("end_date")
    private String endDate;

    @JsonProperty("day_of_week")
    private String dayOfWeek;

    private double latitude;

    private double longitude;

    @JsonProperty("hosted_by")
    private String hostedBy;

    @JsonProperty("short_address")
    private String shortAddress;

    @JsonProperty("long_address")
    private String longAddress;

    private String link;

    private String division;

    private boolean heart;

    private String category;

    private String price;

}
