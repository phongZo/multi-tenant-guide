package com.landingis.api.mapper;

import com.landingis.api.dto.dbConfig.DbConfigDto;
import com.landingis.api.form.dbConfig.CreateDbConfigForm;
import com.landingis.api.form.dbConfig.UpdateDbConfigForm;
import com.landingis.api.storage.master.model.DbConfig;
import org.mapstruct.*;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DbConfigMapper {
    @Mapping(source = "driverClassName", target = "driverClassName")
    @Mapping(source = "url", target = "url")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "initialize", target = "initialize")
    @Mapping(source = "deviceId", target = "device.id")
    @BeanMapping(ignoreByDefault = true)
    @Named("adminCreateMapping")
    DbConfig fromCreateFormToEntity(CreateDbConfigForm createDbConfigForm);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "driverClassName", target = "driverClassName")
    @Mapping(source = "url", target = "url")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "initialize", target = "initialize")
    @BeanMapping(ignoreByDefault = true)
    @Named("adminUpdateMapping")
    void fromUpdateFormToEntity(UpdateDbConfigForm updateDbConfigForm, @MappingTarget DbConfig dbConfig);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "driverClassName", target = "driverClassName")
    @Mapping(source = "url", target = "url")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "initialize", target = "initialize")
    @Mapping(source = "device.id", target = "deviceDto.id")
    @BeanMapping(ignoreByDefault = true)
    @Named("adminGetMapping")
    DbConfigDto fromEntityToDto(DbConfig dbConfig);
}
