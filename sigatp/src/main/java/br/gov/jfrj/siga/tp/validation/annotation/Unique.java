package br.gov.jfrj.siga.tp.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import br.gov.jfrj.siga.tp.validation.UniqueConstraintValidator;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = UniqueConstraintValidator.class)
public @interface Unique {
	String message() default "{unique.validation}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String field();
}