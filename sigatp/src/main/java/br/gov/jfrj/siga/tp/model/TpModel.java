package br.gov.jfrj.siga.tp.model;

import br.gov.jfrj.siga.model.Objeto;

public abstract class TpModel extends Objeto {

	private static final long serialVersionUID = -3265658962532346951L;

	public static final Long VAZIO = 0L;

	@Override
	public void save() {
		TpDao.getInstance().gravar(this);
	}
}
