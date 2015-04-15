package controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import play.data.validation.Valid;
import play.data.validation.Validation;
import play.mvc.Controller;
import play.mvc.With;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdmin;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminFrota;
import br.gov.jfrj.siga.tp.model.Andamento;
import br.gov.jfrj.siga.tp.model.Avaria;
import br.gov.jfrj.siga.tp.model.EstadoRequisicao;
import br.gov.jfrj.siga.tp.model.EstadoServico;
import br.gov.jfrj.siga.tp.model.Missao;
import br.gov.jfrj.siga.tp.model.RequisicaoTransporte;
import br.gov.jfrj.siga.tp.model.ServicoVeiculo;
import br.gov.jfrj.siga.tp.model.TiposDeServico;
import br.gov.jfrj.siga.tp.model.Veiculo;
import br.gov.jfrj.siga.tp.util.MenuMontador;
import br.gov.jfrj.siga.tp.util.SigaTpException;

@With(AutorizacaoGIAntigo.class)
public class ServicosVeiculo extends Controller {
	@RoleAdmin
	@RoleAdminFrota
	public static void incluir() throws Exception {
		ServicoVeiculo servico = new ServicoVeiculo();
		EstadoServico estadoServico = EstadoServico.AGENDADO;
		MenuMontador.instance().recuperarMenuServicoVeiculo(servico.id, estadoServico);
	 	montarCombos(servico.id);
		render(servico, estadoServico);
	}
	
	@RoleAdmin
	@RoleAdminFrota
	public static void cancelar() throws Exception {
		ServicoVeiculo servico = new ServicoVeiculo();
		EstadoServico estadoServico = EstadoServico.CANCELADO;
		MenuMontador.instance().recuperarMenuServicoVeiculo(servico.id, estadoServico);
	 	montarCombos(servico.id);
		render(servico, estadoServico);
	}
	
	@RoleAdmin
	@RoleAdminFrota
	public static void salvar(@Valid ServicoVeiculo servico, List<Avaria> avarias) throws Exception {
		DpPessoa dpPessoa = AutorizacaoGIAntigo.cadastrante();
		servico.cpOrgaoUsuario = AutorizacaoGIAntigo.titular().getOrgaoUsuario();
		servico.setSequence(servico.cpOrgaoUsuario);
		servico.executor = dpPessoa;
		String template;
		boolean novoServico = false;
		
		if (servico.id == 0) {
			novoServico = true;
			servico.dataHora = Calendar.getInstance();
			servico.situacaoServico = EstadoServico.AGENDADO;
			servico.dataHoraInicio = servico.dataHoraInicioPrevisto;
			servico.dataHoraFim = servico.dataHoraFimPrevisto;
			template = "@incluir";
		}
		else{
			servico.ultimaAlteracao = Calendar.getInstance();
			template = "@editar";
		}

		if (servico.situacaoServico == EstadoServico.REALIZADO) {
			if (avarias != null && avarias.size() > 0) {
				for (Avaria avaria : avarias) {
					avaria = Avaria.AR.findById(avaria.getId());
					avaria.setDataDeSolucao(Calendar.getInstance());
					avaria.save();
					redirecionarSeErroAoSalvar(servico, template);
				}
			}
		}
		
		if(servico.dataHoraInicioPrevisto != null & servico.dataHoraFimPrevisto != null && (!servico.ordemDeDatasPrevistas(servico.dataHoraInicioPrevisto, servico.dataHoraFimPrevisto))){
			Validation.addError("dataHoraFimPrevisto", "servicosVeiculo.dataHoraFimPrevisto.validation");
			redirecionarSeErroAoSalvar(servico, template);
		}
		
		if (servico.situacaoServico == EstadoServico.CANCELADO) {
			verificarMotivoCancelamentoPreenchido(servico);
			if (servico.tiposDeServico.getGeraRequisicao()) {
				String descricaoRequisicao = "Cancelado pelo servico " + servico.getSequence();
				servico.requisicaoTransporte.cancelar(dpPessoa, descricaoRequisicao);
			}
			redirecionarSeErroAoSalvar(servico, template);
		}
		
		if (servico.dataHoraInicio != null && servico.dataHoraFim != null) {
			if (!servico.ordemDeDatasPrevistas(servico.dataHoraInicio, servico.dataHoraFim)){
				Validation.addError("dataHoraFim", "servicosVeiculo.dataHoraFim.validation");
			}
			redirecionarSeErroAoSalvar(servico, template);
		}

		if (novoServico && servico.tiposDeServico.getGeraRequisicao())
		{
			verificarDescricaoPreenchida(servico);
			redirecionarSeErroAoSalvar(servico, template);

			List<Missao> missoes = 	Missao.retornarMissoes("veiculo.id", servico.veiculo.getId(), servico.cpOrgaoUsuario.getId(),
									   servico.dataHoraInicio,servico.dataHoraFim);
			String listaMissoes = "";
			
			String delimitador="";
			for (Missao item : missoes) {
				listaMissoes += delimitador;
				listaMissoes += item.getSequence();
				delimitador=",";
			}

    		if (missoes.size() > 0) {
        		Validation.addError("LinkErroVeiculo", listaMissoes);
    		}
			redirecionarSeErroAoSalvar(servico, template);

			servico.requisicaoTransporte = gravarRequisicao(servico);

			String descricaoRequisicao = "Aberta para o servico " + servico.getSequence();
        	gravarAndamentosRequisicao(EstadoRequisicao.ABERTA, dpPessoa, descricaoRequisicao, 
        							   servico.requisicaoTransporte);

        	descricaoRequisicao = "Autorizada para o servico " + servico.getSequence();
        	gravarAndamentosRequisicao(EstadoRequisicao.AUTORIZADA, dpPessoa, descricaoRequisicao, 
        							   servico.requisicaoTransporte);
		}
		
		servico.save();
	 	montarCombos(servico.id);
		listarFiltrado(servico.situacaoServico);
	}
	
