package zerobase.sijak.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyPageResponse {

    private String nickname;

    private String location;

    private String email;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("age_range")
    private String ageRange;

    private String birth;

    private String gender;


}
