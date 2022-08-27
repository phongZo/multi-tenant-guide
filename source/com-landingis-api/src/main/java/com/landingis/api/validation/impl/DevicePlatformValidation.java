package com.landingis.api.validation.impl;



import com.landingis.api.constant.LandingISConstant;
import com.landingis.api.validation.DevicePlatform;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class DevicePlatformValidation implements ConstraintValidator<DevicePlatform, Integer> {
    private boolean allowNull;

    @Override
    public void initialize(DevicePlatform constraintAnnotation) { allowNull = constraintAnnotation.allowNull(); }

    @Override
    public boolean isValid(Integer devicePlatform, ConstraintValidatorContext constraintValidatorContext) {
        if(devicePlatform == null && allowNull) {
            return true;
        }
        if(!Objects.equals(devicePlatform, LandingISConstant.DEVICE_PLATFORM_ANDROID)
            && !Objects.equals(devicePlatform, LandingISConstant.DEVICE_PLATFORM_IOS)
            && !Objects.equals(devicePlatform, LandingISConstant.DEVICE_PLATFORM_WEB)) {
            return false;
        }
        return true;
    }
}
