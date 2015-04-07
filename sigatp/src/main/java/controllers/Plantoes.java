package controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import play.data.binding.As;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.mvc.Controller;
import play.mvc.With;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdmin;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissao;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissaoComplexo;
import br.gov.jfrj.siga.tp.model.Afastamento;
import br.gov.jfrj.siga.tp.model.Condutor;
import br.gov.jfrj.siga.tp.model.ItemMenu;
import br.gov.jfrj.siga.tp.model.Missao;
import br.gov.jfrj.siga.tp.model.Plantao;
import br.gov.jfrj.siga.tp.util.MenuMontador;

@With(AutorizacaoGIAntigo.class)
public class Plantoes extends Controller {
	
	public static void listarPorCondutor(Long idCondutor) throws Exception {
		Condutor condutor = Condutor.AR.findById(idCondutor);
		List<Plantao> plantoes = Plantao.buscarTodosPorCondutor(condutor);
		MenuMontador.instance().recuperarMenuCondutores(idCondutor, ItemMenu.PLANTOES);
		render(plantoes, condutor);
	}
	
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void incluir(Long idCondutor) throws Exception{
		Condutor condutor = Condutor.AR.findById(idCondutor);
		Plantao plantao = new Plantao();
		plantao.condutor = condutor;
		render(plantao);
	}
	
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void editar(Long id){
		Plantao plantao = Plantao.findById(id);
		render(plantao);
	}
	
	private static String formatarData(Calendar data) {
		return String.format("%02d",data.get(Calendar.DAY_OF_MONTH)) + "/" + String.format("%02d",data.get(Calendar.MONTH) + 1) + "/" + String.format("%04d",data.get(Calendar.YEAR));
	}
	
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void salvar(@Valid Plantao plantao, 
							  @As(lang={"*"}, value={"dd/MM/yyyy HH:mm"}) Calendar dataHoraInicioNova,
							  @As(lang={"*"}, value={"dd/MM/yyyy HH:mm"}) Calendar dataHoraFimNova) throws Exception{
		String template = plantao.id > 0 ? "Plantoes/editar.html" : "Plantoes/incluir.html";
		
		if (!plantao.ordemDeDatasCorreta()){
			Validation.addError("dataHoraInicio", "plantoes.dataHoraInicio.validation");
			renderTemplate(template, plantao);			
		}
		
		String listaAfastamento = "";
		List<Afastamento> afastamentos = Afastamento.buscarPorCondutores(plantao.condutor.getId(), formatarData(plantao.dataHoraInicio),
													 formatarData(plantao.dataHoraFim));
		
		for (Afastamento item : afastamentos) {
			listaAfastamento += listaAfastamento == "" ? "" : ",";
			listaAfastamento += formatarData(item.getDataHoraInicio()) + " a " + formatarData(item.getDataHoraFim());
		}
		
		if (!listaAfastamento.equals("")) {
			String plural = listaAfastamento.contains(",") ? "nos per&iacute;odos" : "no per&iacute;odo";
			Validation.addError("plantao", "Condutor afastado " + plural + " de: " + listaAfastamento  + ".");
		}
		
		if(Validation.hasErrors()){
			renderTemplate(template, plantao);
		}
		else {
			String listaPlantao = "";
			List<Plantao> plantoes= Plantao.buscarPorCondutores(plantao.condutor.getId(), formatarData(plantao.dataHoraInicio),
					 formatarData(plantao.dataHoraFim));

			for (Plantao item : plantoes) {
				listaPlantao += listaPlantao == "" ? "" : ",";
				listaPlantao += formatarData(item.dataHoraInicio) + " a " + formatarData(item.dataHoraFim);
			}

			if (!listaPlantao.equals("")) {
				String plural = listaPlantao.contains(",") ? "nos per&iacute;odos" : "no per&iacute;odo";
				Validation.addError("plantao", "Condutor em plant&atilde;o " + plural + " de: " + listaPlantao + ".");
			}
		}
		
		if(Validation.hasErrors()){
			renderTemplate(template, plantao);
		}
		else{
			if(template.contains("editar")) {
				if (!(plantao.dataHoraInicio.before(dataHoraInicioNova) &&	plantao.dataHoraFim.after(dataHoraFimNova)))
				{
					List<Missao> missoes = retornarMissoesCondutorPlantao(plantao, dataHoraInicioNova, dataHoraFimNova);
					String listaMissoes = "";
				
					for (Missao item : missoes) {
						listaMissoes += listaMissoes == "" ? "" : ",";
						listaMissoes += item.getSequence();
					}
	
		    		if (missoes.size() > 0) {	    			
		        		Validation.addError("LinkErroCondutor", listaMissoes);
		    		}
				}
			}
			
			if (Validation.hasErrors()) {
    			renderTemplate(template, plantao);
			}
			else {
				plantao.save();
				listarPorCondutor(plantao.condutor.getId());
			}
		}	
	}	
	
	private static List<Missao> retornarMissoesCondutorPlantao(Plantao plantao,Calendar dataHoraInicioNova,Calendar dataHoraFimNova) {
		List<Missao> retorno = new ArrayList<Missao>();
		
		if (dataHoraInicioNova == null && dataHoraFimNova == null) {		
			return Missao.retornarMissoes("condutor.id", plantao.condutor.getId(), plantao.condutor.getCpOrgaoUsuario().getId(),
									plantao.dataHoraInicio,plantao.dataHoraFim);
		}
		
		if (dataHoraInicioNova != null) {
			if (dataHoraInicioNova.after(plantao.dataHoraInicio)) {
				retorno.addAll(Missao.retornarMissoes("condutor.id", plantao.condutor.getId(), plantao.condutor.getCpOrgaoUsuario().getId(),
										   	   dataHoraInicioNova,plantao.dataHoraInicio));
			}
		}
		
		if (dataHoraFimNova != null) {
			if (dataHoraFimNova.before(plantao.dataHoraFim)) {
				retorno.addAll(Missao.retornarMissoes("condutor.id", plantao.condutor.getId(), plantao.condutor.getCpOrgaoUsuario().getId(),
											   plantao.dataHoraFim,dataHoraFimNova));
			}
		}
		
		return retorno;
	}
	
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void excluir(Long id) throws Exception{
		Plantao plantao = Plantao.findById(id);

		List<Missao> missoes = retornarMissoesCondutorPlantao(plantao, null, null);
		String listaMissoes = "";
		String delimitador="";
		
		for (Missao item : missoes) {
			listaMissoes += delimitador;
			listaMissoes += item.getSequence();
			delimitador=",";
		}

		if (missoes.size() > 0) {
    		Validation.addError("LinkErroCondutor", listaMissoes);
		}

		if (Validation.hasErrors()) {
			renderTemplate("Afastamentos/editar.html", plantao);
		}		
		else {
			plantao.delete();
			listarPorCondutor(plantao.condutor.getId());
		}
	}

	
}
