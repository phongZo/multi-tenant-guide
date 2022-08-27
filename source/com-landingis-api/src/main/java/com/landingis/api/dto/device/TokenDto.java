package com.landingis.api.dto.device;

import lombok.Data;

@Data
public class TokenDto {
    private Long id;
    private String token;
    private String newSessionId;
    private boolean enabledRemview;
    private String deviceId;
    private String posName;
    private Long posId;
    private String posToken;
    private Boolean isActive;
    private String tenantId;
    private HappyHourDto happyHour;
    private String accessToken;
    private Boolean isAdmin;
    private String latLongLimit;
    private Integer distanceLimit;
    private Integer permission;
}
