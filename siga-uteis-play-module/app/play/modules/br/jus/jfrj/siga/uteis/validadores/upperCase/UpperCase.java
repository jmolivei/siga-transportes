package play.modules.br.jus.jfrj.siga.uteis.validadores.upperCase;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.oval.configuration.annotation.Constraint;

/**
 * Tem por finalidade converter para maiusculas todo atributo que estiver 
 * anotado por @UpperCase
 * 
 * Utilizada em classes que derivam da classe GenericModel ou Model do pacote 
 * play.db.jpa. 
 * @author jlo
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(checkWith=UpperCaseCheck.class)
public @interface UpperCase
{
}