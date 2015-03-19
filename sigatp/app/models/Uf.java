package models;

import java.util.List;

import br.gov.jfrj.siga.dp.CpUF;

@SuppressWarnings("serial")
public class Uf extends CpUF {

	public static List<Uf> listarTodos() {
		List<Uf> listaUf = Uf.findAll();
		return listaUf;
	}
	
	public Uf() {
		super();
	}
}
