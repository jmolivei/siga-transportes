package br.gov.jfrj.siga.tp.binder;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.gov.jfrj.siga.model.ContextoPersistencia;
import br.gov.jfrj.siga.tp.model.TpModel;

@Convert(TpModel.class)
public class TpModelConverter implements Converter<TpModel> {

	@Override
	public TpModel convert(String id, Class<? extends TpModel> type, ResourceBundle bundle) {
		if (id != null) {
			return ContextoPersistencia
					.em()
					.find(type, Long.valueOf(id));
		}
		return novaInstancia(type);
	}

	private TpModel novaInstancia(Class<? extends TpModel> type) {
		try {
			return type.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(MessageFormat.format("Erro ao instanciar objeto do tipo {0}. Existe constutor padrao?", type), e);
		}
	}
}
