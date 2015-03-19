package play.modules.br.jus.jfrj.siga.uteis.validadores.validarAnoData;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.GregorianCalendar;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.FieldContext;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;

@SuppressWarnings("serial")
public class ValidarAnoDataCheck extends AbstractAnnotationCheck<ValidarAnoData> {
	
	public static Boolean validarAnoData(Object objeto, Field campo, int intervalo)  {
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(((GregorianCalendar) campo.get(objeto)).getTime());
			return (calendar.get(Calendar.YEAR) - Calendar.getInstance().get(Calendar.YEAR) <= intervalo);
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean isSatisfied(Object validatedObject, Object value,
			OValContext context, Validator validator) throws OValException {

		Field campo = ((FieldContext) context).getField();
		campo.setAccessible(true);

		if (!campo.isAnnotationPresent(ValidarAnoData.class)) {
			this.setMessage("Campo invalido");
			return false;
		}
		
		if (! campo.getType().getName().equals("java.util.Calendar")) {
			this.setMessage("Campo invalido");
			return false;
		}
		
		ValidarAnoData anotacao = campo.getAnnotation(ValidarAnoData.class);
		int ano = Calendar.getInstance().get(Calendar.YEAR) + anotacao.intervalo();
		this.setMessage("Ano de " + anotacao.descricaoCampo() + " nao deve ser maior que o ano de " + ano + ".");

		if (value == null) {
			return true;
		}

		return validarAnoData(validatedObject, campo, anotacao.intervalo());
	}
}