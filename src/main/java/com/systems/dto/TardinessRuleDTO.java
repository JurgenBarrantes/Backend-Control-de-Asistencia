package com.systems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TardinessRuleDTO {
    private Integer idTardinessRule;
    private Integer tardinnessThresholdMinutes;
    private Integer absenceThresholdMinutes;

}
