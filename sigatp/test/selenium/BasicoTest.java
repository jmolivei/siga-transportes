package selenium;

import org.junit.After;
import org.junit.Assert;

import selenium.ferramentas.Browser;
import selenium.ferramentas.Pagina;
import selenium.sigatp.NavegacaoSigaTp;
import play.test.UnitTest;

public class BasicoTest {

	private Pagina pagina;
	private NavegacaoSigaTp login;
	
	public NavegacaoSigaTp getLogin() {
		return login;
	}
	
	public Pagina getPagina() {
		return pagina;
	}
	
	public void inicializaTeste() throws Exception {
		Browser browser = Browser.valueOf(System.getProperty("nomeBrowser"));
		inicializaTeste(browser);
	}
	
	public void inicializaTeste(Browser browser) throws Exception {
		pagina = new Pagina(browser);
		login = new NavegacaoSigaTp(pagina);
		login.logar(System.getProperty("usuarioSiga"), System.getProperty("senhaSiga"));
	}
	
	//@Test
	public void acessarSigaTpChromeTest() throws Exception {
		inicializaTeste(Browser.CHROME);
		Assert.assertTrue(pagina.contemTexto("Lista de Requisi"));
	}
	
	//@Test
	public void acessarSigaTpFirefoxTest() throws Exception {
		inicializaTeste(Browser.FIREFOX);
		Assert.assertTrue(pagina.contemTexto("Lista de Requisi"));
	}
	
	//@Test
	public void acessarSigaTpIETest() throws Exception {
		inicializaTeste(Browser.INTERNETEXPLORER);
		Assert.assertTrue(pagina.contemTexto("Lista de Requisi"));
	}
	
	@After
	public void finaliza() {
		pagina.finalizar();
	}
	
	
}