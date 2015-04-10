package br.gov.jfrj.siga.tp.util.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy=CnhConstraintValidator.class)
public @interface Cnh {
	String message() default "{condutor.CnhCheck.cnhinvalida}";
	Class<Object>[] groups() default {};
	Class<Object>[] payload() default {};
}
