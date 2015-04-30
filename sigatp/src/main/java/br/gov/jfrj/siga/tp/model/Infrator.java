package br.gov.jfrj.siga.tp.model;

public enum Infrator {

	CONDUTOR("CONDUTOR"), PROPRIETARIO("PROPRIETÁRIO"), PESSOA_FISICA("PESSOA FÍSICA"), 
	PESSOA_FIS_JUR("PESSOA FÍSICA/JURÍDICA"), TRANSPORTADOR("TRANSPORTADOR"), EXPEDIDOR("EXPEDIDOR");

	private String descricao;
	
	Infrator(String descricao){
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
