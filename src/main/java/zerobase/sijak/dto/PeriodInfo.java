package zerobase.sijak.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PeriodInfo {

    private String startDate;

    private String endDate;

    private int total;


}
