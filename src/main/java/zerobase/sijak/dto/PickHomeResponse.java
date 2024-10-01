package zerobase.sijak.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PickHomeResponse {

    private Integer id;

    private Integer view;

    private String thumbnail;

    private String name;

    private String time;

    private String target;

    private boolean status;

    private boolean heart;

    @JsonProperty("start_date")
    private String startDate;

    @JsonProperty("end_date")
    private String endDate;

    @JsonProperty("day_of_week")
    private String dayOfWeek;

    private String address;

    private String link;

}
