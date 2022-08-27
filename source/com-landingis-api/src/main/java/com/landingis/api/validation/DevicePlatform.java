package com.landingis.api.validation;



import com.landingis.api.validation.impl.DevicePlatformValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DevicePlatformValidation.class)
@Documented
public @interface DevicePlatform {
    boolean allowNull() default true;
    String message() default "Device platform invalid.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
