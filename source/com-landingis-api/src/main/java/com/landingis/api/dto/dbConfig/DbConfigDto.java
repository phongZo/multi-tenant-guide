package com.landingis.api.dto.dbConfig;

import com.landingis.api.dto.device.DeviceDto;
import lombok.Data;

@Data
public class DbConfigDto {
    private Long id;
    private String name;
    private String url;
    private String username;
    private String password;
    private String driverClassName;
    private boolean initialize;
    private DeviceDto deviceDto;
}
