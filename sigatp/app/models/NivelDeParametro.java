package models;

public enum NivelDeParametro {

	GERAL(1), ORGAO(10), COMPLEXO(100), LOTACAO(1000), USUARIO(10000) ;
	
	private int nivel;
	
	NivelDeParametro(int nivel){
		this.nivel = nivel;
	}

	public int getNivel() {
		return this.nivel;
	}
	
	
}
