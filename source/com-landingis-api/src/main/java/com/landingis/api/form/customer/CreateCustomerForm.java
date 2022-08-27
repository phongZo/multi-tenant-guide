package com.landingis.api.form.customer;

import com.landingis.api.validation.Status;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CreateCustomerForm {

    @NotEmpty(message = "name cannot be null")
    @ApiModelProperty(required = true)
    private String name;

    @NotNull(message = "status cannot be null")
    @ApiModelProperty(required = true)
    @Status
    private Integer status;

    @NotNull(message = "branchId cannot be null")
    @ApiModelProperty(required = true)
    private Long branchId;
}
