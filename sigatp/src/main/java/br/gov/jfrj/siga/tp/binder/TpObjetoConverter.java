package br.gov.jfrj.siga.tp.binder;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.gov.jfrj.siga.model.ContextoPersistencia;
import br.gov.jfrj.siga.model.Objeto;
import br.gov.jfrj.siga.tp.model.TpModel;

@Convert(Objeto.class)
public class TpObjetoConverter implements Converter<Objeto> {

	@Override
	public Objeto convert(String idString, Class<? extends Objeto> type, ResourceBundle bundle) {
		if (idString != null && !idString.isEmpty()) {
			Long id = Long.valueOf(idString);

			if (TpModel.existe(id))
				return buscarRegistro(type, id);
		}
		return novaInstancia(type);
	}

	private Objeto buscarRegistro(Class<? extends Objeto> type, Long id) {
		return ContextoPersistencia
					.em()
					.find(type, Long.valueOf(id));
	}

	private Objeto novaInstancia(Class<? extends Objeto> type) {
		try {
			return type.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(MessageFormat.format("Erro ao instanciar objeto do tipo {0}. Existe constutor padrao?", type), e);
		}
	}

}
