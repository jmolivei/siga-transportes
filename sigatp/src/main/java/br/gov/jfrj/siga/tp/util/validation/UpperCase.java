package br.gov.jfrj.siga.tp.util.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import play.modules.br.jus.jfrj.siga.uteis.validadores.upperCase.UpperCaseCheck;
import net.sf.oval.configuration.annotation.Constraint;

/**
 * Tem por finalidade converter para maiusculas todo atributo que estiver 
 * anotado por @UpperCase
 * 
 * Utilizada em classes Model
 * @author jlo
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(checkWith=UpperCaseCheck.class)
public @interface UpperCase
{
}