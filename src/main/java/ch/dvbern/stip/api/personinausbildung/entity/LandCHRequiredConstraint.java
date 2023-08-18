package ch.dvbern.stip.api.personinausbildung.entity;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import static ch.dvbern.stip.api.common.validation.ValidationsConstant.VALIDATION_LAND_CH_FIELD_REQUIRED_MESSAGE;

@Target({ ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LandCHRequiredConstraintValidator.class)
@Documented
public @interface LandCHRequiredConstraint {

	String message() default VALIDATION_LAND_CH_FIELD_REQUIRED_MESSAGE;

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
