package play.modules.br.jus.jfrj.siga.uteis.validadores.telefone;

import play.data.validation.Check;

public class TelefoneCheck extends Check {

	@Override
	public boolean isSatisfied(Object validatedObject, Object value) {
		this.setMessage("Numero de telefone fixo invalido.");
		return validarFixo(((String)value).toString());
	}

	private boolean validarFixo(String string) {
		return true;
	}
}
