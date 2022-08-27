package com.landingis.api.form.device;

import com.landingis.api.validation.Status;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class UpdateDeviceForm {

    @NotNull(message = "id cannot be null")
    @ApiModelProperty(required = true)
    private Long id;

    @NotEmpty(message = "name cannot be null")
    @ApiModelProperty(required = true)
    private String name;

    @ApiModelProperty(name = "sessionId")
    private String sessionId;

    @ApiModelProperty(name = "enabledRemview")
    private Boolean enabledRemview;

    @NotNull(message = "status cannot be null")
    @ApiModelProperty(required = true)
    @Status
    private Integer status;

    @ApiModelProperty(name = "expireDate")
    private Date expireDate;

    @ApiModelProperty(name = "extDate")
    private Date extDate;

    @ApiModelProperty(name = "setting")
    private String setting;

    @ApiModelProperty(name = "isAdmin")
    private Boolean isAdmin;

    @ApiModelProperty(name = "latLongLimit")
    private String latLongLimit;

    @ApiModelProperty(name = "distanceLimit")
    private Integer distanceLimit;

    @ApiModelProperty(name = "permission")
    private Integer permission;
}
