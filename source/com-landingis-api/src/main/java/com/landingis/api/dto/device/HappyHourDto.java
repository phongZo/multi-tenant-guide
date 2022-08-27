package com.landingis.api.dto.device;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HappyHourDto {
    @JsonProperty("isHappyHour")
    private boolean isHappyHour = false;
    private Integer drink;
    private Integer food;
}
