package com.landingis.api.form.settings;

import com.landingis.api.validation.Status;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CreateSettingsForm {
    @NotEmpty(message = "name cannot be null")
    @ApiModelProperty(required = true)
    private String name;

    @NotEmpty(message = "key cannot be null")
    @ApiModelProperty(required = true)
    private String key;

    @NotEmpty(message = "value cannot be null")
    @ApiModelProperty(required = true)
    private String value;

    @NotEmpty(message = "description cannot be null")
    @ApiModelProperty(required = true)
    private String description;

    @NotEmpty(message = "group cannot be null")
    @ApiModelProperty(required = true)
    private String group;

    @NotNull(message = "groupId cannot be null")
    @ApiModelProperty(required = true)
    private Integer groupId;

    @NotNull(message = "editable cannot be null")
    @ApiModelProperty(required = true)
    private boolean editable;

    @NotNull(message = "kind cannot be null")
    @ApiModelProperty(required = true)
    private Integer kind;

    @Status
    @NotNull(message = "status cannot be null")
    @ApiModelProperty(required = true)
    private Integer status;
}
