package controllers;

import java.util.*;

import br.gov.jfrj.siga.tp.model.ItemMenu;
import br.gov.jfrj.siga.tp.model.LotacaoVeiculo;
import br.gov.jfrj.siga.tp.model.Veiculo;
import br.gov.jfrj.siga.tp.util.MenuMontador;
import play.mvc.Controller;
import play.mvc.With;
import models.*;

@With(AutorizacaoGIAntigo.class)
public class LotacoesVeiculo extends Controller {
	
	public static Long NOVOVEICULO = null;
	public static String ACTION_EDITAR = "Veiculos/Editar";

	public static void listarPorVeiculo(Long idVeiculo) {
		Veiculo veiculo = Veiculo.findById(idVeiculo);
		List<LotacaoVeiculo> lotacoesVeiculo = LotacaoVeiculo.buscarTodosPorVeiculo(veiculo);
		MenuMontador.instance().RecuperarMenuVeiculos(idVeiculo, ItemMenu.LOTACOES);
		render(lotacoesVeiculo, veiculo);
	}
}
