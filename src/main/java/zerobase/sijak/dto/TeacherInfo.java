package zerobase.sijak.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherInfo {

    private Integer id;

    private String name;

    @JsonProperty("instructor_history")
    private List<CareerInfo> instructorHistory;

}