	public static void listar() {
	 	CpOrgaoUsuario cpOrgaoUsuario = AutorizacaoGIAntigo.titular().getOrgaoUsuario();
		List<ServicoVeiculo> servicos = ServicoVeiculo.find("cpOrgaoUsuario", cpOrgaoUsuario).fetch();
	  	EstadoServico situacaoServico = EstadoServico.AGENDADO; 
	  	MenuMontador.instance().recuperarMenuServicosVeiculo(null);
	  	Collections.sort(servicos);
	  	render(servicos, situacaoServico);
	}
	
	public static void listarFiltrado(EstadoServico estado) {
		if(estado == null) {
			estado = EstadoServico.AGENDADO;
		}
		
		CpOrgaoUsuario cpOrgaoUsuario = AutorizacaoGIAntigo.titular().getOrgaoUsuario();
	  	List<ServicoVeiculo> servicos = ServicoVeiculo.find("cpOrgaoUsuario=? and situacaoServico = ?", cpOrgaoUsuario, estado).fetch();
	  	EstadoServico situacaoServico = EstadoServico.AGENDADO; 
	  	MenuMontador.instance().recuperarMenuServicosVeiculo(estado);	  	
	  	renderTemplate("@listar", servicos, situacaoServico);
	}

	private static void verificarMotivoCancelamentoPreenchido(ServicoVeiculo servico) {
		Boolean erro = false;

		try {
			if (servico.motivoCancelamento.isEmpty()) {
				erro = true;
			}
		} catch (NullPointerException ex) {
			erro = true;
		}

		if (erro) {
			Validation.addError("motivoCancelamento", "servicoVeiculo.motivoCancelamento.validation");
		}
	}
	
	private static void verificarDescricaoPreenchida(ServicoVeiculo servico) {
		Boolean erro = false;

		try {
			if (servico.descricao.isEmpty()) {
				erro = true;
			}
		} catch (NullPointerException ex) {
			erro = true;
		}

		if (erro) {
			Validation.required("descricao", "A Descricao nao pode estar vazia.");
		}
	}

	@RoleAdmin
	@RoleAdminFrota
	public static void editar(Long id) throws Exception {
		ServicoVeiculo servico = ServicoVeiculo.findById(id);
	 	montarCombos(servico.id);
		MenuMontador.instance().recuperarMenuServicoVeiculo(id, servico.situacaoServico);
		render(servico);
	}
	
	private static void gravarAndamentosRequisicao(EstadoRequisicao estadoRequisicao, DpPessoa dpPessoa, 
			String descricao,RequisicaoTransporte requisicaoTransporte) {
		Andamento andamento = new Andamento();
		andamento.descricao = descricao;
		andamento.dataAndamento = Calendar.getInstance();
		andamento.estadoRequisicao = estadoRequisicao;
		andamento.requisicaoTransporte = requisicaoTransporte;
		andamento.responsavel = dpPessoa;
		andamento.save();
	}
	
