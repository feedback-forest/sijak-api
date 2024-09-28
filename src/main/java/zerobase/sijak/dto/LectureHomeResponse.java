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

    private Integer id;

    private String thumbnail;

    private String name;

    private String time;

    private String target;

    private String address;

    private String link;

    private boolean isHeart;

}
