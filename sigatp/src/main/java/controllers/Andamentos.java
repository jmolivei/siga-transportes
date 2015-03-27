package controllers;

import java.util.Calendar;
import java.util.List;

import controllers.AutorizacaoGI.RoleAdmin;
import controllers.AutorizacaoGI.RoleAdminFrota;
import controllers.AutorizacaoGI.RoleAdminMissao;
import controllers.AutorizacaoGI.RoleAprovador;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.i18n.Messages;
import play.mvc.*;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.tp.model.Andamento;
import br.gov.jfrj.siga.tp.model.EstadoRequisicao;
import br.gov.jfrj.siga.tp.model.RequisicaoTransporte;
import br.gov.jfrj.siga.tp.util.MenuMontador;
import models.*;

@With(AutorizacaoGI.class)
public class Andamentos extends Controller {

	public static void listarPorRequisicao(Long idRequisicao, boolean popUp) {
		RequisicaoTransporte requisicaoTransporte = RequisicaoTransporte.findById(idRequisicao);
		List<Andamento> andamentos = Andamento.find("requisicaoTransporte = ? order by id desc",requisicaoTransporte).fetch();
		MenuMontador.instance().RecuperarMenuRequisicoes(idRequisicao, popUp, popUp);
		render(andamentos, requisicaoTransporte);
	}
	
	@RoleAdmin
	@RoleAdminFrota
	@RoleAdminMissao	
	@RoleAprovador
	public static void salvar(@Valid Andamento andamento) throws Exception {
		if (andamento.requisicaoTransporte.getUltimoEstado() == EstadoRequisicao.CANCELADA) {
			Validation.addError("estadoRequisicao", "andamentos.estadoRequisicao.validation");
			redirecionarSeErroAoSalvar(andamento);
		}
		
		if (andamento.estadoRequisicao == EstadoRequisicao.CANCELADA
		||  andamento.estadoRequisicao == EstadoRequisicao.REJEITADA)  {
			validation.required(andamento.descricao);
			redirecionarSeErroAoSalvar(andamento);
		}

		if (andamento.estadoRequisicao == EstadoRequisicao.CANCELADA) {
			if (andamento.requisicaoTransporte.cancelar(AutorizacaoGI.cadastrante(),"CANCELADA")) {
				Application.index();
			}
			else {
				Validation.addError("estadoRequisicao", "andamentos.estadoRequisicao.andamento.validation");
				redirecionarSeErroAoSalvar(andamento);
			}
		} else {
			DpPessoa dpPessoa = AutorizacaoGI.cadastrante();
			andamento.responsavel = dpPessoa;
			andamento.dataAndamento = Calendar.getInstance();
			redirecionarSeErroAoSalvar(andamento);
			andamento.save();
			Requisicoes.listarPAprovar();
		}
	}
	
	private static void redirecionarSeErroAoSalvar(Andamento andamento) {
		if(Validation.hasErrors()) 
		{
			MenuMontador.instance().RecuperarMenuRequisicoes(andamento.requisicaoTransporte.id, false, false);
			String template="";
			switch (andamento.estadoRequisicao) {
			case AUTORIZADA:
				template = "@autorizar";
				break;
			case CANCELADA:
				template = "@cancelar";
				break;
			case REJEITADA:
				template = "@rejeitar";
				break;
			default:
				break;
			}
			renderTemplate(template, andamento);
		}
	}
	
	@Before(priority=200,only={"autorizar","cancelar","rejeitar"})
	protected static void montarAndamentos() throws Exception {
		Long id = params.get("id", Long.class);
		Andamento andamento = new Andamento();
		andamento.requisicaoTransporte = RequisicaoTransporte.findById(id);
		String acaoExecutada = Http.Request.current().actionMethod;
		acaoExecutada = (acaoExecutada.substring(0, acaoExecutada.length()-1) + "DA").toUpperCase();
		andamento.estadoRequisicao = EstadoRequisicao.valueOf(acaoExecutada);
		
		//TODO verificar a necessidade do ultimo true 
		MenuMontador.instance().RecuperarMenuRequisicoes(id, false, true);
		
		renderArgs.put("andamento", andamento);
	}
	
	@RoleAdmin
	@RoleAdminMissao
	@RoleAprovador
	public static void autorizar(Long id) throws Exception {
		Andamento andamento = (Andamento) renderArgs.current().get("andamento");
		if (Http.Request.current().actionMethod.equals("autorizar") || Http.Request.current().actionMethod.equals("rejeitar")) {
			if (andamento.requisicaoTransporte.getUltimoAndamento().estadoRequisicao != EstadoRequisicao.AUTORIZADA &&
				andamento.requisicaoTransporte.getUltimoAndamento().estadoRequisicao != EstadoRequisicao.REJEITADA &&
				andamento.requisicaoTransporte.getUltimoAndamento().estadoRequisicao != EstadoRequisicao.ABERTA) {
				throw new Exception(Messages.get("andamentos.autorizarOuCancelar.exception", andamento.requisicaoTransporte.getSequence()));
			}
		}
		render();
	}
	
	@RoleAdmin
	@RoleAdminMissao	
	@RoleAprovador
	public static void cancelar(Long id) throws Exception {
		Andamento andamento = (Andamento) renderArgs.get("andamento");
		if (Http.Request.current().actionMethod.equals("autorizar") || Http.Request.current().actionMethod.equals("rejeitar")) {
			if (andamento.requisicaoTransporte.getUltimoAndamento().estadoRequisicao != EstadoRequisicao.AUTORIZADA &&
				andamento.requisicaoTransporte.getUltimoAndamento().estadoRequisicao != EstadoRequisicao.REJEITADA &&
				andamento.requisicaoTransporte.getUltimoAndamento().estadoRequisicao != EstadoRequisicao.ABERTA) {
				throw new Exception(Messages.get("andamentos.autorizarOuCancelar.exception", andamento.requisicaoTransporte.getSequence()));
			}
		}
		render();
	}
	
	@RoleAdmin
	@RoleAdminMissao
	@RoleAprovador
	public static void rejeitar(Long id) {
		render();
	}
}
