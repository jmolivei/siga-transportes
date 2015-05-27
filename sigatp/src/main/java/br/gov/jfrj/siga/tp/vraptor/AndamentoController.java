package br.gov.jfrj.siga.tp.vraptor;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import play.data.validation.Valid;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Http;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.validator.I18nMessage;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.tp.model.Andamento;
import br.gov.jfrj.siga.tp.model.EstadoRequisicao;
import br.gov.jfrj.siga.tp.model.RequisicaoTransporte;
import br.gov.jfrj.siga.tp.model.TpDao;
import br.gov.jfrj.siga.vraptor.SigaObjects;
import controllers.Application;
import controllers.Requisicoes;

@Path("/app/andamento/")
@Resource
public class AndamentoController extends TpController{

	public AndamentoController(HttpServletRequest request, Result result, Validator validator, SigaObjects so, EntityManager em) {
		super(request, result, TpDao.getInstance(), validator, so, em);
	}

	@Path("listarPorRequisicao/{idRequisicao}/{popUp}")
	public void listarPorRequisicao(Long idRequisicao, boolean popUp) throws Exception {
		RequisicaoTransporte requisicaoTransporte = RequisicaoTransporte.AR.findById(idRequisicao);
		List<Andamento> andamentos = Andamento.AR.find("requisicaoTransporte = ? order by id desc", requisicaoTransporte).fetch();
		MenuMontador.instance(result).recuperarMenuRequisicoes(idRequisicao, popUp, popUp);
		result.include("andamentos", andamentos);
		result.include("requisicaoTransporte", requisicaoTransporte);
	}

//	@RoleAdmin
//	@RoleAdminFrota
//	@RoleAdminMissao
//	@RoleAprovador
	@Path("salvar/{andamento}")
	public void salvar(@Valid Andamento andamento) throws Exception {
		if (andamento.getRequisicaoTransporte().getUltimoEstado() == EstadoRequisicao.CANCELADA) {
			validator.add(new I18nMessage("estadoRequisicao", "andamentos.estadoRequisicao.validation"));
			redirecionarSeErroAoSalvar(andamento);
		}

		if (andamento.getEstadoRequisicao() == EstadoRequisicao.CANCELADA
		||  andamento.getEstadoRequisicao() == EstadoRequisicao.REJEITADA)  {

			if(validator.hasErrors())
			//validation.required(andamento.descricao);
				redirecionarSeErroAoSalvar(andamento);
		}

		if (andamento.getEstadoRequisicao() == EstadoRequisicao.CANCELADA) {
			if (andamento.getRequisicaoTransporte().cancelar(getCadastrante(), "CANCELADA")) {
				Application.index();
			}
			else {
				validator.add(new I18nMessage("estadoRequisicao", "andamentos.estadoRequisicao.andamento.validation"));
				//Validation.addError("estadoRequisicao", "andamentos.estadoRequisicao.andamento.validation");
				redirecionarSeErroAoSalvar(andamento);
			}
		} else {
			DpPessoa dpPessoa = getCadastrante();
			andamento.setResponsavel(dpPessoa);
			andamento.setDataAndamento(Calendar.getInstance());
			redirecionarSeErroAoSalvar(andamento);
			andamento.save();
			Requisicoes.listarPAprovar();
		}
	}

	private void redirecionarSeErroAoSalvar(Andamento andamento) throws Exception {
		if(validator.hasErrors()){
			MenuMontador.instance(result).recuperarMenuRequisicoes(andamento.getRequisicaoTransporte().getId(), false, false);
			result.include("andamento", andamento);
			switch (andamento.getEstadoRequisicao()) {
			case AUTORIZADA:
				result.redirectTo(this).autorizar(andamento.getId());
				break;
			case CANCELADA:
				result.redirectTo(this).cancelar(andamento.getId());
				break;
			case REJEITADA:
				result.redirectTo(this).rejeitar(andamento.getId());
				break;
			default:
				break;
			}
		}
	}


	@Before(priority=200,only={"autorizar","cancelar","rejeitar"})
	protected void montarAndamentos() throws Exception {
//		Long id = params.get("id", Long.class);
		Long id = Long.valueOf((String) getRequest().getAttribute("id"));
		Andamento andamento = new Andamento();
		andamento.setRequisicaoTransporte(RequisicaoTransporte.AR.findById(id));
		String acaoExecutada = Http.Request.current().actionMethod;
		acaoExecutada = (acaoExecutada.substring(0, acaoExecutada.length()-1) + "DA").toUpperCase();
		andamento.setEstadoRequisicao(EstadoRequisicao.valueOf(acaoExecutada));

		//TODO verificar a necessidade do ultimo true
		MenuMontador.instance(result).recuperarMenuRequisicoes(id, false, true);

		result.include("andamento", andamento);
	}

//	@RoleAdmin
//	@RoleAdminMissao
//	@RoleAprovador
	@Path("autorizar/{id}")
	public void autorizar(Long id) throws Exception {
		Andamento andamento = (Andamento) result.included().get("andamento");
		if (Http.Request.current().actionMethod.equals("autorizar") || Http.Request.current().actionMethod.equals("rejeitar")) {
			if (andamento.getRequisicaoTransporte().getUltimoAndamento().getEstadoRequisicao() != EstadoRequisicao.AUTORIZADA &&
				andamento.getRequisicaoTransporte().getUltimoAndamento().getEstadoRequisicao() != EstadoRequisicao.REJEITADA &&
				andamento.getRequisicaoTransporte().getUltimoAndamento().getEstadoRequisicao() != EstadoRequisicao.ABERTA) {
				throw new Exception(Messages.get("andamentos.autorizarOuCancelar.exception", andamento.getRequisicaoTransporte().buscarSequence()));
			}
		}
	}

//	@RoleAdmin
//	@RoleAdminMissao
//	@RoleAprovador
	@Path("cancelar/{id}")
	public void cancelar(Long id) throws Exception {
		Andamento andamento = (Andamento) result.included().get("andamento");
		if (Http.Request.current().actionMethod.equals("autorizar") || Http.Request.current().actionMethod.equals("rejeitar")) {
			if (andamento.getRequisicaoTransporte().getUltimoAndamento().getEstadoRequisicao() != EstadoRequisicao.AUTORIZADA &&
				andamento.getRequisicaoTransporte().getUltimoAndamento().getEstadoRequisicao() != EstadoRequisicao.REJEITADA &&
				andamento.getRequisicaoTransporte().getUltimoAndamento().getEstadoRequisicao() != EstadoRequisicao.ABERTA) {
				throw new Exception(Messages.get("andamentos.autorizarOuCancelar.exception", andamento.getRequisicaoTransporte().buscarSequence()));
			}
		}
	}

//	@RoleAdmin
//	@RoleAdminMissao
//	@RoleAprovador
	@Path("rejeitar/{id}")
	public void rejeitar(Long id) {
	}
}
