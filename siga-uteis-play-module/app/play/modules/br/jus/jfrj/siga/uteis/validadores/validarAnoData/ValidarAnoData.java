package play.modules.br.jus.jfrj.siga.uteis.validadores.validarAnoData;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.oval.configuration.annotation.Constraint;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(checkWith=ValidarAnoDataCheck.class)
public @interface ValidarAnoData {
	int intervalo() default 1;
	String descricaoCampo();
}