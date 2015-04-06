package br.gov.jfrj.siga.tp.model;

import br.gov.jfrj.siga.model.Objeto;

public class TPObjeto extends Objeto {

	private static final long serialVersionUID = -3265658962532346951L;

	@Override
	public void save() {
		TpDao.getInstance().gravar(this);
	}
}
