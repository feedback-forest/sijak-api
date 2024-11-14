package zerobase.sijak.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @JsonProperty("short_address")
    private String shortAddress;

    @JsonProperty("long_address")
    private String longAddress;

    private String link;

    private String division;

    private String price;

    private List<String> categories;

}
