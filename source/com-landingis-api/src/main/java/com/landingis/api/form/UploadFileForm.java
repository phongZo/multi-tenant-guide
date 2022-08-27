package com.landingis.api.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class UploadFileForm {
    private String type = "DOCUMENT";

    @NotEmpty(message = "md5 hash is required")
    @ApiModelProperty(name = "md5", required = true)
    private String md5 ;

    @NotNull(message = "file is required")
    @ApiModelProperty(name = "file", required = true)
    private MultipartFile file;
}
