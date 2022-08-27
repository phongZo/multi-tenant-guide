package com.landingis.api.form.device;

import com.landingis.api.validation.DevicePlatform;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class VerifyQRCodeForm {

    @NotEmpty(message = "qrCode cannot be null")
    @ApiModelProperty(required = true)
    private String qrCode;

    @NotEmpty(message = "deviceId cannot be null")
    @ApiModelProperty(required = true)
    private String deviceId;

    @NotNull(message = "platform cannot be null")
    @ApiModelProperty(required = true)
    @DevicePlatform
    private Integer platform;
}
