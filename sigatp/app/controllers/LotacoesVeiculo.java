package controllers;

import uteis.MenuMontador;

import java.util.*;

import play.mvc.Controller;
import play.mvc.With;
import models.*;

@With(AutorizacaoGI.class)
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
