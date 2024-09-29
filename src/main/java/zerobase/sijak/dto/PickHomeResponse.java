package zerobase.sijak.dto;

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

    private String address;

    private String link;

}
