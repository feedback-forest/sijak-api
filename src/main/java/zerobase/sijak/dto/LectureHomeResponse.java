package zerobase.sijak.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LectureHomeResponse {

    Integer id;

    String name;

    String time;

    String address;

    boolean isHeart;

}
