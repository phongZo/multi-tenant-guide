package com.landingis.api.dto.device;

import com.landingis.api.dto.ABasicAdminDto;
import com.landingis.api.dto.customer.CustomerDto;
import lombok.Data;

import java.util.Date;

@Data
public class DeviceDto extends ABasicAdminDto {
    private Long id;
    private String name;
    private String posId;
    private String sessionId;
    private Integer type;
    private Integer platform;
    private Boolean enabledRemview;
    private CustomerDto customerDto;
    private DeviceDto parentDto;
    private Date timeLastUsed;
    private Date timeLastOnline;
    private Boolean isLogin;
    private Date expireDate;
    private Date extDate;
    private String setting;
    private Integer permission;
    private Boolean isAdmin;
    private String latLongLimit;
    private Integer distanceLimit;
    private String accessToken;
}
