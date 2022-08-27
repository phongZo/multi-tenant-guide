package com.landingis.api.validation.impl;



import com.landingis.api.constant.LandingISConstant;
import com.landingis.api.validation.DeviceType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class DeviceTypeValidation implements ConstraintValidator<DeviceType, Integer> {
    private boolean allowNull;

    @Override
    public void initialize(DeviceType constraintAnnotation) { allowNull = constraintAnnotation.allowNull(); }

    @Override
    public boolean isValid(Integer deviceType, ConstraintValidatorContext constraintValidatorContext) {
        if(deviceType == null && allowNull) {
            return true;
        }
        if(!Objects.equals(deviceType, LandingISConstant.DEVICE_TYPE_POS)
            && !Objects.equals(deviceType, LandingISConstant.DEVICE_TYPE_REMVIEW)) {
            return false;
        }
        return true;
    }
}
