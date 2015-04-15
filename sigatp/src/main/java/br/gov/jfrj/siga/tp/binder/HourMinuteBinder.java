package br.gov.jfrj.siga.tp.binder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * TODO: remover ao final da migracao
 * 
 * @author db1
 *
 */
@Deprecated
public class HourMinuteBinder /* implements TypeBinder<Calendar> */{

	public Object bind(String name, Annotation[] annotations, String value, Class<?> actualClass, Type genericType) {
		String dataHora = "01/01/1900 " + value;
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Calendar data = Calendar.getInstance();
		try {
			data.setTime(formato.parse(dataHora));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return data;
	}
}
