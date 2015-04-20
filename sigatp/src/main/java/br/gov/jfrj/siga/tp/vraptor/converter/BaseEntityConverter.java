package br.gov.jfrj.siga.tp.vraptor.converter;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import br.com.caelum.vraptor.Converter;
import br.gov.jfrj.siga.model.ContextoPersistencia;
import br.gov.jfrj.siga.tp.model.TpModel;

public class BaseEntityConverter<T> implements Converter<T> {

	@Override
	public T convert(String idString, Class<? extends T> type, ResourceBundle bundle) {
		if (idString != null && !idString.isEmpty()) {
			Long id = Long.valueOf(idString);

			if (TpModel.existe(id))
				return buscarRegistro(type, id);
		}
		return novaInstancia(type);
	}

	private T buscarRegistro(Class<? extends T> type, Long id) {
		return ContextoPersistencia
					.em()
					.find(type, Long.valueOf(id));
	}

	private T novaInstancia(Class<? extends T> type) {
		try {
			return type.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(MessageFormat.format("Erro ao instanciar objeto do tipo {0}. Existe constutor padrao?", type), e);
		}
	}
}
