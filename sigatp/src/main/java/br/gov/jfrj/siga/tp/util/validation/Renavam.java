package br.gov.jfrj.siga.tp.util.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = RenavamCheck.class)
public @interface Renavam {

	String message() default "{renavamCheck.validation}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}