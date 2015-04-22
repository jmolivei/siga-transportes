package controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import play.data.validation.Valid;
import play.data.validation.Validation;
import play.db.jpa.JPA;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import play.templates.JavaExtensions;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdmin;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissao;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissaoComplexo;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAgente;
import br.gov.jfrj.siga.tp.model.Andamento;
import br.gov.jfrj.siga.tp.model.CategoriaCNH;
import br.gov.jfrj.siga.tp.model.Condutor;
import br.gov.jfrj.siga.tp.model.EscalaDeTrabalho;
import br.gov.jfrj.siga.tp.model.EstadoMissao;
import br.gov.jfrj.siga.tp.model.EstadoRequisicao;
import br.gov.jfrj.siga.tp.model.Missao;
import br.gov.jfrj.siga.tp.model.RequisicaoTransporte;
import br.gov.jfrj.siga.tp.model.RequisicaoVsEstado;
import br.gov.jfrj.siga.tp.model.Veiculo;
import br.gov.jfrj.siga.tp.util.MenuMontador;
import br.gov.jfrj.siga.tp.util.PerguntaSimNao;

@With(AutorizacaoGIAntigo.class)
public class Missoes extends Controller {

	@RoleAdmin
	@RoleAdminMissao
	@RoleAgente
	@RoleAdminMissaoComplexo
	public static void listar() throws Exception {
		Object[] parametros = { AutorizacaoGIAntigo.titular().getOrgaoUsuario(), EstadoMissao.PROGRAMADA, EstadoMissao.INICIADA, EstadoMissao.FINALIZADA, EstadoMissao.CANCELADA };

		List<Missao> missoes = recuperarMissoes("cpOrgaoUsuario = ? and estadoMissao in (?,?,?,?)", parametros);

		EstadoMissao estadoMissao = EstadoMissao.PROGRAMADA;

		MenuMontador.instance().recuperarMenuMissoes(null);

		Condutor condutor = new Condutor();

		render(missoes, estadoMissao, condutor);
	}

	private static List<Missao> recuperarMissoes(String criterioBusca, Object[] parametros) throws Exception {

		if (!AutorizacaoGIAntigo.ehAdministrador() && !AutorizacaoGIAntigo.ehAdministradorMissao() && !AutorizacaoGIAntigo.ehAdministradorMissaoPorComplexo()) {
			Condutor condutorLogado = Condutor.recuperarLogado(AutorizacaoGIAntigo.titular(), AutorizacaoGIAntigo.titular().getOrgaoUsuario());
			if (condutorLogado != null) {
				criterioBusca = criterioBusca + " and condutor = ?";
				Object[] parametrosFiltrado = new Object[parametros.length + 1];
				for (int i = 0; i < parametros.length; i++) {
					parametrosFiltrado[i] = parametros[i];
				}
				parametrosFiltrado[parametros.length] = condutorLogado;
				parametros = parametrosFiltrado;
			}

		} else if (AutorizacaoGIAntigo.ehAdministradorMissaoPorComplexo()) {
			criterioBusca = criterioBusca + " and cpComplexo = ?";
			Object[] parametrosFiltrado = new Object[parametros.length + 1];
			for (int i = 0; i < parametros.length; i++) {
				parametrosFiltrado[i] = parametros[i];
			}
			if (AutorizacaoGIAntigo.ehAdministradorMissaoPorComplexo()) {
				parametrosFiltrado[parametros.length] = AutorizacaoGIAntigo.getComplexoAdministrado();
			}

			parametros = parametrosFiltrado;
		}

		List<Missao> missoes = Missao.AR.find(criterioBusca + " order by dataHoraSaida desc", parametros).fetch();

		return missoes;
	}

	@RoleAgente
	public static void listarPorCondutorLogado() {
		Condutor condutorLogado = Condutor.recuperarLogado(AutorizacaoGIAntigo.titular(), AutorizacaoGIAntigo.titular().getOrgaoUsuario());

		List<Missao> missoes = Missao.buscarTodasAsMissoesPorCondutor(condutorLogado);

		MenuMontador.instance().recuperarMenuMissoes(null);

		EstadoMissao estadoMissao = EstadoMissao.PROGRAMADA;

		Condutor condutor = new Condutor();

		renderTemplate("@listar", missoes, estadoMissao, condutor);
	}

	@RoleAgente
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void listarPorCondutor(Condutor condutorEscalado) throws Exception {

		condutorEscalado = Condutor.AR.findById(condutorEscalado.getId());

		List<Missao> missoes = Missao.buscarTodasAsMissoesPorCondutor(condutorEscalado);

		MenuMontador.instance().recuperarMenuMissoesPorCondutor();

		EstadoMissao estadoMissao = EstadoMissao.PROGRAMADA;

		validarListarParaCondutorEscalado(condutorEscalado);

		renderTemplate("@listar", missoes, condutorEscalado, estadoMissao);
	}

	protected static void validarListarParaCondutorEscalado(Condutor condutorEscalado) throws Exception {
		/* Criada uma miss�o Fake somente passar o condutor */
		Missao missao = new Missao();
		missao.setId(-1L);
		missao.condutor = condutorEscalado;
		checarCondutorPeloUsuarioAutenticado(missao);
	}

