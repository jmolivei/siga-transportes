package br.gov.jfrj.siga.tp.binder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ResourceBundle;

import org.apache.commons.lang.time.DateUtils;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

@ApplicationScoped
@Convert(Calendar.class)
public class GenericCalendarConverter implements Converter<Calendar> {

	private static final String DATA_INICIO = "01/01/1900 ";
	private static final String DD_MM_YYYY_HH_MM = "dd/MM/yyyy HH:mm";
	private static final String[] PADROES = { 
		"dd/MM/yyyy", 
		DD_MM_YYYY_HH_MM

	};

	@Override
	public Calendar convert(String value, Class<? extends Calendar> type, ResourceBundle bundle) {
		if (dataPreenchida(value)) {
			if (value.matches("\\d\\d:\\d\\d")) {
				return converterQuandoApenasHorasMinutos(value);
			}
			return converterUtilizandoPadroesAceitos(value);
		}
		return null;
	}

	private boolean dataPreenchida(String value) {
		return value != null && !value.isEmpty();
	}

	private Calendar converterUtilizandoPadroesAceitos(String value) {
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(DateUtils.parseDate(value, PADROES));
			return calendar;
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	private Calendar converterQuandoApenasHorasMinutos(String value) {
		try {
			String dataHora = DATA_INICIO + value;
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new SimpleDateFormat(DD_MM_YYYY_HH_MM).parse(dataHora));
			return calendar;
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
}
