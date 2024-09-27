package zerobase.sijak.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyPageParam {

    @Size(min = 2, max = 12, message = "띄어쓰기 없이 2자 ~ 12자까지 가능해요.")
    String nickname;

    String address;

}
