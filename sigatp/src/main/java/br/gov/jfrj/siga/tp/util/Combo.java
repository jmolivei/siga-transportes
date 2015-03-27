package br.gov.jfrj.siga.tp.util;

import java.lang.reflect.Method;

import play.mvc.Scope.RenderArgs;

public enum Combo {

	Cor("cores","models"), Grupo("grupos","models"), DpLotacao("dpLotacoes","br.gov.jfrj.siga.dp"), CategoriaCNH("categoriasCNH","models"),Fornecedor("fornecedores","models"), Veiculo("veiculos","models");
	
	private String descricao;
	private String pacote;
	
	Combo(String descricao, String pacote){
		this.setDescricao(descricao);
		this.setPacote(pacote);
	}

	private void setPacote(String pacote) {
		this.pacote = pacote;
	}
	
	public String getPacote() {
		return this.pacote;
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
		
	public String nomeCompletoDaClasse() {
		return this.pacote + "." +  this.name();
	}
	
	/**
	 * Método responsável por renderizar as listas que serão exibidas aas combos dos forms.
	 * Foi utilizado uma convenção abaixo:
	 *     Passado o parâmetro Combo.Cor , é retornado o atributo cores que representa a 
	 *     lista de cores.
	 * Veja o exemplo da inclusão de um novo enumerator
	 *     Avarias("avarias")
	 * @param parametrosRequest - RenderArgs
	 * @param args - Lista dos enumerators Combo
	 * @return
	 * @throws Exception 
	 */
	public static RenderArgs montar(RenderArgs parametrosRequest,Combo... args) throws Exception {
		for (Combo combo : args) {		
				Class<?> nomeDaClasse = Class.forName(combo.nomeCompletoDaClasse());
				Method  metodo = nomeDaClasse.getDeclaredMethod ("findAll");
				parametrosRequest.put(combo.getDescricao(),metodo.invoke (null));
		}
		return parametrosRequest;
	}
	
}
