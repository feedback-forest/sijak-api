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

    private String address;

    private String link;

    private boolean heart;

}