	private static RequisicaoTransporte gravarRequisicao(ServicoVeiculo servico) throws Exception {
		RequisicaoTransporte requisicaoTransporte = new RequisicaoTransporte();
		requisicaoTransporte.cpOrgaoUsuario = AutorizacaoGIAntigo.titular().getOrgaoUsuario();
		requisicaoTransporte.setSequence(requisicaoTransporte.cpOrgaoUsuario);
		requisicaoTransporte.solicitante = AutorizacaoGIAntigo.cadastrante();
		requisicaoTransporte.dataHoraSaidaPrevista = servico.dataHoraInicioPrevisto;
		requisicaoTransporte.dataHoraRetornoPrevisto = servico.dataHoraFimPrevisto;
		requisicaoTransporte.finalidade = servico.descricao;
		requisicaoTransporte.passageiros = "Requisicao para Servico";
		requisicaoTransporte.itinerarios = "Requisicao para Servico";
		requisicaoTransporte.cpComplexo = AutorizacaoGIAntigo.recuperarComplexoPadrao();

		if(requisicaoTransporte.id == 0) {
			requisicaoTransporte.dataHora = Calendar.getInstance();
		}
		
		requisicaoTransporte.save();
		return requisicaoTransporte;
	}
	
	@RoleAdmin
	@RoleAdminFrota
	public static void excluir(Long id) throws Exception {
		ServicoVeiculo servico = ServicoVeiculo.findById(id);

        if(! Validation.hasErrors()) {
        	if (servico.tiposDeServico != TiposDeServico.MANUTENCAOINTERNA) {
        		try {
        			servico.requisicaoTransporte.excluir(true);
        		} catch(SigaTpException ex) {
	        		Validation.addError("requisicaoTransporte", ex.getMessage());
	        		redirecionarSeErroAoSalvar(servico, "@ler");
        		}
       			listar();
        	}
        	else {
        		servico.delete();
        		listar();
        	}
        }
	}

	public static void buscarServico(String sequence, Boolean popUp) throws Exception {
		recuperarPelaSigla(sequence, popUp);
		renderTemplate("@ler");
	}
	
	public static void ler(Long id) throws Exception {
		ServicoVeiculo servico = ServicoVeiculo.findById(id);
		MenuMontador.instance().recuperarMenuServicoVeiculo(id, servico.situacaoServico);
	 	montarCombos(servico.id);
	  	render();
	}

	private static void redirecionarSeErroAoSalvar(ServicoVeiculo servico, String template) throws Exception {
		if(Validation.hasErrors()) 
		{
		 	MenuMontador.instance().recuperarMenuServicosVeiculo(servico.situacaoServico);
		 	montarCombos(servico.id);
		 	renderTemplate(template, servico);
		}
	}
	
	private static void montarCombos(Long id) throws Exception {
		List<Veiculo> veiculos = Veiculo.listarTodos(AutorizacaoGIAntigo.titular().getOrgaoUsuario());
		List<TiposDeServico> tiposDeServico = new ArrayList<TiposDeServico> (Arrays.asList(TiposDeServico.values()));
		List<EstadoServico> estadosServico = new ArrayList<EstadoServico>();
		List<Avaria> avarias = new ArrayList<Avaria>();
		
		if (id == 0) {
			estadosServico = (Arrays.asList(EstadoServico.AGENDADO));
		}
		else {
			ServicoVeiculo servico = ServicoVeiculo.findById(id);

			avarias = Avaria.buscarPendentesPorVeiculo(servico.veiculo);
			
			if (servico.situacaoServico.equals(EstadoServico.AGENDADO)) {
				estadosServico = (Arrays.asList(EstadoServico.getValuesComboIniciarServico()));
			}
			else if (servico.situacaoServico.equals(EstadoServico.INICIADO)) {
				estadosServico = (Arrays.asList(EstadoServico.getValuesComboFinalizarServico()));
			}
			else {
				estadosServico = (Arrays.asList(servico.situacaoServico));
			}
		}
		
		renderArgs.put("estadosServico",estadosServico);
		renderArgs.put("tiposDeServico",tiposDeServico);
		renderArgs.put("veiculos",veiculos);
		renderArgs.put("avarias", avarias);
	}

	protected static void recuperarPelaSigla(String sigla, Boolean popUp) throws Exception {
		ServicoVeiculo servico = ServicoVeiculo.buscar(sigla);
		MenuMontador.instance().recuperarMenuServicoVeiculo(servico.id, servico.situacaoServico);
		if(popUp != null) {
			renderArgs.put("mostrarMenu", !popUp);
		} else {
			renderArgs.put("mostrarMenu", true);
		}
		
		renderArgs.put("servico", servico);
	}
}