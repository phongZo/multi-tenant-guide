package com.landingis.api.validation;



import com.landingis.api.validation.impl.DeviceTypeValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DeviceTypeValidation.class)
@Documented
public @interface DeviceType {
    boolean allowNull() default true;
    String message() default "Device type invalid.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
