package br.gov.jfrj.siga.tp.vraptor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.validator.I18nMessage;
import br.com.caelum.vraptor.view.Results;
import br.gov.jfrj.siga.base.AplicacaoException;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.tp.auth.AutorizacaoGI;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdmin;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissao;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissaoComplexo;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAgente;
import br.gov.jfrj.siga.tp.enums.Template;
import br.gov.jfrj.siga.tp.model.Andamento;
import br.gov.jfrj.siga.tp.model.CategoriaCNH;
import br.gov.jfrj.siga.tp.model.Condutor;
import br.gov.jfrj.siga.tp.model.EscalaDeTrabalho;
import br.gov.jfrj.siga.tp.model.EstadoMissao;
import br.gov.jfrj.siga.tp.model.EstadoRequisicao;
import br.gov.jfrj.siga.tp.model.Missao;
import br.gov.jfrj.siga.tp.model.RequisicaoTransporte;
import br.gov.jfrj.siga.tp.model.RequisicaoVsEstado;
import br.gov.jfrj.siga.tp.model.TpDao;
import br.gov.jfrj.siga.tp.model.Veiculo;
import br.gov.jfrj.siga.tp.util.PerguntaSimNao;
import br.gov.jfrj.siga.vraptor.SigaObjects;

@Resource
@Path("/app/missao")
public class MissaoController extends TpController {

	private static final String MISSOES_AUTORIZACAO_GI_SEM_ACESSO_EXCEPTION = "missoes.autorizacaoGI.semAcesso.exception";
	private static final String MISSAO_REQUISICOES_TRANSPORTE_VALIDATION = "missao.requisicoesTransporte.validation";
	private static final String ODOMETRO_RETORNO_EM_KM_STR = "odometroRetornoEmKm";
	private static final String VEICULOS_STR = "veiculos";
	private static final String MOSTRAR_DADOS_INICIADA_STR = "mostrarDadosIniciada";
	private static final String MISSAO_STR = "missao";
	private static final String MISSOES_STR = "missoes";
	private static final String ESTADO_MISSAO_STR = "estadoMissao";
	private static final String CONDUTORES_STR = "condutores";
	private static final String CONDUTOR_STR = "condutor";
	private static final String REQUISICOES_TRANSPORTE_STR = "requisicoesTransporte";
	private static final String PATTERN_DDMMYYYYHHMM = "dd/MM/yyyy HH:mm";

	private AutorizacaoGI autorizacaoGI;

