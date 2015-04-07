package controllers;

import java.util.List;

import play.mvc.Controller;
import play.mvc.With;
import br.gov.jfrj.siga.tp.model.ItemMenu;
import br.gov.jfrj.siga.tp.model.LotacaoVeiculo;
import br.gov.jfrj.siga.tp.model.Veiculo;
import br.gov.jfrj.siga.tp.util.MenuMontador;

@With(AutorizacaoGIAntigo.class)
public class LotacoesVeiculo extends Controller {

	public static Long NOVOVEICULO = null;
	public static String ACTION_EDITAR = "Veiculos/Editar";

	public static void listarPorVeiculo(Long idVeiculo) throws Exception {
		Veiculo veiculo = Veiculo.AR.findById(idVeiculo);
		List<LotacaoVeiculo> lotacoesVeiculo = LotacaoVeiculo.buscarTodosPorVeiculo(veiculo);
		MenuMontador.instance().recuperarMenuVeiculos(idVeiculo, ItemMenu.LOTACOES);
		render(lotacoesVeiculo, veiculo);
	}
}
