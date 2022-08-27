package com.landingis.api.dto.settings;

import com.landingis.api.dto.ABasicAdminDto;
import lombok.Data;

@Data
public class SettingsDto extends ABasicAdminDto {
    private String name;
    private String key;
    private String value;
    private String description;
    private String group;
    private Integer groupId;
    private boolean editable;
    private Integer kind;
}
