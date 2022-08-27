package com.landingis.api.form.device;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UpdateMobileDeviceForm {
    @NotEmpty(message = "deviceName cannot be null")
    @ApiModelProperty(required = true)
    private String deviceName;
}
