package br.gov.jfrj.siga.tp.binder;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

@ApplicationScoped
@Convert(Double.class)
public class DoubleConverter implements Converter<Double> {

	@Override
	public Double convert(String value, Class<? extends Double> type, ResourceBundle bundle) {
		try {
			Logger.getLogger(this.getClass()).warn(MessageFormat.format("convertendo {0} para double", value));
			String valorInput = (value.equals("") ? "0.00" : value.replace(".", "").replace(",", "."));
			return new Double(valorInput);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
