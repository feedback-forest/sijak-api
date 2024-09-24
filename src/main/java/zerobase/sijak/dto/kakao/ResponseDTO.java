package zerobase.sijak.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO {

    TokenDTO tokenDTO;

    @JsonProperty("is_new")
    boolean isNew;
}

