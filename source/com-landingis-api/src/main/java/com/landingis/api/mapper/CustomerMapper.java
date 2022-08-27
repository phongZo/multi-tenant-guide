package com.landingis.api.mapper;

import com.landingis.api.dto.customer.CustomerDto;
import com.landingis.api.form.customer.CreateCustomerForm;
import com.landingis.api.form.customer.UpdateCustomerForm;
import com.landingis.api.storage.master.model.Customer;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CustomerMapper {

    @Mapping(source = "name", target = "name")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("adminCreateMapping")
    Customer fromCreateFormToEntity(CreateCustomerForm createCustomerForm);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("adminUpdateMapping")
    void fromUpdateFormToEntity(UpdateCustomerForm updateCustomerForm, @MappingTarget Customer customer);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "modifiedBy", target = "modifiedBy")
    @Mapping(source = "createdBy", target = "createdBy")
    @BeanMapping(ignoreByDefault = true)
    @Named("adminGetMapping")
    CustomerDto fromEntityToDto(Customer customer);

    @IterableMapping(elementTargetType = CustomerDto.class, qualifiedByName = "adminGetMapping")
    List<CustomerDto> fromEntityListToDtoList(List<Customer> customers);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @BeanMapping(ignoreByDefault = true)
    @Named("adminGetMappingAutoComplete")
    CustomerDto fromEntityToDtoAutoComplete(Customer customer);

    @IterableMapping(elementTargetType = CustomerDto.class, qualifiedByName = "adminGetMappingAutoComplete")
    List<CustomerDto> fromEntityListToDtoListAutoComplete(List<Customer> customers);
}
