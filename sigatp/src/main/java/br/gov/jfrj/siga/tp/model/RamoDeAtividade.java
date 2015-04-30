package br.gov.jfrj.siga.tp.model;

public enum RamoDeAtividade{

	COMBUSTIVEL("COMBUST͍VEL"), MANUTENCAO("MANUTEN�ÃO"), 
	VEICULOS("VE�CULOS"), PECAS("PEǇAS"), PNEUS("PNEUS"),
	BATERIAS("BATERIAS"), MATERIALDELIMPEZA("MATERIAL DE LIMPEZA"),
	CONSERVACAO("CONSERVA��O");
	
	private String descricao;
	
	RamoDeAtividade(String descricao){
		this.setDescricao(descricao);
	}

	private void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public String getDescricao() {
		return this.descricao;
	}
	
	@Override
	public String toString() {
		return this.name();
	}
	
}
