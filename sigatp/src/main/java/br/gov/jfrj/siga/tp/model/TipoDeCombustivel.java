package br.gov.jfrj.siga.tp.model;

import java.util.ArrayList;
import java.util.List;

public enum TipoDeCombustivel {

	GASOLINA("GASOLINA", true),
	ALCOOL("ETANOL", true),
	DIESEL("DIESEL", true), GNV("GNV", true),
	ALCOOL_E_GASOLINA("FLEX", false),
	ALCOOL_E_GNV("ALCOOL/GNV", false),
	GASOLINA_E_GNV("GASOLINA/GNV", false),
	DIESEL_E_GNV("DIESEL/GNV", false),
	GASOLINA_E_ALCOOL_E_GNV("GASOLINA/ALCOOL/GNV", false),
	GASOLINA_ADITIVADA("GASOLINA ADITIVADA", true);
	
	private String descricao;
	private boolean exibirNoAbastecimento;
	
	TipoDeCombustivel(String descricao, boolean exibirNoAbastecimento){
		this.setDescricao(descricao);
		this.setExibirNoAbastecimento(exibirNoAbastecimento);
	}

	private void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	private void setExibirNoAbastecimento(boolean exibirNoAbastecimento) {
		this.exibirNoAbastecimento = exibirNoAbastecimento;
	}
	
	public String getDescricao() {
		return this.descricao;
	}
	
	public boolean getExibirNoAbastecimento() {
		return this.exibirNoAbastecimento;
	}
	
	@Override
	public String toString() {
		return this.name();
	}
	
	public static List<TipoDeCombustivel> tiposParaAbastecimento() {
		List<TipoDeCombustivel> retorno = new ArrayList<TipoDeCombustivel>();
		TipoDeCombustivel[] todos = TipoDeCombustivel.values();
		
		for (int i = 0; i < todos.length; i++) {
			TipoDeCombustivel tipo = todos[i];
			if(tipo.getExibirNoAbastecimento()) {
				retorno.add(tipo);
			}
		}
		
		return retorno;
	}
	
}