	@RoleAgente
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void listarFiltrado(EstadoMissao estado) throws Exception {
		if (estado == null) {
			estado = EstadoMissao.PROGRAMADA;
		}

		EstadoMissao estadoMissao = estado;
		Object[] parametros = { AutorizacaoGIAntigo.titular().getOrgaoUsuario(), estadoMissao };
		List<Missao> missoes = recuperarMissoes("cpOrgaoUsuario = ? and estadoMissao = ?", parametros);

		MenuMontador.instance().recuperarMenuMissoes(estado);

		Condutor condutor = new Condutor();

		renderTemplate("@listar", missoes, estadoMissao, condutor);
	}

	@RoleAgente
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void salvar(@Valid Missao missao, List<RequisicaoTransporte> requisicoesTransporte_alt, List<RequisicaoTransporte> requisicoesTransporte_ant) throws Exception {
		DpPessoa dpPessoa = AutorizacaoGIAntigo.cadastrante();
		String template;

		missao.cpOrgaoUsuario = AutorizacaoGIAntigo.titular().getOrgaoUsuario();

		if (missao.getId() > 0) {
			template = "@editar";
		} else {
			missao.setSequence(missao.cpOrgaoUsuario);
			template = "@incluir";
		}

		if (requisicoesTransporte_alt == null || requisicoesTransporte_alt.size() == 0) {
			missao.requisicoesTransporte = requisicoesTransporte_alt;
			Validation.addError("requisicoesTransporte", "missao.requisicoesTransporte.validation");
		}

		checarCategoriaCNHVeiculoCondutor(missao);

		redirecionarSeErroAoSalvar(missao, template);

		missao.responsavel = dpPessoa;

		validarRequisicoesDeServico(missao, template);

		boolean novaMissao = false;
		if (missao.getId() == 0) {
			novaMissao = true;
			missao.dataHora = Calendar.getInstance();
		}

		checarCondutorPeloUsuarioAutenticado(missao);

		missao = recuperarComplexoPeloPerfil(missao);

		missao.save();

		if (novaMissao) {
			gravarAndamentos(dpPessoa, "PROGRAMADO", missao.requisicoesTransporte, missao, EstadoRequisicao.PROGRAMADA);
		} else {
			deletarAndamentos(requisicoesTransporte_ant, missao);
			atualizarAndamentos(missao);
		}
		listarFiltrado(missao.estadoMissao);
	}

	/*
	 * private static void checarAcesso(Missao missao) throws Exception { if (! AutorizacaoGI.ehAdministrador() && ! AutorizacaoGI.ehAdministradorMissao()) { if (missao.getId() == 0) { throw new
	 * Exception("Voce nao tem acesso para incluir Missao"); }
	 * 
	 * if (! AutorizacaoGI.titular().equivale(missao.condutor.dpPessoa)) { try { throw new Exception("Voce nao tem acesso a esta miss�o"); } catch (Exception e) { AutorizacaoGI.tratarExcecoes(e); } }
	 * }
	 * 
	 * 
	 * }
	 */

	private static boolean validarRequisicoesDeServico(Missao missao, String template) throws Exception {
		Boolean temRequisicaoDeServico = false;
		Veiculo veiculoInicial = null;

		for (Iterator<RequisicaoTransporte> iterator = missao.requisicoesTransporte.iterator(); iterator.hasNext();) {
			RequisicaoTransporte req = iterator.next();
			req = RequisicaoTransporte.AR.findById(req.id);

			if (req.servicoVeiculo != null) {
				temRequisicaoDeServico = true;
				if (veiculoInicial == null) {
					veiculoInicial = req.servicoVeiculo.veiculo;
				} else {
					if (!veiculoInicial.equals(req.servicoVeiculo.veiculo)) {
						Validation.addError("veiculo", "missoes.veiculo.validation");
						redirecionarSeErroAoSalvar(missao, template);
					}
				}
			}
		}
		return temRequisicaoDeServico;
	}

	private static void gravarAndamentos(DpPessoa dpPessoa, String descricao, RequisicaoTransporte[] requisicoesTransporte, Missao missao, EstadoRequisicao[] estadosRequisicao) {

		for (int i = 0; i < requisicoesTransporte.length; i++) {
			RequisicaoTransporte requisicaoTransporte = requisicoesTransporte[i];

			Andamento andamento = new Andamento();
			andamento.descricao = descricao;
			andamento.dataAndamento = Calendar.getInstance();
			andamento.estadoRequisicao = estadosRequisicao[i];
			andamento.requisicaoTransporte = requisicaoTransporte;
			andamento.responsavel = dpPessoa;
			andamento.missao = missao;
			andamento.save();
		}
	}

	private static void gravarAndamentos(DpPessoa dpPessoa, String descricao, List<RequisicaoTransporte> requisicoesTransporte, Missao missao, EstadoRequisicao estadoRequisicao) {

		@SuppressWarnings("unused")
		int i = 0;
		for (RequisicaoTransporte requisicaoTransporte : requisicoesTransporte) {
			gravaAndamento(dpPessoa, descricao, missao, estadoRequisicao, requisicaoTransporte);
			i++;
		}
	}

	private static void deletarAndamentos(List<RequisicaoTransporte> requisicoesTransporte, Missao missao) throws Exception {

		for (RequisicaoTransporte requisicaoTransporte : requisicoesTransporte) {
			List<Andamento> andamentos = Andamento.find("requisicaoTransporte.id = ? order by id desc", requisicaoTransporte.id).fetch();
			for (Andamento andamento : andamentos) {
				if (andamento.missao != null && andamento.missao.getId().equals(missao.getId()) && andamento.estadoRequisicao.equals(EstadoRequisicao.PROGRAMADA)) {
					andamento.delete();
				}
			}
		}
	}