	public MissaoController(HttpServletRequest request, Result result, Validator validator, SigaObjects so, EntityManager em, AutorizacaoGI autorizacaoGI){
		super(request, result, TpDao.getInstance(), validator, so, em);
		this.autorizacaoGI = autorizacaoGI;
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAgente
	@RoleAdminMissaoComplexo
	@Path("/listar")
	public void listar() throws Exception {
		Object[] parametros = { getTitular().getOrgaoUsuario(), EstadoMissao.PROGRAMADA, EstadoMissao.INICIADA, EstadoMissao.FINALIZADA, EstadoMissao.CANCELADA };
		StringBuilder criterio = new StringBuilder();
		criterio.append("cpOrgaoUsuario = ? and estadoMissao in (?,?,?,?)");
		List<Missao> missoes = recuperarMissoes(criterio, parametros);
		EstadoMissao estadoMissao = EstadoMissao.PROGRAMADA;
		MenuMontador.instance(result).recuperarMenuMissoes(null);
		Condutor condutor = new Condutor();

		result.include(MISSOES_STR, missoes);
		result.include(ESTADO_MISSAO_STR, estadoMissao);
		result.include(CONDUTOR_STR, condutor);
	}

	private List<Missao> recuperarMissoes(StringBuilder criterioBusca, Object[] parametros) throws Exception {
		Object[] parametrosFiltrado = new Object[parametros.length + 1];

		if (!autorizacaoGI.ehAdministrador() && !autorizacaoGI.ehAdministradorMissao() && !autorizacaoGI.ehAdministradorMissaoPorComplexo()) {
			Condutor condutorLogado = Condutor.recuperarLogado(getTitular(), getTitular().getOrgaoUsuario());
			if (condutorLogado != null) {
				criterioBusca.append(" and condutor = ?");
				for (int i = 0; i < parametros.length; i++) {
					parametrosFiltrado[i] = parametros[i];
				}
				parametrosFiltrado[parametros.length] = condutorLogado;
			}

		} else if (autorizacaoGI.ehAdministradorMissaoPorComplexo()) {
			criterioBusca.append(" and cpComplexo = ?");
			for (int i = 0; i < parametros.length; i++) {
				parametrosFiltrado[i] = parametros[i];
			}
			if (autorizacaoGI.ehAdministradorMissaoPorComplexo()) {
				parametrosFiltrado[parametros.length] = getComplexoAdministrado();
			}
		}

		return Missao.AR.find(criterioBusca.toString() + " order by dataHoraSaida desc", parametrosFiltrado).fetch();
	}

	@RoleAgente
	@Path("/listarPorCondutorLogado")
	public void listarPorCondutorLogado() throws Exception {
		Condutor condutorLogado = Condutor.recuperarLogado(getTitular(), getTitular().getOrgaoUsuario());
		List<Missao> missoes = Missao.buscarTodasAsMissoesPorCondutor(condutorLogado);
		MenuMontador.instance(result).recuperarMenuMissoes(null);
		EstadoMissao estadoMissao = EstadoMissao.PROGRAMADA;
		Condutor condutor = new Condutor();

		result.include(MISSOES_STR, missoes);
		result.include(ESTADO_MISSAO_STR, estadoMissao);
		result.include(CONDUTOR_STR, condutor);

		result.redirectTo(this).listar();
	}

	@RoleAgente
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/listarPorCondutor")
	public void listarPorCondutor(Condutor condutorEscalado) throws Exception {
		Condutor condutorEncontrado = Condutor.AR.findById(condutorEscalado.getId());
		List<Missao> missoes = Missao.buscarTodasAsMissoesPorCondutor(condutorEncontrado);
		MenuMontador.instance(result).recuperarMenuMissoesPorCondutor();
		EstadoMissao estadoMissao = EstadoMissao.PROGRAMADA;
		validarListarParaCondutorEscalado(condutorEncontrado);

		result.include(MISSOES_STR, missoes);
		result.include("condutorEscalado", condutorEncontrado);
		result.include(ESTADO_MISSAO_STR, estadoMissao);

		result.redirectTo(this).listar();
	}

	protected void validarListarParaCondutorEscalado(Condutor condutorEscalado) throws Exception {
		Missao missao = new Missao();
		missao.setId(-1L);
		missao.setCondutor(condutorEscalado);
		checarCondutorPeloUsuarioAutenticado(missao);
	}

	@RoleAgente
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/listarFiltrado")
	public void listarFiltrado(EstadoMissao estado) throws Exception {
		EstadoMissao estadoMissao = seEstadoNuloUsarDefault(estado);

		Object[] parametros = { getTitular().getOrgaoUsuario(), estadoMissao };
		StringBuilder criterio = new StringBuilder();
		criterio.append("cpOrgaoUsuario = ? and estadoMissao = ?");
		List<Missao> missoes = recuperarMissoes(criterio, parametros);
		MenuMontador.instance(result).recuperarMenuMissoes(estado);
		Condutor condutor = new Condutor();

		result.include(MISSOES_STR, missoes);
		result.include(ESTADO_MISSAO_STR, estadoMissao);
		result.include(CONDUTOR_STR, condutor);

		result.redirectTo(this).listar();
	}

	private EstadoMissao seEstadoNuloUsarDefault(EstadoMissao estado) {
		return null == estado ? EstadoMissao.PROGRAMADA : estado;
	}

	@RoleAgente
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/salvar")
	public void salvar(@Valid Missao missao, List<RequisicaoTransporte> requisicoesTransporteAlt, List<RequisicaoTransporte> requisicoesTransportAnt) throws Exception {
		DpPessoa dpPessoa = getCadastrante();
		Template template;

		missao.setCpOrgaoUsuario(getTitular().getOrgaoUsuario());

		if (missao.getId() > 0)
			template = Template.EDITAR;
		else {
			missao.setSequence(missao.getCpOrgaoUsuario());
			template = Template.INCLUIR;
		}

		if (requisicoesTransporteAlt == null || requisicoesTransporteAlt.isEmpty()) {
			missao.setRequisicoesTransporte(requisicoesTransporteAlt);
			validator.add(new I18nMessage(REQUISICOES_TRANSPORTE_STR, MISSAO_REQUISICOES_TRANSPORTE_VALIDATION));
		}

		checarCategoriaCNHVeiculoCondutor(missao);
		redirecionarSeErroAoSalvar(missao, template);
		missao.setResponsavel(dpPessoa);

		validarRequisicoesDeServico(missao, template);

		boolean novaMissao = false;
		if (missao.getId() == 0) {
			novaMissao = true;
			missao.setDataHora(Calendar.getInstance());
		}

		checarCondutorPeloUsuarioAutenticado(missao);
		Missao missaoPronta = recuperarComplexoPeloPerfil(missao);
		missaoPronta.save();

		if (novaMissao)
			gravarAndamentos(dpPessoa, "PROGRAMADO", missaoPronta.getRequisicoesTransporte(), missaoPronta, EstadoRequisicao.PROGRAMADA);
		else {
			deletarAndamentos(requisicoesTransportAnt, missaoPronta);
			atualizarAndamentos(missaoPronta);
		}

		listarFiltrado(missaoPronta.getEstadoMissao());
	}

	private boolean validarRequisicoesDeServico(Missao missao, Template template) throws Exception {
		Boolean temRequisicaoDeServico = false;
		Veiculo veiculoInicial = null;

		for (Iterator<RequisicaoTransporte> iterator = missao.getRequisicoesTransporte().iterator(); iterator.hasNext();) {
			RequisicaoTransporte req = iterator.next();
			req = RequisicaoTransporte.AR.findById(req.getId());

			if (req.getServicoVeiculo() != null) {
				temRequisicaoDeServico = true;
				if (veiculoInicial == null)
					veiculoInicial = req.getServicoVeiculo().getVeiculo();
				else if (!veiculoInicial.equals(req.getServicoVeiculo().getVeiculo())) {
					validator.add(new I18nMessage("veiculo", "missoes.veiculo.validation"));
					redirecionarSeErroAoSalvar(missao, template);
				}
			}
		}

		return temRequisicaoDeServico;
	}

	private void gravarAndamentos(DpPessoa dpPessoa, String descricao, RequisicaoTransporte[] requisicoesTransporte, Missao missao, EstadoRequisicao[] estadosRequisicao) {

		for (int i = 0; i < requisicoesTransporte.length; i++) {
			RequisicaoTransporte requisicaoTransporte = requisicoesTransporte[i];

			Andamento andamento = new Andamento();
			andamento.setDescricao(descricao);
			andamento.setDataAndamento(Calendar.getInstance());
			andamento.setEstadoRequisicao(estadosRequisicao[i]);
			andamento.setRequisicaoTransporte(requisicaoTransporte);
			andamento.setResponsavel(dpPessoa);
			andamento.setMissao(missao);
			andamento.save();
		}
	}

	private void gravarAndamentos(DpPessoa dpPessoa, String descricao, List<RequisicaoTransporte> requisicoesTransporte, Missao missao, EstadoRequisicao estadoRequisicao) {
		for (RequisicaoTransporte requisicaoTransporte : requisicoesTransporte)
			gravaAndamento(dpPessoa, descricao, missao, estadoRequisicao, requisicaoTransporte);
	}

	private void deletarAndamentos(List<RequisicaoTransporte> requisicoesTransporte, Missao missao) throws Exception {

		for (RequisicaoTransporte requisicaoTransporte : requisicoesTransporte) {
			List<Andamento> andamentos = Andamento.AR.find("requisicaoTransporte.id = ? order by id desc", requisicaoTransporte.getId()).fetch();
			for (Andamento andamento : andamentos) {
				if (missaoEmAndamento(missao, andamento) && andamento.getEstadoRequisicao().equals(EstadoRequisicao.PROGRAMADA)) {
					andamento.delete();
				}
			}
		}
	}

	private void atualizarAndamentos(Missao missao) throws Exception {
		for (RequisicaoTransporte requisicaoTransporte : missao.getRequisicoesTransporte()) {
			List<Andamento> andamentos = Andamento.AR.find("requisicaoTransporte.id = ? order by id desc", requisicaoTransporte.getId()).fetch();

			for (Andamento andamento : andamentos)
				if (!missaoEmAndamento(missao, andamento)) {
					gravaAndamento(getCadastrante(), "PROGRAMADA", missao, EstadoRequisicao.PROGRAMADA, requisicaoTransporte);
					break;
				}
		}
	}

	private boolean missaoEmAndamento(Missao missao, Andamento andamento) {
		return andamento.getMissao() != null && andamento.getMissao().getId().equals(missao.getId());
	}

	private void gravaAndamento(DpPessoa dpPessoa, String descricao, Missao missao, EstadoRequisicao estadoRequisicao, RequisicaoTransporte requisicaoTransporte) {
		Andamento andamento = new Andamento();
		andamento.setDescricao(descricao);
		andamento.setDataAndamento(Calendar.getInstance());
		andamento.setEstadoRequisicao(estadoRequisicao);
		andamento.setRequisicaoTransporte(requisicaoTransporte);
		andamento.setResponsavel(dpPessoa);
		andamento.setMissao(missao);
		andamento.save();
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@RoleAgente
	@Path("/finalizar/{id}")
	public void finalizar(Long id) throws Exception {
		Missao missao = Missao.AR.findById(id);
		montarDadosParaAMissao(missao);
		checarCondutorPeloUsuarioAutenticado(missao);
		Integer i = 0;
		RequisicaoVsEstado[] requisicoesVsEstados = new RequisicaoVsEstado[missao.getRequisicoesTransporte().size()];
		for (RequisicaoTransporte requisicaoTransporte : missao.getRequisicoesTransporte()) {
			RequisicaoVsEstado requisicaoVsEstados = new RequisicaoVsEstado();
			requisicaoVsEstados.idRequisicaoTransporte = requisicaoTransporte.getId();
			requisicaoVsEstados.estado = requisicaoTransporte.getUltimoEstado();
			requisicoesVsEstados[i] = requisicaoVsEstados;
			i = i + 1;
		}

		result.include(MISSAO_STR, missao);
		result.include("requisicoesVsEstados", requisicoesVsEstados);
		result.include("mostrarBotoesFinalizar", true);
		result.include("mostrarDadosProgramada", true);
		result.include(MOSTRAR_DADOS_INICIADA_STR, true);
		result.include("mostrarDadosFinalizada", true);
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@RoleAgente
	@Path("/finalizarMissao")
	public void finalizarMissao(@Valid Missao missao, RequisicaoVsEstado[] requisicoesVsEstados) throws Exception {
		verificarDatasInicialFinal(missao);
		verificarOdometroSaidaZerado(missao);
		verificarOdometroRetornoZerado(missao);
		verificarOdometrosSaidaRetorno(missao);
		DpPessoa dpPessoa = getCadastrante();

		result.include("requisicoesVsEstados", requisicoesVsEstados);

		checarCategoriaCNHVeiculoCondutor(missao);
		redirecionarSeErroAoSalvar(missao, Template.FINALIZAR);
		missao.setCpOrgaoUsuario(getTitular().getOrgaoUsuario());
		missao.setResponsavel(dpPessoa);
		missao.setEstadoMissao(EstadoMissao.FINALIZADA);
		checarCondutorPeloUsuarioAutenticado(missao);

		Missao missaoPronta = recuperarComplexoPeloPerfil(missao);
		missaoPronta.save();

		RequisicaoTransporte[] requisicoes = missaoPronta.getRequisicoesTransporte().toArray(new RequisicaoTransporte[missaoPronta.getRequisicoesTransporte().size()]);
		EstadoRequisicao[] estadosRequisicao = new EstadoRequisicao[missaoPronta.getRequisicoesTransporte().size()];
		for (int i = 0; i < requisicoes.length; i++) {
			estadosRequisicao[i] = RequisicaoVsEstado.encontrarEstadoNaLista(requisicoesVsEstados, requisicoes[i].getId());
		}

		gravarAndamentos(dpPessoa, "PELA MISSAO N." + missaoPronta.getSequence(), requisicoes, missaoPronta, estadosRequisicao);
		listarFiltrado(missaoPronta.getEstadoMissao());
	}

	private void verificarOdometrosSaidaRetorno(Missao missao) {
		if (missao.getOdometroSaidaEmKm() > missao.getOdometroRetornoEmKm()) {
			validator.add(new I18nMessage(ODOMETRO_RETORNO_EM_KM_STR, "missoes.odometroRetornoEmKm.validation"));
		}
	}

	private void verificarOdometroSaidaZerado(Missao missao) {
		if (missao.getOdometroSaidaEmKm() == 0) {
			validator.add(new I18nMessage(ODOMETRO_RETORNO_EM_KM_STR, "O valor do od&ocirc;metro de sa&iacute;da deve ser maior que zero."));
		}
	}

	private void verificarOdometroRetornoZerado(Missao missao) {
		if (missao.getOdometroRetornoEmKm() == 0) {
			validator.add(new I18nMessage(ODOMETRO_RETORNO_EM_KM_STR, "O valor do od&ocirc;metro de retorno deve ser maior que zero."));
		}
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@RoleAgente
	@Path("/iniciarMissao")
	public void iniciarMissao(@Valid Missao missao, List<RequisicaoTransporte> requisicoesTransporteAlt) throws Exception {
		verificarDisponibilidadeDeCondutor(missao);
		verificarOdometroSaidaZerado(missao);
		DpPessoa dpPessoa = getCadastrante();

		if (null == requisicoesTransporteAlt || requisicoesTransporteAlt.isEmpty()) {
			missao.setRequisicoesTransporte(requisicoesTransporteAlt);
			validator.add(new I18nMessage(REQUISICOES_TRANSPORTE_STR, MISSAO_REQUISICOES_TRANSPORTE_VALIDATION));
		}

		checarCategoriaCNHVeiculoCondutor(missao);

		redirecionarSeErroAoSalvar(missao, Template.INICIAR);

		boolean temRequisicaoDeServico = validarRequisicoesDeServico(missao, Template.INICIAR);

		if (!temRequisicaoDeServico) {
			verificarDisponibilidadeDeVeiculo(missao);
		}

		missao.setCpOrgaoUsuario(getTitular().getOrgaoUsuario());
		missao.setResponsavel(dpPessoa);
		missao.setEstadoMissao(EstadoMissao.INICIADA);

		checarCondutorPeloUsuarioAutenticado(missao);

		Missao missaoPronta = recuperarComplexoPeloPerfil(missao);
		checarComplexo(missaoPronta.getCpComplexo().getIdComplexo());

		missaoPronta.save();
		gravarAndamentos(dpPessoa, "PELA MISSAO N." + missaoPronta.getSequence(), missaoPronta.getRequisicoesTransporte(), missaoPronta, EstadoRequisicao.EMATENDIMENTO);
		buscarPelaSequence(false, missaoPronta.getSequence());
	}

	protected Missao recuperarComplexoPeloPerfil(Missao missao) throws Exception {
		if (autorizacaoGI.ehAgente() || autorizacaoGI.ehAdministradorMissaoPorComplexo()) {
			RequisicaoTransporte req1 = RequisicaoTransporte.AR.findById(missao.getRequisicoesTransporte().get(0).getId());
			missao.setCpComplexo(req1.getCpComplexo());
		} else
			missao.setCpComplexo(recuperarComplexoPadrao());

		return missao;
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/iniciarMissaoRapido")
	public void iniciarMissaoRapido(@Valid Missao missao, List<RequisicaoTransporte> requisicoesTransporteAlt) throws Exception {
		DpPessoa dpPessoa = getCadastrante();
		Template template = Template.INICIORAPIDO;
		missao.setCpOrgaoUsuario(getTitular().getOrgaoUsuario());
		missao.setSequence(missao.getCpOrgaoUsuario());
		missao.setResponsavel(dpPessoa);

		if (requisicoesTransporteAlt == null || requisicoesTransporteAlt.isEmpty()) {
			missao.setRequisicoesTransporte(requisicoesTransporteAlt);
			validator.add(new I18nMessage(REQUISICOES_TRANSPORTE_STR, MISSAO_REQUISICOES_TRANSPORTE_VALIDATION));
		}

		boolean temRequisicaoDeServico = validarRequisicoesDeServico(missao, template);

		if (!temRequisicaoDeServico)
			verificarDisponibilidadeDeVeiculo(missao);

		checarCategoriaCNHVeiculoCondutor(missao);
		redirecionarSeErroAoSalvar(missao, template);
		missao.setDataHora(Calendar.getInstance());
		missao.setInicioRapido(PerguntaSimNao.SIM);

		Missao missaoPronta = recuperarComplexoPeloPerfil(missao);
		checarCondutorPeloUsuarioAutenticado(missaoPronta);
		checarComplexo(missaoPronta.getCpComplexo().getIdComplexo());

		missaoPronta.save();

		gravarAndamentos(dpPessoa, "PROGRAMADO POR INICIO RAPIDO PARA MISSAO NO. " + missaoPronta.getSequence(), missaoPronta.getRequisicoesTransporte(), missaoPronta, EstadoRequisicao.PROGRAMADA);
		validarRequisicoesDeServico(missaoPronta, template);
		missaoPronta.setEstadoMissao(EstadoMissao.INICIADA);

		missaoPronta.save();
		gravarAndamentos(dpPessoa, "INICIO RAPIDO PELA MISSAO N." + missaoPronta.getSequence(), missaoPronta.getRequisicoesTransporte(), missaoPronta, EstadoRequisicao.EMATENDIMENTO);

		result.redirectTo(this).buscarPelaSequence(false, missaoPronta.getSequence());
	}

	@RoleAdmin
	@RoleAgente
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/iniciar/{id}")
	public void iniciar(Long id) throws Exception {
		Missao missao = Missao.AR.findById(id);
		montarDadosParaAMissao(missao);
		checarCondutorPeloUsuarioAutenticado(missao);
		checarComplexo(missao.getCpComplexo().getIdComplexo());

		result.include(MISSAO_STR, missao);
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/cancelar/{id}")
	public void cancelar(Long id) throws Exception {
		Missao missao = Missao.AR.findById(id);
		checarCondutorPeloUsuarioAutenticado(missao);
		checarComplexo(missao.getCpComplexo().getIdComplexo());

		result.include(MISSAO_STR, missao);
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/cancelarMissao")
	public void cancelarMissao(@Valid Missao missao) throws Exception {
		verificarJustificativaPreenchida(missao);

		DpPessoa dpPessoa = getCadastrante();
		redirecionarSeErroAoCancelar(missao);
		missao.setCpOrgaoUsuario(getTitular().getOrgaoUsuario());
		missao.setResponsavel(dpPessoa);
		missao.setEstadoMissao(EstadoMissao.CANCELADA);
		checarCondutorPeloUsuarioAutenticado(missao);
		checarComplexo(missao.getCpComplexo().getIdComplexo());
		missao.setCpComplexo(missao.getRequisicoesTransporte().get(0).getCpComplexo());
		missao.save();

		gravarAndamentos(dpPessoa, "MISSAO CANCELADA", missao.getRequisicoesTransporte(), missao, EstadoRequisicao.NAOATENDIDA);

		listarFiltrado(EstadoMissao.CANCELADA);
	}

	private void verificarJustificativaPreenchida(Missao missao) {
		if (missao.getJustificativa().isEmpty()) {
			validator.add(new I18nMessage("justificativa", "missoes.justificativa.validation"));
		}
	}

	private void verificarDisponibilidadeDeVeiculo(Missao m) throws Exception {
		Boolean veiculoEstaDisponivel = Veiculo.estaDisponivel(m);
		if (!veiculoEstaDisponivel) {
			validator.add(new I18nMessage("veiculo", "missoes.veiculoEstaDisponivel.validation", m.getVeiculo().getDadosParaExibicao()));
		}

	}

	private void verificarDisponibilidadeDeCondutor(Missao m) throws Exception {
		Boolean condutorEstaDisponivel = Condutor.estaDisponivel(m);
		if (!condutorEstaDisponivel) {
			validator.add(new I18nMessage(CONDUTOR_STR, "missoes.condutorEstaDisponivel.validation", m.getCondutor().getDadosParaExibicao()));
		}
	}

	private void verificarDatasInicialFinal(Missao m) {
		Boolean dataSaidaAntesDeDataRetorno = m.getDataHoraSaida().before(m.getDataHoraRetorno());
		if (!dataSaidaAntesDeDataRetorno) {
			validator.add(new I18nMessage("dataHoraRetorno", "missoes.dataSaidaAntesDeDataRetorno.validation"));
		}
	}

	@SuppressWarnings("unchecked")
	private void redirecionarSeErroAoSalvar(Missao missao, Template template) throws Exception {
		if (validator.hasErrors()) {
			montarCombos();
			if (missao.getRequisicoesTransporte() != null) {
				List<RequisicaoTransporte> requisicoesTransporte = (List<RequisicaoTransporte>) getRequest().getAttribute(REQUISICOES_TRANSPORTE_STR);
				for (int i = 0; i < missao.getRequisicoesTransporte().size(); i++)
					if (missao.getRequisicoesTransporte().get(i) != null) {
						RequisicaoTransporte req = RequisicaoTransporte.AR.findById(missao.getRequisicoesTransporte().get(i).getId());
						missao.getRequisicoesTransporte().set(i, req);
					}

				requisicoesTransporte.removeAll(missao.getRequisicoesTransporte());
				result.include(REQUISICOES_TRANSPORTE_STR, requisicoesTransporte);
				result.include("estadoRequisicao", EstadoRequisicao.ATENDIDA);
			}

			if (missao.getDataHoraSaida() != null) {
				String dataHoraSaidaStr = new SimpleDateFormat(PATTERN_DDMMYYYYHHMM).format(missao.getDataHoraSaida().getTime());
				if (!autorizacaoGI.ehAdministrador() && !autorizacaoGI.ehAdministradorMissao() && !autorizacaoGI.ehAdministradorMissaoPorComplexo()) {
					List<Condutor> condutores = new ArrayList<Condutor>();
					condutores.add(missao.getCondutor());

					List<Veiculo> veiculos = new ArrayList<Veiculo>();
					veiculos.add(missao.getVeiculo());

					result.include(CONDUTORES_STR, condutores);
					result.include(VEICULOS_STR, veiculos);
				} else {
					result.include(CONDUTORES_STR, listarCondutoresDisponiveis(missao.getId(), getTitular().getOrgaoUsuario().getId(), dataHoraSaidaStr, missao.getInicioRapido()));
					result.include(VEICULOS_STR, listarVeiculosDisponiveis(missao.getId(), getTitular().getOrgaoUsuario().getId(), dataHoraSaidaStr));
				}
			}

			renderTemplate(template, missao);
		}
	}

	private void redirecionarSeErroAoCancelar(Missao missao) throws Exception {
		if (validator.hasErrors()) {
			Template template = Template.CANCELAR;

			renderTemplate(template, missao);
		}
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/incluirInicioRapido")
	public void incluirInicioRapido(Long[] req) throws Exception {
		redirecionarCasoRequisicaoNula(req);

		Missao missao = new Missao();
		missao.setInicioRapido(PerguntaSimNao.SIM);
		missao.setRequisicoesTransporte(new ArrayList<RequisicaoTransporte>());
		for (int cont = 0; cont < req.length; cont++)
			missao.getRequisicoesTransporte().add((RequisicaoTransporte) RequisicaoTransporte.AR.findById(req[cont]));

		removerRequisicoesDoRenderArgs(missao.getRequisicoesTransporte());

		renderTemplate(Template.INICIORAPIDO, missao);
	}

	private void redirecionarCasoRequisicaoNula(Long[] req) {
		if (req == null)
			result.redirectTo(this).incluir();
	}

	@RoleAdmin
	@RoleAgente
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/editar/{id}")
	public void editar(Long id) throws Exception {
		Missao missao = Missao.AR.findById(id);
		montarDadosParaAMissao(missao);
		checarCondutorPeloUsuarioAutenticado(missao);
		checarComplexo(missao.getCpComplexo().getIdComplexo());
		MenuMontador.instance(result).recuperarMenuMissao(id, missao.getEstadoMissao());

		result.include("mostrarBotoesEditar", true);
		result.include("mostrarDadosProgramada", true);

		if("INICIADA".equals(missao.getEstadoMissao().getDescricao()))
			result.include(MOSTRAR_DADOS_INICIADA_STR, true);

		if("FINALIZADA".equals(missao.getEstadoMissao().getDescricao())) {
			result.include(MOSTRAR_DADOS_INICIADA_STR, true);
			result.include("mostrarDadosFinalizada", true);
		}

//		EstadoRequisicao.valuesComboAtendimentoMissao()
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@RoleAgente
	@Path("/buscarPelaSequence/{popUp}/{sequence*}")
	public void buscarPelaSequence(boolean popUp, String sequence) throws Exception {
		recuperarPelaSigla(sequence, popUp);
		result.redirectTo(this).ler();
	}

	@Path("/ler")
	public void ler() {
		/**
		 * Direciona para a view LER
		 */
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@RoleAgente
	@Path("/ler/{id}")
	public void ler(Long id) throws Exception {
		Missao missao = Missao.AR.findById(id);
		montarDadosParaAMissao(missao);
		MenuMontador.instance(result).recuperarMenuMissao(id, missao.getEstadoMissao());
		checarCondutorPeloUsuarioAutenticado(missao);
		checarComplexo(missao.getCpComplexo().getIdComplexo());
	}

	protected void montarCombos() throws Exception {
		Calendar ultimos7dias = Calendar.getInstance();
		ultimos7dias.add(Calendar.DATE, -7);
		CpOrgaoUsuario orgaoParametro = getTitular().getOrgaoUsuario();
		Object[] parametros = { ultimos7dias, orgaoParametro };
		EstadoRequisicao[] estados = { EstadoRequisicao.AUTORIZADA, EstadoRequisicao.PROGRAMADA, EstadoRequisicao.EMATENDIMENTO, EstadoRequisicao.NAOATENDIDA, EstadoRequisicao.ATENDIDAPARCIALMENTE };
		StringBuilder criterioBusca = new StringBuilder();
		criterioBusca.append("(dataHoraRetornoPrevisto is null or dataHoraRetornoPrevisto >= ?) and cpOrgaoUsuario = ?");
		result.of(RequisicaoController.class).recuperarRequisicoes(criterioBusca, parametros, estados);
	}

	protected void montarComboCondutor() throws Exception {
		List<Condutor> condutores = autorizacaoGI.ehAdministradorMissaoPorComplexo() ? Condutor.listarEscaladosDoComplexo(true, autorizacaoGI.recuperarComplexoAdministrador(), getTitular().getOrgaoUsuario()) : Condutor.listarEscalados(true, getTitular().getOrgaoUsuario());

		result.include("condutoresEscalados", condutores);
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/excluir/{id}")
	public void excluir(Long id) throws Exception {
		Missao missao = Missao.AR.findById(id);
		checarCondutorPeloUsuarioAutenticado(missao);
		checarComplexo(missao.getCpComplexo().getIdComplexo());
		missao.delete();

		result.redirectTo(this).listar();
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@RoleAgente
	@Path("/listarVeiculosECondutoresDisponiveis")
	public void listarVeiculosECondutoresDisponiveis(String nomePropriedade, String nomePropriedade1, Long idMissao, String dataSaida, String veiculosDisp, PerguntaSimNao inicioRapido) throws Exception {
		Missao missao = Missao.AR.findById(idMissao);
		String opcaoSelecionada = " selected = 'selected'";
		String selectDesabilitado = " disabled = 'disabled'";

		List<Veiculo> veiculosDisponiveis = null;

		if ("".equals(veiculosDisp))
			veiculosDisponiveis = listarVeiculosDisponiveis(idMissao, getTitular().getOrgaoUsuario().getId(), dataSaida);
		else
			veiculosDisponiveis = listarVeiculosDisponiveis(veiculosDisp);

		StringBuilder htmlSelectVeiculos = new StringBuilder();
		htmlSelectVeiculos.append("<select id='selveiculosdisponiveis' name='" + nomePropriedade.toString() + "' size='1' ");

		if (autorizacaoGI.ehAgente())
			htmlSelectVeiculos.append(selectDesabilitado);

		htmlSelectVeiculos.append(">");

		for (Veiculo veiculo : veiculosDisponiveis) {
			htmlSelectVeiculos.append("<option value='" + veiculo.getId() + "'");
			if (missao != null && veiculo.equals(missao.getVeiculo()))
				htmlSelectVeiculos.append(opcaoSelecionada);

			htmlSelectVeiculos.append(">" + veiculo.getDadosParaExibicao());
		}

		htmlSelectVeiculos.append("</option>" + "</select>");

		List<Condutor> condutoresDisponiveis = listarCondutoresDisponiveis(idMissao, getTitular().getOrgaoUsuario().getId(), dataSaida, inicioRapido);
		StringBuilder htmlSelectCondutores = new StringBuilder();
		htmlSelectCondutores.append("<select id='selcondutoresdisponiveis' name='" + nomePropriedade1.toString() + "' size='1' ");

		if (autorizacaoGI.ehAgente())
			htmlSelectCondutores.append(selectDesabilitado);

		htmlSelectCondutores.append(">");

		for (Condutor condutor : condutoresDisponiveis) {
			htmlSelectCondutores.append("<option value='" + condutor.getId() + "'");
			if (missao != null && condutor.equals(missao.getCondutor()))
				htmlSelectCondutores.append(opcaoSelecionada);

			htmlSelectCondutores.append(">" + condutor.getDadosParaExibicao());

		}
		htmlSelectCondutores.append("</option>" + "</select>");

		String html = htmlSelectVeiculos.toString() + "@" + htmlSelectCondutores.toString();

		result.use(Results.http()).body(html);
	}

	@SuppressWarnings("unchecked")
	@Path("/buscarPorCondutoreseEscala")
	public void buscarPorCondutoreseEscala(EscalaDeTrabalho escala) {
		SimpleDateFormat formatar = new SimpleDateFormat(PATTERN_DDMMYYYYHHMM);
		String dataFormatadaFimOracle;
		if (escala.getDataVigenciaFim() == null) {
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

		Query qry = Missao.AR.em().createQuery(qrl);
		try {
			missoes = (List<Missao>) qry.getResultList();
		} catch (NoResultException ex) {
			missoes = null;
		}

		result.include(MISSOES_STR, missoes);
	}

	protected void checarCategoriaCNHVeiculoCondutor(Missao missao) throws Exception {
		CategoriaCNH categoriaCondutor1 = null;
		CategoriaCNH categoriaCondutor2 = null;

		if (missao.getCondutor().getCategoriaCNH().getDescricao().length() == 2) {
			switch (missao.getCondutor().getCategoriaCNH().getDescricao()) {
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
			default:
				break;
			}

			if (categoriaCondutor1 != null && categoriaCondutor2 != null && (categoriaCondutor1.compareTo(missao.getVeiculo().getCategoriaCNH()) >= 0 || categoriaCondutor2.compareTo(missao.getVeiculo().getCategoriaCNH()) >= 0))
				return;
		} else {
			if (missao.getCondutor().getCategoriaCNH().compareTo(missao.getVeiculo().getCategoriaCNH()) >= 0) {
				return;
			}
		}

		validator.add(new I18nMessage("categoriaCnhCondutor", "missao.categoriaCNHCondutorErradaParaVeiculo.validation"));
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/incluirComRequisicoes")
	public void incluirComRequisicoes(Long[] req) throws Exception {
		redirecionarCasoRequisicaoNula(req);

		Missao missao = new Missao();
		missao.setInicioRapido(PerguntaSimNao.NAO);
		missao.setRequisicoesTransporte(new ArrayList<RequisicaoTransporte>());

		for (int cont = 0; cont < req.length; cont++)
			missao.getRequisicoesTransporte().add((RequisicaoTransporte) RequisicaoTransporte.AR.findById(req[cont]));

		removerRequisicoesDoRenderArgs(missao.getRequisicoesTransporte());
		result.include(MISSAO_STR, missao);

		result.forwardTo(this).incluir();
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/incluir")
	public void incluir() {
		Missao missao = new Missao();
		missao.setInicioRapido(PerguntaSimNao.NAO);
		MenuMontador.instance(result).recuperarMenuMissao(missao.getId(), missao.getEstadoMissao());

		result.include(MISSAO_STR, missao);
	}

	private void removerRequisicoesDoRenderArgs(List<RequisicaoTransporte> requisicoesTransporte) {
		@SuppressWarnings("unchecked")
		List<RequisicaoTransporte> requisicoes = (List<RequisicaoTransporte>) getRequest().getAttribute(REQUISICOES_TRANSPORTE_STR);

		if (requisicoes != null)
			requisicoes.removeAll(requisicoesTransporte);

		result.include(REQUISICOES_TRANSPORTE_STR, requisicoes);
	}

	protected void recuperarPelaSigla(String sequence, Boolean popUp) throws Exception {
		Missao missao = Missao.buscar(sequence);
		montarDadosParaAMissao(missao);
		MenuMontador.instance(result).recuperarMenuMissao(missao.getId(), missao.getEstadoMissao());

		if (popUp != null)
			result.include("mostrarMenu", !popUp);
		else
			result.include("mostrarMenu", true);

		checarCondutorPeloUsuarioAutenticado(missao);
		checarComplexo(missao.getCpComplexo().getIdComplexo());
	}

	protected void montarDadosParaAMissao(Missao missao) throws Exception {
		removerRequisicoesDoRenderArgs(missao.getRequisicoesTransporte());
		SimpleDateFormat formatar = new SimpleDateFormat(PATTERN_DDMMYYYYHHMM);
		String dataHoraSaidaStr = formatar.format(missao.getDataHoraSaida().getTime());
		List<Condutor> condutores = listarCondutoresDisponiveis(missao.getId(), getTitular().getOrgaoUsuario().getId(), dataHoraSaidaStr, missao.getInicioRapido());
		boolean encontrouCondutor = false;

		if (condutores != null && !condutores.isEmpty())
			for (Condutor condutor : condutores) {
				if (condutor.getId().equals(missao.getCondutor().getId())) {
					encontrouCondutor = true;
					break;
				}
			}
		else
			condutores = new ArrayList<Condutor>();

		if (!encontrouCondutor)
			condutores.add(missao.getCondutor());

		StringBuilder veiculosDisp = new StringBuilder();
		for (RequisicaoTransporte req : missao.getRequisicoesTransporte())
			if (req.getServicoVeiculo() != null) {
				req = RequisicaoTransporte.AR.findById(req.getId());
				veiculosDisp.append(req.getServicoVeiculo().getVeiculo().getId()).append(", ");
			}

		List<Veiculo> veiculos = new ArrayList<Veiculo>();

		if ("".equals(veiculosDisp.toString()))
			veiculos = listarVeiculosDisponiveis(missao.getId(), getTitular().getOrgaoUsuario().getId(), dataHoraSaidaStr);
		else
			veiculos = listarVeiculosDisponiveis(veiculosDisp.toString());

		boolean encontrouVeiculo = false;

		if (veiculos != null && !veiculos.isEmpty())
			for (Veiculo veiculo : veiculos) {
				if (veiculo.getId().equals(missao.getVeiculo().getId())) {
					encontrouVeiculo = true;
					break;
				}
			}
		else
			veiculos = new ArrayList<Veiculo>();

		if (!encontrouVeiculo)
			veiculos.add(missao.getVeiculo());

		result.include(CONDUTORES_STR, condutores);
		result.include(VEICULOS_STR, veiculos);
		result.include(MISSAO_STR, missao);

		EstadoRequisicao estadoRequisicao = EstadoRequisicao.ATENDIDA;
		EstadoMissao estadoMissao = EstadoMissao.PROGRAMADA;

		EstadoRequisicao[] estados = new EstadoRequisicao[missao.getRequisicoesTransporte().size()];
		for (int i = 0; i < estados.length; i++)
			estados[i] = EstadoRequisicao.ATENDIDA;

		result.include("estados", estados);
		result.include("estadoRequisicao", estadoRequisicao);
		result.include(ESTADO_MISSAO_STR, estadoMissao);
	}

	protected void checarCondutorPeloUsuarioAutenticado(Missao missao) throws Exception {
		if (autorizacaoGI.ehAgente()) {
			if (missao.getId() == 0)
				throw new Exception(new I18nMessage(MISSOES_STR, "missoes.autorizacaoGI.ehAgente.exception").getMessage());

			if (!getTitular().equivale(missao.getCondutor().getDpPessoa()))
				try {
					throw new Exception(new I18nMessage(MISSOES_STR, MISSOES_AUTORIZACAO_GI_SEM_ACESSO_EXCEPTION).getMessage());
				} catch (Exception e) {
					throw new AplicacaoException(e.getMessage());
				}
		}
	}

	private List<Veiculo> listarVeiculosDisponiveis(String veiculosDisp) throws Exception {
		List<Veiculo> veiculos = new ArrayList<Veiculo>();
		List<String> itens = Arrays.asList(veiculosDisp.substring(0, veiculosDisp.length() - 2).split("\\s*,\\s*"));

		if (itens.isEmpty())
			for (String item : itens) {
				Veiculo itemVeiculo = new Veiculo();
				itemVeiculo = Veiculo.AR.findById(Long.parseLong(item));

				if (!veiculos.contains(itemVeiculo))
					veiculos.add(itemVeiculo);
			}

		return veiculos;
	}

	private List<Veiculo> listarVeiculosDisponiveis(Long idMissao, Long idOrgao, String dataSaida) {
		return Veiculo.listarDisponiveis(dataSaida, idMissao, idOrgao);
	}

	private List<Condutor> listarCondutoresDisponiveis(Long idMissao, Long idOrgao, String dataSaida, PerguntaSimNao inicioRapido) throws Exception {
		return Condutor.listarDisponiveis(dataSaida, idMissao, idOrgao, inicioRapido);
	}

	private void checarComplexo(Long idComplexo) throws Exception {
		if (autorizacaoGI.ehAdministradorMissaoPorComplexo()) {
			if (!getComplexoAdministrado().getIdComplexo().equals(idComplexo)) {
				try {
					throw new Exception(new I18nMessage(MISSOES_STR, MISSOES_AUTORIZACAO_GI_SEM_ACESSO_EXCEPTION).getMessage());
				} catch (Exception e) {
					throw new AplicacaoException(e.getMessage());
				}
			} else if (autorizacaoGI.ehAprovador() && !recuperarComplexoPadrao().getIdComplexo().equals(idComplexo)) {
				try {
					throw new Exception(new I18nMessage(MISSOES_STR, MISSOES_AUTORIZACAO_GI_SEM_ACESSO_EXCEPTION).getMessage());
				} catch (Exception e) {
					throw new AplicacaoException(e.getMessage());
				}
			}
		}
	}

	private void renderTemplate(Template template, Missao missao) throws Exception {

		switch(template) {
			case EDITAR:
				result.redirectTo(this).editar(missao.getId());
				break;

			case INCLUIR:
				result.redirectTo(this).incluir();
				break;

			case INICIORAPIDO:
				result.redirectTo(this).iniciarMissaoRapido(missao, null);
				break;

			case CANCELAR:
				result.redirectTo(this).cancelarMissao(missao);
				break;

			default:
				break;
		}
	}
}