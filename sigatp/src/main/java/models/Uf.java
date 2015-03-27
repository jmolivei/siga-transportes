package models;

import java.util.List;

import br.gov.jfrj.siga.dp.CpUF;
import br.gov.jfrj.siga.tp.model.CpRepository;

@SuppressWarnings("serial")
public class Uf extends CpUF {

	public static List<Uf> listarTodos() {
		return CpRepository.findAll(Uf.class);
	}

	public Uf() {
		super();
	}
}
