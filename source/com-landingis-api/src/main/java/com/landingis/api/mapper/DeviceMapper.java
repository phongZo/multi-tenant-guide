package com.landingis.api.mapper;

import com.landingis.api.dto.device.DeviceDto;
import com.landingis.api.form.device.CreateDeviceForm;
import com.landingis.api.form.device.UpdateDeviceForm;
import com.landingis.api.storage.master.model.Device;
import org.mapstruct.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        //nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = { CustomerMapper.class })
public interface DeviceMapper {


    @Mapping(source = "name", target = "name")
    @Mapping(source = "posId", target = "posId")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "customerId", target = "customer.id")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "expireDate", target = "expireDate", qualifiedByName = "convertToLocalDate")
    @Mapping(source = "extDate", target = "extDate", qualifiedByName = "convertToLocalDate")
    @Mapping(source = "setting", target = "setting")
    @Mapping(source = "isAdmin", target = "isAdmin")
    @Mapping(source = "latLongLimit", target = "latLongLimit")
    @Mapping(source = "distanceLimit", target = "distanceLimit")
    @BeanMapping(ignoreByDefault = true)
    @Named("adminCreateMapping")
    Device fromCreateFormToEntity(CreateDeviceForm createDeviceForm);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "expireDate", target = "expireDate", qualifiedByName = "convertToLocalDate")
    @Mapping(source = "extDate", target = "extDate", qualifiedByName = "convertToLocalDate")
    @Mapping(source = "setting", target = "setting", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "isAdmin", target = "isAdmin")
    @Mapping(source = "permission", target = "permission")
    @Mapping(source = "latLongLimit", target = "latLongLimit")
    @Mapping(source = "distanceLimit", target = "distanceLimit")
    @BeanMapping(ignoreByDefault = true)
    @Named("adminUpdateMapping")
    void fromUpdateFormToEntity(UpdateDeviceForm updateDeviceForm, @MappingTarget Device device);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "posId", target = "posId")
    @Mapping(source = "sessionId", target = "sessionId")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "platform", target = "platform")
    @Mapping(source = "enabledRemview", target = "enabledRemview")
    @Mapping(source = "customer", target = "customerDto", qualifiedByName = "adminGetMappingAutoComplete")
    @Mapping(source = "timeLastUsed", target = "timeLastUsed")
    @Mapping(source = "timeLastOnline", target = "timeLastOnline")
    @Mapping(source = "isLogin", target = "isLogin")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "expireDate", target = "expireDate", qualifiedByName = "convertToDate")
    @Mapping(source = "extDate", target = "extDate", qualifiedByName = "convertToDate")
    @Mapping(source = "setting", target = "setting")
    @Mapping(source = "isAdmin", target = "isAdmin")
    @Mapping(source = "permission", target = "permission")
    @Mapping(source = "latLongLimit", target = "latLongLimit")
    @Mapping(source = "distanceLimit", target = "distanceLimit")
    @Mapping(source = "parent", target = "parentDto", qualifiedByName = "getMappingAutoComplete")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "modifiedBy", target = "modifiedBy")
    @Mapping(source = "createdBy", target = "createdBy")
    @BeanMapping(ignoreByDefault = true)
    @Named("adminGetMapping")
    DeviceDto fromEntityToDto(Device device);

    @IterableMapping(elementTargetType = DeviceDto.class, qualifiedByName = "adminGetMapping")
    List<DeviceDto> fromEntityListToDtoList(List<Device> devices);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "posId", target = "posId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "enabledRemview", target = "enabledRemview")
    @Mapping(source = "parent", target = "parentDto", qualifiedByName = "getMappingAutoComplete")
    @Mapping(source = "platform", target = "platform")
    @Mapping(source = "timeLastOnline", target = "timeLastOnline")
    @Mapping(source = "isLogin", target = "isLogin")
    @Mapping(source = "expireDate", target = "expireDate", qualifiedByName = "convertToDate")
    @Mapping(source = "extDate", target = "extDate", qualifiedByName = "convertToDate")
    @BeanMapping(ignoreByDefault = true)
    @Named("getMappingAutoComplete")
    DeviceDto fromEntityToDtoAutoComplete(Device device);

    @IterableMapping(elementTargetType = DeviceDto.class, qualifiedByName = "getMappingAutoComplete")
    List<DeviceDto> fromEntityListToDtoListAutoComplete(List<Device> devices);

    @Named("convertToLocalDate")
    default LocalDate convertToLocalDateViaInstant(Date dateToConvert){
        if(dateToConvert == null){
            return null;
        }
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    @Named("convertToDate")
    default Date convertToDateViaInstant(LocalDate dateToConvert){
        if(dateToConvert == null){
            return null;
        }
        return Date.from(dateToConvert.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }
}
