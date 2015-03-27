package controllers.rest;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import br.gov.jfrj.siga.tp.model.TipoDePassageiro;
import play.mvc.Controller;

public class TiposDePassageiro extends Controller {
	public static void ver(String id) throws Exception {
		ObjectMapper oM = new ObjectMapper();
		//oM.setVisibility(JsonMethod.FIELD, Visibility.ANY);
		ObjectWriter oW = oM.writer().withDefaultPrettyPrinter();
		String json = oW.writeValueAsString(TipoDePassageiro.valuesString());

		renderText(json);
	}
	
	public static void incluir() throws Exception {
		renderText("Funcao nao implementada");
	}
	
	public static void alterar(Long id) throws Exception {
		renderText("Funcao nao implementada");
	}
	
	public static void excluir(Long id) throws Exception {
		renderText("Funcao nao implementada");
	}
	

}
