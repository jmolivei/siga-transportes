package br.gov.jfrj.siga.tp.vraptor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.validator.ValidationMessage;
import br.com.caelum.vraptor.view.Results;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdmin;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissao;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissaoComplexo;
import br.gov.jfrj.siga.tp.model.Afastamento;
import br.gov.jfrj.siga.tp.model.Condutor;
import br.gov.jfrj.siga.tp.model.ItemMenu;
import br.gov.jfrj.siga.tp.model.Missao;
import br.gov.jfrj.siga.tp.model.Plantao;
import br.gov.jfrj.siga.tp.model.TpDao;
import br.gov.jfrj.siga.vraptor.SigaObjects;

@Resource
@Path("/app/plantao")
public class PlantaoController extends TpController {

	public PlantaoController(HttpServletRequest request, Result result, CpDao dao, Localization localization, Validator validator, SigaObjects so, EntityManager em) throws Exception {
		super(request, result, TpDao.getInstance(), validator, so, em);
	}

	@Path("/listarPorCondutor/{idCondutor}")
	public void listarPorCondutor(Long idCondutor) throws Exception {
		Condutor condutor = buscaCondutor(idCondutor);
		List<Plantao> plantoes = Plantao.buscarTodosPorCondutor(condutor);
		
		MenuMontador.instance(result).recuperarMenuCondutores(idCondutor, ItemMenu.PLANTOES);
		
		result.include("plantoes", plantoes);
		result.include("condutor", condutor);
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/editar/{idCondutor}/{id}")
	public void editar(Long idCondutor, Long id) throws Exception {
		Plantao plantao;
		if (id > 0)
			plantao = Plantao.AR.findById(id);
		else {
			plantao = new Plantao();
			plantao.setCondutor(buscaCondutor(idCondutor));
		}

		MenuMontador.instance(result).recuperarMenuCondutores(idCondutor, ItemMenu.PLANTOES);
		result.include("plantao", plantao);
		result.include("idCond", idCondutor);
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/incluir/{idCondutor}")
	public void incluir(Long idCondutor) throws Exception {
		result.redirectTo(this).editar(idCondutor, 0L);
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/salvar")
	public void salvar(@Valid Plantao plantao, Calendar dataHoraInicioNova, Calendar dataHoraFimNova)
			throws Exception {
		Long idCondutor = setaCondutor(plantao);
		Long idPlantao = isEdicao(plantao) ? plantao.id : 0L;

		error(!plantao.ordemDeDatasCorreta(), "dataHoraInicio", "plantoes.dataHoraInicio.validation");

		String listaAfastamento = "";
		List<Afastamento> afastamentos = Afastamento.buscarPorCondutores(
				plantao.condutor.getId(), formatarData(plantao.dataHoraInicio),
				formatarData(plantao.dataHoraFim));

		for (Afastamento item : afastamentos) {
			listaAfastamento += listaAfastamento == "" ? "" : ",";
			listaAfastamento += formatarData(item.getDataHoraInicio()) + " a "
					+ formatarData(item.getDataHoraFim());
		}
		
		if(!listaAfastamento.equals(""))
			validator.add(new ValidationMessage("Condutor afastado " + getMensagemPeriodo(listaAfastamento) + " de: " + listaAfastamento + ".", "plantao"));

		if (validator.hasErrors()) {
			result.include("plantao", plantao);
			redirecionaPaginaCasoOcorraErros(idCondutor, idPlantao);
		} else {
			String listaPlantao = "";
			List<Plantao> plantoes = Plantao.buscarPorCondutores(
					plantao.condutor.getId(),
					formatarData(plantao.dataHoraInicio),
					formatarData(plantao.dataHoraFim));

			for (Plantao item : plantoes) {
				listaPlantao += listaPlantao == "" ? "" : ",";
				listaPlantao += formatarData(item.dataHoraInicio) + " a "
						+ formatarData(item.dataHoraFim);
			}

			if(!listaPlantao.equals(""))
				validator.add(new ValidationMessage("Condutor em plant&atilde;o " + getMensagemPeriodo(listaPlantao) + " de: " + listaPlantao + ".", "plantao"));
		}

		if (validator.hasErrors()) {
			result.include("plantao", plantao);
			redirecionaPaginaCasoOcorraErros(idCondutor, idPlantao);
		} else {
			if (isEdicao(plantao)) {
				if (!(plantao.dataHoraInicio.before(dataHoraInicioNova) && plantao.dataHoraFim
						.after(dataHoraFimNova))) {
					List<Missao> missoes = retornarMissoesCondutorPlantao(
							plantao, dataHoraInicioNova, dataHoraFimNova);
					String listaMissoes = "";

					for (Missao item : missoes) {
						listaMissoes += listaMissoes == "" ? "" : ",";
						listaMissoes += item.getSequence();
					}
					error(missoes.size() > 0, "LinkErroCondutor", listaMissoes);
				}
			}

			if (validator.hasErrors()) {
				result.include("plantao", plantao);
				redirecionaPaginaCasoOcorraErros(idCondutor, idPlantao);
			} else {
				plantao.save();
				result.forwardTo(this).listarPorCondutor(idCondutor);
			}
		}
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/excluir/{id}")
	public void excluir(Long id) throws Exception {
		Plantao plantao = Plantao.AR.findById(id);

		List<Missao> missoes = retornarMissoesCondutorPlantao(plantao, null, null);
		String listaMissoes = "";
		String delimitador = "";

		for (Missao item : missoes) {
			listaMissoes += delimitador;
			listaMissoes += item.getSequence();
			delimitador = ",";
		}

		error(missoes.size() > 0, "LinkErroCondutor", listaMissoes);

		if (validator.hasErrors())
			redirecionaPaginaCasoOcorraErros(plantao.condutor.getId(), id);
		else {
			plantao.delete();
			result.forwardTo(this).listarPorCondutor(plantao.condutor.getId());
		}
	}

	private Condutor buscaCondutor(Long idCondutor) throws Exception {
		return Condutor.AR.findById(idCondutor);
	}

	private static String formatarData(Calendar data) {
		return String.format("%02d", data.get(Calendar.DAY_OF_MONTH)) + "/"
				+ String.format("%02d", data.get(Calendar.MONTH) + 1) + "/"
				+ String.format("%04d", data.get(Calendar.YEAR));
	}
	
	private String getMensagemPeriodo(String lista) {
		if(lista != null) 
			return lista.contains(",") ? "nos per&iacute;odos": "no per&iacute;odo";
		
		return "";
	}

	private boolean isEdicao(Plantao plantao) {
		return plantao.id > 0;
	}

	private Long setaCondutor(Plantao plantao) throws Exception {
		Long idCondutor = plantao.condutor.getId();
		plantao.setCondutor(buscaCondutor(idCondutor));
		return idCondutor;
	}

	private void redirecionaPaginaCasoOcorraErros(Long idCondutor, Long idPlantao) throws Exception {
		validator.onErrorUse(Results.logic()).forwardTo(PlantaoController.class).editar(idCondutor, idPlantao);
	}

	private List<Missao> retornarMissoesCondutorPlantao(Plantao plantao,
			Calendar dataHoraInicioNova, Calendar dataHoraFimNova) {
		List<Missao> retorno = new ArrayList<Missao>();

		if (dataHoraInicioNova == null && dataHoraFimNova == null) {
			return Missao.retornarMissoes("condutor.id", plantao.condutor
					.getId(), plantao.condutor.getCpOrgaoUsuario().getId(),
					plantao.dataHoraInicio, plantao.dataHoraFim);
		}

		if (dataHoraInicioNova != null) {
			if (dataHoraInicioNova.after(plantao.dataHoraInicio)) {
				retorno.addAll(Missao.retornarMissoes("condutor.id",
						plantao.condutor.getId(), plantao.condutor
								.getCpOrgaoUsuario().getId(),
						dataHoraInicioNova, plantao.dataHoraInicio));
			}
		}

		if (dataHoraFimNova != null) {
			if (dataHoraFimNova.before(plantao.dataHoraFim)) {
				retorno.addAll(Missao.retornarMissoes("condutor.id",
						plantao.condutor.getId(), plantao.condutor
								.getCpOrgaoUsuario().getId(),
						plantao.dataHoraFim, dataHoraFimNova));
			}
		}

		return retorno;
	}
	
}