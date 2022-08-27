package com.landingis.api.form.settings;

import com.landingis.api.validation.Status;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UpdateSettingsForm {
    @NotNull(message = "id cannot be null")
    @ApiModelProperty(required = true)
    private Long id;

    @NotEmpty(message = "value cannot be null")
    @ApiModelProperty(required = true)
    private String value;

    @NotEmpty(message = "description cannot be null")
    @ApiModelProperty(required = true)
    private String description;

    @Status
    @NotNull(message = "status cannot be null")
    @ApiModelProperty(required = true)
    private Integer status;

}
