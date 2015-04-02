package controllers.rest;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import play.mvc.Controller;
import br.gov.jfrj.siga.tp.rest.FinalidadeRequisicaoRest;

public class Finalidades extends Controller {
	
	public static void ver(Long id) throws Exception {
		ObjectMapper oM = new ObjectMapper();
		//oM.setVisibility(JsonMethod.FIELD, Visibility.ANY);
		ObjectWriter oW = oM.writer().withDefaultPrettyPrinter();
		String json;
		
		FinalidadeRequisicaoRest fin = FinalidadeRequisicaoRest.buscarFinalidade(id);
		if(fin != null) {
			json = oW.writeValueAsString(fin);
		} else {
			FinalidadeRequisicaoRest[] finalidades = FinalidadeRequisicaoRest.buscarFinalidades();
			json = oW.writeValueAsString(finalidades);
		}

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
