package com.landingis.api.form.device;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class VerifyDeviceForm {

    @NotEmpty(message = "posId cannot be null")
    @ApiModelProperty(required = true)
    private String posId;

    @NotEmpty(message = "sessionId cannot be null")
    @ApiModelProperty(required = true)
    private String sessionId;
}
