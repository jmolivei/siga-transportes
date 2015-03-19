package models;

public enum RamoDeAtividade{

	COMBUSTIVEL("COMBUSTÍVEL"), MANUTENCAO("MANUTENÇÃO"), 
	VEICULOS("VEÍCULOS"), PECAS("PEÇAS"), PNEUS("PNEUS"),
	BATERIAS("BATERIAS"), MATERIALDELIMPEZA("MATERIAL DE LIMPEZA"),
	CONSERVACAO("CONSERVAÇÃO");
	
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
