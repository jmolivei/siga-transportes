package br.jus.jfrj.siga.uteis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/*
 * Utilizada em classes que derivam da classe GenericModel ou Model do pacote 
 * play.db.jpa. 
 * @author jlo
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface UpperCase{
}
