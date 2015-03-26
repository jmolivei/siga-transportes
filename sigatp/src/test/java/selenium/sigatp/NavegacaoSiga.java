package selenium.sigatp;

import selenium.ferramentas.Pagina;

public class NavegacaoSiga {
	
	private static final String _URL_SIGA_LOGIN = "http://localhost/siga/principal.action";
	
	private Pagina pagina;
	
	public NavegacaoSiga(Pagina pagina) {
		this.pagina = pagina;
	}
	
	public void acessarSiga() {
		pagina.acessarViaGet(_URL_SIGA_LOGIN);
	}
	
	public void logar(String login, String senha) {
		pagina.preencherCampoFormulario("j_username", login);
		pagina.preencherCampoFormulario("j_password", senha);
		
		pagina.clicarElementoPorXpath("//input[@value='Acessar']");
	}
	
	public Pagina getPagina() {
		return pagina;
	}

}
