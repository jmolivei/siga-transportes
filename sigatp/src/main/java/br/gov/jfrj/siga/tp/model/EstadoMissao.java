package br.gov.jfrj.siga.tp.model;

public enum EstadoMissao {

	PROGRAMADA("PROGRAMADA"), INICIADA("INICIADA"),FINALIZADA("FINALIZADA"), CANCELADA("CANCELADA") ;
	
	private String descricao;
	
	EstadoMissao(String descricao){
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
	
	public boolean equals(EstadoMissao outroEstado) {
		return this.descricao.equals(outroEstado.getDescricao());
	}
	
	/**
	 * Sobrecarga criada para auxiliar o desenvolvimento 
	 * na camada de visao. 
	 * 
	 * @param outroEstado String de descricao do estado que se quer verificar.
	 * @return se a string passada identifica o estado
	 */
	public boolean equals(String outroEstado) {
		return this.descricao.equals(outroEstado);
	}
	
}
