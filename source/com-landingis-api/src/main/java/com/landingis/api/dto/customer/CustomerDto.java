package com.landingis.api.dto.customer;

import com.landingis.api.dto.ABasicAdminDto;
import lombok.Data;

@Data
public class CustomerDto extends ABasicAdminDto {
    private Long id;
    private String name;
}
