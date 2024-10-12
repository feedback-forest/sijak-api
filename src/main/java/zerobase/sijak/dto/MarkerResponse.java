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
public class MarkerResponse {

    @JsonProperty("short_address")
    private String shortAddress;

    @JsonProperty("long_address")
    private String longAddress;

    @JsonProperty("hosted_by")
    private String hostedBy;

    private Double longitude;

    private Double latitude;

}
