package com.landingis.api.form.dbConfig;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UpdateDbConfigForm {

    @NotNull(message = "id cannot be null")
    @ApiModelProperty(required = true)
    private Long id;

    @NotEmpty(message = "url cannot be null")
    @ApiModelProperty(required = true)
    private String url;

    @NotEmpty(message = "username cannot be null")
    @ApiModelProperty(required = true)
    private String username;

    @NotEmpty(message = "password cannot be null")
    @ApiModelProperty(required = true)
    private String password;

    @NotEmpty(message = "driverClassName cannot be null")
    @ApiModelProperty(required = true)
    private String driverClassName;

    @NotNull(message = "initialize cannot be null")
    @ApiModelProperty(required = true)
    private boolean initialize;
}