	private static void atualizarAndamentos(Missao missao) throws Exception {

		for (RequisicaoTransporte requisicaoTransporte : missao.requisicoesTransporte) {
			List<Andamento> andamentos = Andamento.find("requisicaoTransporte.id = ? order by id desc", requisicaoTransporte.id).fetch();
			for (Andamento andamento : andamentos) {
				Boolean novoAndamento = true;
				if (andamento.missao != null && andamento.missao.getId().equals(missao.getId())) {
					novoAndamento = false;
					break;
				}
				if (novoAndamento) {
					gravaAndamento(AutorizacaoGIAntigo.cadastrante(), "PROGRAMADA", missao, EstadoRequisicao.PROGRAMADA, requisicaoTransporte);
					break;
				}
			}
		}
	}

	private static void gravaAndamento(DpPessoa dpPessoa, String descricao, Missao missao, EstadoRequisicao estadoRequisicao, RequisicaoTransporte requisicaoTransporte) {
		Andamento andamento = new Andamento();
		andamento.descricao = descricao;
		andamento.dataAndamento = Calendar.getInstance();
		andamento.estadoRequisicao = estadoRequisicao;
		andamento.requisicaoTransporte = requisicaoTransporte;
		andamento.responsavel = dpPessoa;
		andamento.missao = missao;
		andamento.save();
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@RoleAgente
	public static void finalizar(Long id, String veiculosDisp) throws Exception {
		Missao missao = Missao.AR.findById(id);

		montarDadosParaAMissao(missao);

		// TODO ver a necessidade de ter o menu
		// MenuMontador.instance().RecuperarMenuMissao(id, missao.estadoMissao);

		checarCondutorPeloUsuarioAutenticado(missao);

		Integer i = 0;
		RequisicaoVsEstado[] requisicoesVsEstados = new RequisicaoVsEstado[missao.requisicoesTransporte.size()];
		for (RequisicaoTransporte requisicaoTransporte : missao.requisicoesTransporte) {
			RequisicaoVsEstado requisicaoVsEstados = new RequisicaoVsEstado();
			requisicaoVsEstados.idRequisicaoTransporte = requisicaoTransporte.id;
			requisicaoVsEstados.estado = requisicaoTransporte.ultimoEstado;
			requisicoesVsEstados[i] = requisicaoVsEstados;
			i = i + 1;
		}

		render(missao, requisicoesVsEstados);
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@RoleAgente
	public static void finalizarMissao(@Valid Missao missao, RequisicaoVsEstado[] requisicoesVsEstados) throws Exception {
		/*
		 * for (play.data.validation.Error error : Validation.errors()) { System.out.println(error.message() + " - " + error.getKey()); }
		 */

		verificarDatasInicialFinal(missao);
		verificarOdometroSaidaZerado(missao);
		verificarOdometroRetornoZerado(missao);
		verificarOdometrosSaidaRetorno(missao);
		DpPessoa dpPessoa = AutorizacaoGIAntigo.cadastrante();
		renderArgs.put("requisicoesVsEstados", requisicoesVsEstados);
		checarCategoriaCNHVeiculoCondutor(missao);
		redirecionarSeErroAoSalvar(missao, "@finalizar");
		missao.cpOrgaoUsuario = AutorizacaoGIAntigo.titular().getOrgaoUsuario();
		missao.responsavel = dpPessoa;
		missao.estadoMissao = EstadoMissao.FINALIZADA;
		checarCondutorPeloUsuarioAutenticado(missao);
		missao = recuperarComplexoPeloPerfil(missao);
		missao.save();

		RequisicaoTransporte[] requisicoes = missao.requisicoesTransporte.toArray(new RequisicaoTransporte[missao.requisicoesTransporte.size()]);
		EstadoRequisicao[] estadosRequisicao = new EstadoRequisicao[missao.requisicoesTransporte.size()];
		for (int i = 0; i < requisicoes.length; i++) {
			estadosRequisicao[i] = RequisicaoVsEstado.encontrarEstadoNaLista(requisicoesVsEstados, requisicoes[i].id);
		}

		gravarAndamentos(dpPessoa, "PELA MISSAO N." + missao.getSequence(), requisicoes, missao, estadosRequisicao);
		listarFiltrado(missao.estadoMissao);
	}

	private static void verificarOdometrosSaidaRetorno(Missao missao) {
		if (missao.odometroSaidaEmKm > missao.odometroRetornoEmKm) {
			Validation.addError("odometroRetornoEmKm", "missoes.odometroRetornoEmKm.validation");
		}
	}

	private static void verificarOdometroSaidaZerado(Missao missao) {
		if (missao.odometroSaidaEmKm == 0) {
			Validation.addError("odometroRetornoEmKm", "O valor do od&ocirc;metro de sa&iacute;da deve ser maior que zero.");
		}
	}

	private static void verificarOdometroRetornoZerado(Missao missao) {
		if (missao.odometroRetornoEmKm == 0) {
			Validation.addError("odometroRetornoEmKm", "O valor do od&ocirc;metro de retorno deve ser maior que zero.");
		}
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@RoleAgente
	public static void iniciarMissao(@Valid Missao missao, List<RequisicaoTransporte> requisicoesTransporte_alt, List<RequisicaoTransporte> requisicoesTransporte_ant, String veiculosDisp)
			throws Exception {
		verificarDisponibilidadeDeCondutor(missao);
		verificarOdometroSaidaZerado(missao);
		DpPessoa dpPessoa = AutorizacaoGIAntigo.cadastrante();

		if (requisicoesTransporte_alt == null || requisicoesTransporte_alt.size() == 0) {
			missao.requisicoesTransporte = requisicoesTransporte_alt;
			Validation.addError("requisicoesTransporte", "missao.requisicoesTransporte.validation");
		}

		checarCategoriaCNHVeiculoCondutor(missao);

		redirecionarSeErroAoSalvar(missao, "@iniciar");

		boolean temRequisicaoDeServico = validarRequisicoesDeServico(missao, "@iniciar");

		if (!temRequisicaoDeServico) {
			verificarDisponibilidadeDeVeiculo(missao);
		}

		missao.cpOrgaoUsuario = AutorizacaoGIAntigo.titular().getOrgaoUsuario();
		missao.responsavel = dpPessoa;
		missao.estadoMissao = EstadoMissao.INICIADA;

		checarCondutorPeloUsuarioAutenticado(missao);
		missao = recuperarComplexoPeloPerfil(missao);
		checarComplexo(missao.cpComplexo.getIdComplexo());

		missao.save();
		gravarAndamentos(dpPessoa, "PELA MISSAO N." + missao.getSequence(), missao.requisicoesTransporte, missao, EstadoRequisicao.EMATENDIMENTO);
		// listarFiltrado(missao.estadoMissao);
		buscarPelaSequence(missao.getSequence(), null);
	}

	protected static Missao recuperarComplexoPeloPerfil(Missao missao) throws Exception {
		if (AutorizacaoGIAntigo.ehAgente() || AutorizacaoGIAntigo.ehAdministradorMissaoPorComplexo()) {
			RequisicaoTransporte req1 = RequisicaoTransporte.AR.findById(missao.requisicoesTransporte.get(0).id);
			missao.cpComplexo = req1.cpComplexo;
		} else {
			missao.cpComplexo = AutorizacaoGIAntigo.recuperarComplexoPadrao();
		}

		return missao;
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void iniciarMissaoRapido(@Valid Missao missao, List<RequisicaoTransporte> requisicoesTransporte_alt, List<RequisicaoTransporte> requisicoesTransporte_ant) throws Exception {
		DpPessoa dpPessoa = AutorizacaoGIAntigo.cadastrante();
		String template = "@inicioRapido";

		missao.cpOrgaoUsuario = AutorizacaoGIAntigo.titular().getOrgaoUsuario();

		missao.setSequence(missao.cpOrgaoUsuario);

		missao.responsavel = dpPessoa;

		if (requisicoesTransporte_alt == null || requisicoesTransporte_alt.size() == 0) {
			missao.requisicoesTransporte = requisicoesTransporte_alt;
			Validation.addError("requisicoesTransporte", "missao.requisicoesTransporte.validation");
		}

		boolean temRequisicaoDeServico = validarRequisicoesDeServico(missao, template);

		// acho que nao vai ser chamado... //verificarDisponibilidadeDeCondutor(missao);
		if (!temRequisicaoDeServico) {
			verificarDisponibilidadeDeVeiculo(missao);
		}

		checarCategoriaCNHVeiculoCondutor(missao);

		redirecionarSeErroAoSalvar(missao, template);

		missao.dataHora = Calendar.getInstance();

		// marcar missao inicio rapido
		missao.inicioRapido = PerguntaSimNao.SIM;

		missao = recuperarComplexoPeloPerfil(missao);

		checarCondutorPeloUsuarioAutenticado(missao);
		checarComplexo(missao.cpComplexo.getIdComplexo());

		missao.save();

		gravarAndamentos(dpPessoa, "PROGRAMADO POR INICIO RAPIDO PARA MISSAO NO. " + missao.getSequence(), missao.requisicoesTransporte, missao, EstadoRequisicao.PROGRAMADA);

		validarRequisicoesDeServico(missao, template);

		// iniciar
		missao.estadoMissao = EstadoMissao.INICIADA;

		missao.save();
		gravarAndamentos(dpPessoa, "INICIO RAPIDO PELA MISSAO N." + missao.getSequence(), missao.requisicoesTransporte, missao, EstadoRequisicao.EMATENDIMENTO);

		buscarPelaSequence(missao.getSequence(), null);
	}

	@RoleAdmin
	@RoleAgente
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void iniciar(Long id) throws Exception {
		Missao missao = Missao.AR.findById(id);
		montarDadosParaAMissao(missao);

		// TODO ver a necessidade de ter o menu
		// MenuMontador.instance().RecuperarMenuMissao(id, missao.estadoMissao);
		checarCondutorPeloUsuarioAutenticado(missao);
		checarComplexo(missao.cpComplexo.getIdComplexo());
		render(missao);
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void cancelar(Long id) throws Exception {
		Missao missao = Missao.AR.findById(id);

		// TODO ver a necessidade de ter o menu
		// MenuMontador.instance().RecuperarMenuMissao(id, missao.estadoMissao);
		checarCondutorPeloUsuarioAutenticado(missao);
		checarComplexo(missao.cpComplexo.getIdComplexo());
		render(missao);
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void cancelarMissao(@Valid Missao missao) throws Exception {
		verificarJustificativaPreenchida(missao);

		DpPessoa dpPessoa = AutorizacaoGIAntigo.cadastrante();
		redirecionarSeErroAoCancelar(missao);
		missao.cpOrgaoUsuario = AutorizacaoGIAntigo.titular().getOrgaoUsuario();
		missao.responsavel = dpPessoa;
		missao.estadoMissao = EstadoMissao.CANCELADA;
		checarCondutorPeloUsuarioAutenticado(missao);
		checarComplexo(missao.cpComplexo.getIdComplexo());
		missao.cpComplexo = missao.requisicoesTransporte.get(0).cpComplexo;
		missao.save();

		gravarAndamentos(dpPessoa, "MISSAO CANCELADA", missao.requisicoesTransporte, missao, EstadoRequisicao.NAOATENDIDA);

		listarFiltrado(EstadoMissao.CANCELADA);
	}

	private static void verificarJustificativaPreenchida(Missao missao) {
		if (missao.justificativa.isEmpty()) {
			Validation.addError("justificativa", "missoes.justificativa.validation");
		}
	}

	private static void verificarDisponibilidadeDeVeiculo(Missao m) throws Exception {
		Boolean veiculoEstaDisponivel = Veiculo.estaDisponivel(m);
		if (!veiculoEstaDisponivel) {
			Validation.addError("veiculo", "missoes.veiculoEstaDisponivel.validation", m.veiculo.getDadosParaExibicao());
		}

	}

	private static void verificarDisponibilidadeDeCondutor(Missao m) throws Exception {
		Boolean condutorEstaDisponivel = Condutor.estaDisponivel(m);
		if (!condutorEstaDisponivel) {
			Validation.addError("condutor", "missoes.condutorEstaDisponivel.validation", m.condutor.getDadosParaExibicao());
		}
	}

	private static void verificarDatasInicialFinal(Missao m) {
		Boolean dataSaidaAntesDeDataRetorno = m.dataHoraSaida.before(m.dataHoraRetorno);
		if (!dataSaidaAntesDeDataRetorno) {
			Validation.addError("dataHoraRetorno", "missoes.dataSaidaAntesDeDataRetorno.validation");
		}
	}

	@SuppressWarnings("unchecked")
	private static void redirecionarSeErroAoSalvar(Missao missao, String template) throws Exception {
		if (Validation.hasErrors()) {
			for (play.data.validation.Error error : Validation.errors()) {
				System.out.println(error.message() + " - " + error.getKey());
			}
			montarCombos();
			if (missao.requisicoesTransporte != null) {
				List<RequisicaoTransporte> requisicoesTransporte = (List<RequisicaoTransporte>) renderArgs.get("requisicoesTransporte");
				for (int i = 0; i < missao.requisicoesTransporte.size(); i++) {
					if (missao.requisicoesTransporte.get(i) != null) {
						RequisicaoTransporte req = RequisicaoTransporte.AR.findById(missao.requisicoesTransporte.get(i).id);
						missao.requisicoesTransporte.set(i, req);
					}
				}

				requisicoesTransporte.removeAll(missao.requisicoesTransporte);
				renderArgs.put("requisicoesTransporte", requisicoesTransporte);

				renderArgs.put("estadoRequisicao", EstadoRequisicao.ATENDIDA);

			}

			if (missao.dataHoraSaida != null) {
				String dataHoraSaidaStr = JavaExtensions.format(missao.dataHoraSaida.getTime(), "dd/MM/yyyy HH:mm");
				if (!AutorizacaoGIAntigo.ehAdministrador() && !AutorizacaoGIAntigo.ehAdministradorMissao() && !AutorizacaoGIAntigo.ehAdministradorMissaoPorComplexo()) {
					/* Fixa combos quando o perfil do usu�rio logado � agente */
					List<Condutor> condutores = new ArrayList<Condutor>();
					condutores.add(missao.condutor);
					List<Veiculo> veiculos = new ArrayList<Veiculo>();
					veiculos.add(missao.veiculo);
					renderArgs.put("condutores", condutores);
					renderArgs.put("veiculos", veiculos);
				} else {
					renderArgs.put("condutores", listarCondutoresDisponiveis(missao.getId(), AutorizacaoGIAntigo.titular().getOrgaoUsuario().getId(), dataHoraSaidaStr, missao.inicioRapido));
					renderArgs.put("veiculos", listarVeiculosDisponiveis(missao.getId(), AutorizacaoGIAntigo.titular().getOrgaoUsuario().getId(), dataHoraSaidaStr));

				}
			}

			renderTemplate(template, missao);
		}
	}

	private static void redirecionarSeErroAoCancelar(Missao missao) throws Exception {
		if (Validation.hasErrors()) {
			String template = "@cancelar";

			renderTemplate(template, missao);
		}
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void incluir() {
		Missao missao = new Missao();
		missao.inicioRapido = PerguntaSimNao.NAO;
		MenuMontador.instance().recuperarMenuMissao(missao.getId(), missao.estadoMissao);

		render(missao);
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void incluirComRequisicoes(Long[] req) throws Exception {
		if (req == null) {
			incluir();
		}
		Missao missao = new Missao();
		missao.inicioRapido = PerguntaSimNao.NAO;
		missao.requisicoesTransporte = new ArrayList<RequisicaoTransporte>();
		for (int cont = 0; cont < req.length; cont++) {
			missao.requisicoesTransporte.add((RequisicaoTransporte) RequisicaoTransporte.AR.findById(req[cont]));
		}

		removerRequisicoesDoRenderArgs(missao.requisicoesTransporte);

		renderTemplate("@incluir", missao);
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void incluirInicioRapido(Long[] req) throws Exception {
		if (req == null) {
			incluir();
		}
		Missao missao = new Missao();
		missao.inicioRapido = PerguntaSimNao.SIM;
		missao.requisicoesTransporte = new ArrayList<RequisicaoTransporte>();
		for (int cont = 0; cont < req.length; cont++) {
			missao.requisicoesTransporte.add((RequisicaoTransporte) RequisicaoTransporte.AR.findById(req[cont]));
		}

		removerRequisicoesDoRenderArgs(missao.requisicoesTransporte);

		renderTemplate("@inicioRapido", missao);
	}

	private static void removerRequisicoesDoRenderArgs(List<RequisicaoTransporte> requisicoesTransporte) {
		@SuppressWarnings("unchecked")
		List<RequisicaoTransporte> requisicoes = (List<RequisicaoTransporte>) renderArgs.get("requisicoesTransporte");
		if (requisicoes != null) {
			requisicoes.removeAll(requisicoesTransporte);
		}
		renderArgs.put("requisicoesTransporte", requisicoes);
	}

	protected static void montarDadosParaAMissao(Missao missao) throws Exception {
		removerRequisicoesDoRenderArgs(missao.requisicoesTransporte);
		SimpleDateFormat formatar = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String dataHoraSaidaStr = formatar.format(missao.dataHoraSaida.getTime());
		List<Condutor> condutores = listarCondutoresDisponiveis(missao.getId(), AutorizacaoGIAntigo.titular().getOrgaoUsuario().getId(), dataHoraSaidaStr, missao.inicioRapido);
		boolean encontrouCondutor = false;
		if (condutores != null && !condutores.isEmpty()) {
			for (Condutor condutor : condutores) {
				if (condutor.getId().equals(missao.condutor.getId())) {
					encontrouCondutor = true;
					break;
				}
			}
		} else {
			condutores = new ArrayList<Condutor>();
		}
		if (!encontrouCondutor) {
			condutores.add(missao.condutor);
		}

		String veiculosDisp = "";
		for (RequisicaoTransporte req : missao.requisicoesTransporte) {
			if (req.servicoVeiculo != null) {
				req = RequisicaoTransporte.AR.findById(req.id);
				veiculosDisp += req.servicoVeiculo.veiculo.getId() + ", ";
			}
		}

		List<Veiculo> veiculos = new ArrayList<Veiculo>();

		if (veiculosDisp.equals("")) {
			veiculos = listarVeiculosDisponiveis(missao.getId(), AutorizacaoGIAntigo.titular().getOrgaoUsuario().getId(), dataHoraSaidaStr);
		} else {
			veiculos = listarVeiculosDisponiveis(veiculosDisp);
		}

		boolean encontrouVeiculo = false;
		if (veiculos != null && !veiculos.isEmpty()) {
			for (Veiculo veiculo : veiculos) {
				if (veiculo.getId().equals(missao.veiculo.getId())) {
					encontrouVeiculo = true;
					break;
				}
			}
		} else {
			veiculos = new ArrayList<Veiculo>();
		}
		if (!encontrouVeiculo) {
			veiculos.add(missao.veiculo);
		}

		renderArgs.put("condutores", condutores);
		renderArgs.put("veiculos", veiculos);
		renderArgs.put("missao", missao);

		EstadoRequisicao estadoRequisicao = EstadoRequisicao.ATENDIDA;
		EstadoMissao estadoMissao = EstadoMissao.PROGRAMADA;

		EstadoRequisicao[] estados = new EstadoRequisicao[missao.requisicoesTransporte.size()];
		for (int i = 0; i < estados.length; i++) {
			estados[i] = EstadoRequisicao.ATENDIDA;
		}

		renderArgs.put("estados", estados);
		renderArgs.put("estadoRequisicao", estadoRequisicao);
		renderArgs.put("estadoMissao", estadoMissao);
	}

	@RoleAdmin
	@RoleAgente
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void editar(Long id, String veiculosDisp) throws Exception {
		Missao missao = Missao.AR.findById(id);
		montarDadosParaAMissao(missao);
		checarCondutorPeloUsuarioAutenticado(missao);
		checarComplexo(missao.cpComplexo.getIdComplexo());
		MenuMontador.instance().recuperarMenuMissao(id, missao.estadoMissao);

		render();
	}

	protected static void checarCondutorPeloUsuarioAutenticado(Missao missao) throws Exception {
		if (AutorizacaoGIAntigo.ehAgente()) {
			if (missao.getId() == 0) {
				throw new Exception(Messages.get("missoes.autorizacaoGI.ehAgente.exception"));
			}

			if (!AutorizacaoGIAntigo.titular().equivale(missao.condutor.getDpPessoa())) {
				try {
					throw new Exception(Messages.get("missoes.autorizacaoGI.semAcesso.exception"));
				} catch (Exception e) {
					AutorizacaoGIAntigo.tratarExcecoes(e);
				}
			}
		}

	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@RoleAgente
	public static void buscarPelaSequence(String sequence, Boolean popUp) throws Exception {

		recuperarPelaSigla(sequence, popUp);
		renderTemplate("@ler");
	}

	protected static void recuperarPelaSigla(String sequence, Boolean popUp) throws Exception {
		Missao missao = Missao.buscar(sequence);
		montarDadosParaAMissao(missao);
		MenuMontador.instance().recuperarMenuMissao(missao.getId(), missao.estadoMissao);
		if (popUp != null) {
			renderArgs.put("mostrarMenu", !popUp);
		} else {
			renderArgs.put("mostrarMenu", true);
		}
		checarCondutorPeloUsuarioAutenticado(missao);
		checarComplexo(missao.cpComplexo.getIdComplexo());
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@RoleAgente
	public static void ler(Long id, String veiculosDisp) throws Exception {
		Missao missao = Missao.AR.findById(id);
		montarDadosParaAMissao(missao);
		MenuMontador.instance().recuperarMenuMissao(id, missao.estadoMissao);
		checarCondutorPeloUsuarioAutenticado(missao);
		checarComplexo(missao.cpComplexo.getIdComplexo());
		render();
	}

	@Before(priority = 200, only = { "incluir", "incluirComRequisicoes", "editar", "iniciar" })
	protected static void montarCombos() throws Exception {
		Calendar ultimos7dias = Calendar.getInstance();
		ultimos7dias.add(Calendar.DATE, -7);
		CpOrgaoUsuario orgaoParametro = AutorizacaoGIAntigo.titular().getOrgaoUsuario();
		Object[] parametros = { ultimos7dias, orgaoParametro };
		EstadoRequisicao[] estados = { EstadoRequisicao.AUTORIZADA, EstadoRequisicao.PROGRAMADA, EstadoRequisicao.EMATENDIMENTO, EstadoRequisicao.NAOATENDIDA, EstadoRequisicao.ATENDIDAPARCIALMENTE };
		Requisicoes.recuperarRequisicoes("(dataHoraRetornoPrevisto is null or dataHoraRetornoPrevisto >= ?) and cpOrgaoUsuario = ?", parametros, estados);
	}

	@Before(priority = 200, only = { "listar", "listarFiltrado", "listarPorCondutor", "listarPorCondutorLogado" })
	protected static void montarComboCondutor() throws Exception {
		List<Condutor> condutores = (AutorizacaoGIAntigo.ehAdministradorMissaoPorComplexo() ? Condutor.listarEscaladosDoComplexo(true, AutorizacaoGIAntigo.recuperarComplexoAdministrador(),
				AutorizacaoGIAntigo.titular().getOrgaoUsuario()) : Condutor.listarEscalados(true, AutorizacaoGIAntigo.titular().getOrgaoUsuario()));
		renderArgs.put("condutoresEscalados", condutores);
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void excluir(Long id) throws Exception {
		Missao missao = Missao.AR.findById(id);
		checarCondutorPeloUsuarioAutenticado(missao);
		checarComplexo(missao.cpComplexo.getIdComplexo());
		missao.delete();
		listar();
	}

	/* M�todo AJAX */
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@RoleAgente
	public static void listarVeiculosECondutoresDisponiveis(String nomePropriedade, String nomePropriedade1, Long idMissao, String dataSaida, String veiculosDisp, PerguntaSimNao inicioRapido)
			throws Exception {

		Missao missao = Missao.AR.findById(idMissao);
		String opcaoSelecionada = " selected = 'selected'";
		String selectDesabilitado = " disabled = 'disabled'";

		List<Veiculo> veiculosDisponiveis = null;

		if (veiculosDisp.equals("")) {
			veiculosDisponiveis = listarVeiculosDisponiveis(idMissao, AutorizacaoGIAntigo.titular().getOrgaoUsuario().getId(), dataSaida);
		} else {
			veiculosDisponiveis = listarVeiculosDisponiveis(veiculosDisp);
		}

		StringBuffer htmlSelectVeiculos = new StringBuffer();
		htmlSelectVeiculos.append("<select id='selveiculosdisponiveis' name='" + nomePropriedade.toString() + "' size='1' ");
		if (AutorizacaoGIAntigo.ehAgente()) {
			htmlSelectVeiculos.append(selectDesabilitado);
		}
		htmlSelectVeiculos.append(">");
		for (Veiculo veiculo : veiculosDisponiveis) {
			htmlSelectVeiculos.append("<option value='" + veiculo.getId() + "'");
			if (missao != null && veiculo.equals(missao.veiculo)) {
				htmlSelectVeiculos.append(opcaoSelecionada);
			}
			htmlSelectVeiculos.append(">" + veiculo.getDadosParaExibicao());

		}
		htmlSelectVeiculos.append("</option>" + "</select>");

		List<Condutor> condutoresDisponiveis = listarCondutoresDisponiveis(idMissao, AutorizacaoGIAntigo.titular().getOrgaoUsuario().getId(), dataSaida, inicioRapido);
		StringBuffer htmlSelectCondutores = new StringBuffer();
		htmlSelectCondutores.append("<select id='selcondutoresdisponiveis' name='" + nomePropriedade1.toString() + "' size='1' ");
		if (AutorizacaoGIAntigo.ehAgente()) {
			htmlSelectCondutores.append(selectDesabilitado);
		}
		htmlSelectCondutores.append(">");
		for (Condutor condutor : condutoresDisponiveis) {
			htmlSelectCondutores.append("<option value='" + condutor.getId() + "'");
			if (missao != null && condutor.equals(missao.condutor)) {
				htmlSelectCondutores.append(opcaoSelecionada);
			}
			htmlSelectCondutores.append(">" + condutor.getDadosParaExibicao());

		}
		htmlSelectCondutores.append("</option>" + "</select>");

		String html = htmlSelectVeiculos.toString() + "@" + htmlSelectCondutores.toString();

		renderText(html);
	}

	private static List<Veiculo> listarVeiculosDisponiveis(String veiculosDisp) throws NumberFormatException, Exception {
		List<Veiculo> veiculos = new ArrayList<Veiculo>();
		veiculosDisp = veiculosDisp.substring(0, veiculosDisp.length() - 2);
		List<String> itens = Arrays.asList(veiculosDisp.split("\\s*,\\s*"));

		if (itens.size() > 0) {
			for (String item : itens) {
				Veiculo itemVeiculo = new Veiculo();
				itemVeiculo = Veiculo.AR.findById(Long.parseLong(item));

				if (!veiculos.contains(itemVeiculo)) {
					veiculos.add(itemVeiculo);
				}
			}
		}

		return veiculos;
	}

	private static List<Veiculo> listarVeiculosDisponiveis(Long idMissao, Long idOrgao, String dataSaida) {
		List<Veiculo> veiculos = Veiculo.listarDisponiveis(dataSaida, idMissao, idOrgao);
		return veiculos;
	}

	private static List<Condutor> listarCondutoresDisponiveis(Long idMissao, Long idOrgao, String dataSaida, PerguntaSimNao inicioRapido) throws Exception {
		List<Condutor> condutores = Condutor.listarDisponiveis(dataSaida, idMissao, idOrgao, inicioRapido);
		return condutores;
	}

	@SuppressWarnings("unchecked")
	protected static List<Missao> buscarPorCondutoreseEscala(EscalaDeTrabalho escala) {
		SimpleDateFormat formatar = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String dataFormatadaFimOracle;	
		if (escala.getDataVigenciaFim() == null) {
			@SuppressWarnings("unused")
			Calendar dataFim = Calendar.getInstance();
			dataFormatadaFimOracle = "to_date('31/12/9999 23:59', 'DD/MM/YYYY HH24:mi')";
		} else {
			String dataFim = formatar.format(escala.getDataVigenciaFim().getTime());
			dataFormatadaFimOracle = "to_date('" + dataFim + "', 'DD/MM/YYYY HH24:mi')";		
		}
		String dataInicio = formatar.format(escala.getDataVigenciaInicio().getTime());
		String dataFormatadaInicioOracle = "to_date('" + dataInicio + "', 'DD/MM/YYYY HH24:mi')";
		List<Missao> missoes = null; 
		String qrl = 	"SELECT p FROM Missao p" +
		" WHERE  p.condutor.id = " + escala.getCondutor().getId() +
		" AND    p.estadoMissao NOT IN ('" + EstadoMissao.CANCELADA + "','" + EstadoMissao.FINALIZADA + "')" +
		" AND   (p.dataHoraSaida >= " + dataFormatadaInicioOracle +
		" AND    p.dataHoraSaida <= " + dataFormatadaFimOracle + "))"; 

		Query qry = JPA.em().createQuery(qrl);
		try {
			missoes = (List<Missao>) qry.getResultList();
		} catch (NoResultException ex) {
			missoes = null;
		}

		return missoes;
	}

	private static void checarComplexo(Long idComplexo) throws Exception {
		if (AutorizacaoGIAntigo.ehAdministradorMissaoPorComplexo()) {
			if (!AutorizacaoGIAntigo.getComplexoAdministrado().getIdComplexo().equals(idComplexo)) {
				try {
					throw new Exception(Messages.get("missoes.autorizacaoGI.semAcesso.exception"));
				} catch (Exception e) {
					AutorizacaoGIAntigo.tratarExcecoes(e);
				}
			} else if (AutorizacaoGIAntigo.ehAprovador()) {
				if (!AutorizacaoGIAntigo.recuperarComplexoPadrao().getIdComplexo().equals(idComplexo)) {
					try {
						throw new Exception(Messages.get("missoes.autorizacaoGI.semAcesso.exception"));
					} catch (Exception e) {
						AutorizacaoGIAntigo.tratarExcecoes(e);
					}
				}
			}
		}
	}

	protected static void checarCategoriaCNHVeiculoCondutor(Missao missao) throws Exception {
		CategoriaCNH categoriaCondutor1 = null;
		CategoriaCNH categoriaCondutor2 = null;

		if (missao.condutor.getCategoriaCNH().getDescricao().length() == 2) {
			switch (missao.condutor.getCategoriaCNH().getDescricao()) {
			case "AB":
				categoriaCondutor1 = CategoriaCNH.A;
				categoriaCondutor2 = CategoriaCNH.B;
				break;
			case "AC":
				categoriaCondutor1 = CategoriaCNH.A;
				categoriaCondutor2 = CategoriaCNH.C;
				break;
			case "AD":
				categoriaCondutor1 = CategoriaCNH.A;
				categoriaCondutor2 = CategoriaCNH.D;
				break;
			case "AE":
				categoriaCondutor1 = CategoriaCNH.A;
				categoriaCondutor2 = CategoriaCNH.E;
				break;
			// default:
			// return false;
			}
			if (categoriaCondutor1 != null && categoriaCondutor2 != null) {
				if (categoriaCondutor1.compareTo(missao.veiculo.getCategoriaCNH()) >= 0 || categoriaCondutor2.compareTo(missao.veiculo.getCategoriaCNH()) >= 0) {
					return;
				}
			}
		} else {
			if (missao.condutor.getCategoriaCNH().compareTo(missao.veiculo.getCategoriaCNH()) >= 0) {
				return;
			}
		}

		Validation.addError("categoriaCnhCondutor", Messages.get("missao.categoriaCNHCondutorErradaParaVeiculo.validation"));
	}
}
