package br.gov.jfrj.siga.tp.model;

import java.util.List;

import play.data.validation.Required;

public class RelatorioConsumoMedio {
	@Required
	public Veiculo veiculo;

	@Required
	public Abastecimento abastecimentoInicial;
	
	@Required
	public Abastecimento abastecimentoFinal;
	
	public List<Missao> missoes;
	public double kmPercorridos;
	public double consumoMedio;
}