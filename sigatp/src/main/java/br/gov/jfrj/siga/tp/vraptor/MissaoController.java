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
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.validator.I18nMessage;
import br.com.caelum.vraptor.view.Results;
import br.gov.jfrj.siga.base.AplicacaoException;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.dp.dao.CpDao;
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
import br.gov.jfrj.siga.tp.model.TipoRequisicao;
import br.gov.jfrj.siga.tp.model.TpDao;
import br.gov.jfrj.siga.tp.model.Veiculo;
import br.gov.jfrj.siga.tp.util.PerguntaSimNao;
import br.gov.jfrj.siga.vraptor.SigaObjects;

@Resource
@Path("/app/missao")
public class MissaoController extends TpController {

	private AutorizacaoGI autorizacaoGI;

	public MissaoController(HttpServletRequest request, Result result, CpDao dao, Localization localization, Validator validator, SigaObjects so, EntityManager em, AutorizacaoGI autorizacaoGI){
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
		List<Missao> missoes = recuperarMissoes("cpOrgaoUsuario = ? and estadoMissao in (?,?,?,?)", parametros);
		EstadoMissao estadoMissao = EstadoMissao.PROGRAMADA;
		MenuMontador.instance(result).recuperarMenuMissoes(null);
		Condutor condutor = new Condutor();

		result.include("missoes", missoes);
		result.include("estadoMissao", estadoMissao);
		result.include("condutor", condutor);
	}

	private List<Missao> recuperarMissoes(String criterioBusca, Object[] parametros) throws Exception {
		if (!autorizacaoGI.ehAdministrador() && !autorizacaoGI.ehAdministradorMissao() && !autorizacaoGI.ehAdministradorMissaoPorComplexo()) {
			Condutor condutorLogado = Condutor.recuperarLogado(getTitular(), getTitular().getOrgaoUsuario());
			if (condutorLogado != null) {
				criterioBusca = criterioBusca + " and condutor = ?";
				Object[] parametrosFiltrado = new Object[parametros.length + 1];
				for (int i = 0; i < parametros.length; i++) {
					parametrosFiltrado[i] = parametros[i];
				}
				parametrosFiltrado[parametros.length] = condutorLogado;
				parametros = parametrosFiltrado;
			}

		} else if (autorizacaoGI.ehAdministradorMissaoPorComplexo()) {
			criterioBusca = criterioBusca + " and cpComplexo = ?";
			Object[] parametrosFiltrado = new Object[parametros.length + 1];
			for (int i = 0; i < parametros.length; i++) {
				parametrosFiltrado[i] = parametros[i];
			}
			if (autorizacaoGI.ehAdministradorMissaoPorComplexo()) {
				parametrosFiltrado[parametros.length] = getComplexoAdministrado();
			}

			parametros = parametrosFiltrado;
		}

		List<Missao> missoes = Missao.AR.find(criterioBusca + " order by dataHoraSaida desc", parametros).fetch();

		return missoes;
	}

	@RoleAgente
	@Path("/listarPorCondutorLogado")
	public void listarPorCondutorLogado() throws Exception {
		Condutor condutorLogado = Condutor.recuperarLogado(getTitular(), getTitular().getOrgaoUsuario());
		List<Missao> missoes = Missao.buscarTodasAsMissoesPorCondutor(condutorLogado);
		MenuMontador.instance(result).recuperarMenuMissoes(null);
		EstadoMissao estadoMissao = EstadoMissao.PROGRAMADA;
		Condutor condutor = new Condutor();

		result.include("missoes", missoes);
		result.include("estadoMissao", estadoMissao);
		result.include("condutor", condutor);

		result.redirectTo(this).listar();
	}

	@RoleAgente
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/listarPorCondutor")
	public void listarPorCondutor(Condutor condutorEscalado) throws Exception {
		condutorEscalado = Condutor.AR.findById(condutorEscalado.getId());
		List<Missao> missoes = Missao.buscarTodasAsMissoesPorCondutor(condutorEscalado);
		MenuMontador.instance(result).recuperarMenuMissoesPorCondutor();
		EstadoMissao estadoMissao = EstadoMissao.PROGRAMADA;
		validarListarParaCondutorEscalado(condutorEscalado);

		result.include("missoes", missoes);
		result.include("condutorEscalado", condutorEscalado);
		result.include("estadoMissao", estadoMissao);

		result.redirectTo(this).listar();
	}

	protected void validarListarParaCondutorEscalado(Condutor condutorEscalado) throws Exception {
		Missao missao = new Missao();
		missao.setId(-1L);
		missao.condutor = condutorEscalado;
		checarCondutorPeloUsuarioAutenticado(missao);
	}

	@RoleAgente
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/listarFiltrado")
	public void listarFiltrado(EstadoMissao estado) throws Exception {
		if (estado == null)
			estado = EstadoMissao.PROGRAMADA;

		EstadoMissao estadoMissao = estado;
		Object[] parametros = { getTitular().getOrgaoUsuario(), estadoMissao };
		List<Missao> missoes = recuperarMissoes("cpOrgaoUsuario = ? and estadoMissao = ?", parametros);
		MenuMontador.instance(result).recuperarMenuMissoes(estado);
		Condutor condutor = new Condutor();

		result.include("missoes", missoes);
		result.include("estadoMissao", estadoMissao);
		result.include("condutor", condutor);

		result.redirectTo(this).listar();
	}

	@RoleAgente
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/salvar")
	public void salvar(@Valid Missao missao, List<RequisicaoTransporte> requisicoesTransporte_alt, List<RequisicaoTransporte> requisicoesTransporte_ant) throws Exception {
		DpPessoa dpPessoa = getCadastrante();
		Template template;

		missao.cpOrgaoUsuario = getTitular().getOrgaoUsuario();

		if (missao.getId() > 0)
			template = Template.EDITAR;
		else {
			missao.setSequence(missao.cpOrgaoUsuario);
			template = Template.INCLUIR;
		}

		if (requisicoesTransporte_alt == null || requisicoesTransporte_alt.size() == 0) {
			missao.requisicoesTransporte = requisicoesTransporte_alt;
			validator.add(new I18nMessage("requisicoesTransporte", "missao.requisicoesTransporte.validation"));
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

		if (novaMissao)
			gravarAndamentos(dpPessoa, "PROGRAMADO", missao.requisicoesTransporte, missao, EstadoRequisicao.PROGRAMADA);
		else {
			deletarAndamentos(requisicoesTransporte_ant, missao);
			atualizarAndamentos(missao);
		}

		listarFiltrado(missao.estadoMissao);
	}

	private boolean validarRequisicoesDeServico(Missao missao, Template template) throws Exception {
		Boolean temRequisicaoDeServico = false;
		Veiculo veiculoInicial = null;

		for (Iterator<RequisicaoTransporte> iterator = missao.requisicoesTransporte.iterator(); iterator.hasNext();) {
			RequisicaoTransporte req = iterator.next();
			req = RequisicaoTransporte.AR.findById(req.getId());

			if (req.getServicoVeiculo() != null) {
				temRequisicaoDeServico = true;
				if (veiculoInicial == null)
					veiculoInicial = req.getServicoVeiculo().getVeiculo();
				else {
					if (!veiculoInicial.equals(req.getServicoVeiculo().getVeiculo())) {
						validator.add(new I18nMessage("veiculo", "missoes.veiculo.validation"));
						redirecionarSeErroAoSalvar(missao, template);
					}
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

		@SuppressWarnings("unused")
		int i = 0;
		for (RequisicaoTransporte requisicaoTransporte : requisicoesTransporte) {
			gravaAndamento(dpPessoa, descricao, missao, estadoRequisicao, requisicaoTransporte);
			i++;
		}
	}

	private void deletarAndamentos(List<RequisicaoTransporte> requisicoesTransporte, Missao missao) throws Exception {

		for (RequisicaoTransporte requisicaoTransporte : requisicoesTransporte) {
			List<Andamento> andamentos = Andamento.AR.find("requisicaoTransporte.id = ? order by id desc", requisicaoTransporte.getId()).fetch();
			for (Andamento andamento : andamentos) {
				if (andamento.getMissao() != null && andamento.getMissao().getId().equals(missao.getId()) && andamento.getEstadoRequisicao().equals(EstadoRequisicao.PROGRAMADA)) {
					andamento.delete();
				}
			}
		}
	}

	private void atualizarAndamentos(Missao missao) throws Exception {

		for (RequisicaoTransporte requisicaoTransporte : missao.requisicoesTransporte) {
			List<Andamento> andamentos = Andamento.AR.find("requisicaoTransporte.id = ? order by id desc", requisicaoTransporte.getId()).fetch();
			for (Andamento andamento : andamentos) {
				Boolean novoAndamento = true;
				if (andamento.getMissao() != null && andamento.getMissao().getId().equals(missao.getId())) {
					novoAndamento = false;
					break;
				}
				if (novoAndamento) {
					gravaAndamento(getCadastrante(), "PROGRAMADA", missao, EstadoRequisicao.PROGRAMADA, requisicaoTransporte);
					break;
				}
			}
		}
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
		RequisicaoVsEstado[] requisicoesVsEstados = new RequisicaoVsEstado[missao.requisicoesTransporte.size()];
		for (RequisicaoTransporte requisicaoTransporte : missao.requisicoesTransporte) {
			RequisicaoVsEstado requisicaoVsEstados = new RequisicaoVsEstado();
			requisicaoVsEstados.idRequisicaoTransporte = requisicaoTransporte.getId();
			requisicaoVsEstados.estado = requisicaoTransporte.getUltimoEstado();
			requisicoesVsEstados[i] = requisicaoVsEstados;
			i = i + 1;
		}

		result.include("missao", missao);
		result.include("requisicoesVsEstados", requisicoesVsEstados);
		result.include("mostrarBotoesFinalizar", true);
		result.include("mostrarDadosProgramada", true);
		result.include("mostrarDadosIniciada", true);
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
		missao.cpOrgaoUsuario = getTitular().getOrgaoUsuario();
		missao.responsavel = dpPessoa;
		missao.estadoMissao = EstadoMissao.FINALIZADA;
		checarCondutorPeloUsuarioAutenticado(missao);
		missao = recuperarComplexoPeloPerfil(missao);
		missao.save();

		RequisicaoTransporte[] requisicoes = missao.requisicoesTransporte.toArray(new RequisicaoTransporte[missao.requisicoesTransporte.size()]);
		EstadoRequisicao[] estadosRequisicao = new EstadoRequisicao[missao.requisicoesTransporte.size()];
		for (int i = 0; i < requisicoes.length; i++) {
			estadosRequisicao[i] = RequisicaoVsEstado.encontrarEstadoNaLista(requisicoesVsEstados, requisicoes[i].getId());
		}

		gravarAndamentos(dpPessoa, "PELA MISSAO N." + missao.getSequence(), requisicoes, missao, estadosRequisicao);
		listarFiltrado(missao.estadoMissao);
	}

	private void verificarOdometrosSaidaRetorno(Missao missao) {
		if (missao.odometroSaidaEmKm > missao.odometroRetornoEmKm) {
			validator.add(new I18nMessage("odometroRetornoEmKm", "missoes.odometroRetornoEmKm.validation"));
		}
	}

	private void verificarOdometroSaidaZerado(Missao missao) {
		if (missao.odometroSaidaEmKm == 0) {
			validator.add(new I18nMessage("odometroRetornoEmKm", "O valor do od&ocirc;metro de sa&iacute;da deve ser maior que zero."));
		}
	}

	private void verificarOdometroRetornoZerado(Missao missao) {
		if (missao.odometroRetornoEmKm == 0) {
			validator.add(new I18nMessage("odometroRetornoEmKm", "O valor do od&ocirc;metro de retorno deve ser maior que zero."));
		}
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@RoleAgente
	@Path("/iniciarMissao")
	public void iniciarMissao(@Valid Missao missao, List<RequisicaoTransporte> requisicoesTransporte_alt, List<RequisicaoTransporte> requisicoesTransporte_ant, String veiculosDisp) throws Exception {
		verificarDisponibilidadeDeCondutor(missao);
		verificarOdometroSaidaZerado(missao);
		DpPessoa dpPessoa = getCadastrante();

		if (requisicoesTransporte_alt == null || requisicoesTransporte_alt.size() == 0) {
			missao.requisicoesTransporte = requisicoesTransporte_alt;
			validator.add(new I18nMessage("requisicoesTransporte", "missao.requisicoesTransporte.validation"));
		}

		checarCategoriaCNHVeiculoCondutor(missao);

		redirecionarSeErroAoSalvar(missao, Template.INICIAR);

		boolean temRequisicaoDeServico = validarRequisicoesDeServico(missao, Template.INICIAR);

		if (!temRequisicaoDeServico) {
			verificarDisponibilidadeDeVeiculo(missao);
		}

		missao.cpOrgaoUsuario = getTitular().getOrgaoUsuario();
		missao.responsavel = dpPessoa;
		missao.estadoMissao = EstadoMissao.INICIADA;

		checarCondutorPeloUsuarioAutenticado(missao);
		missao = recuperarComplexoPeloPerfil(missao);
		checarComplexo(missao.cpComplexo.getIdComplexo());

		missao.save();
		gravarAndamentos(dpPessoa, "PELA MISSAO N." + missao.getSequence(), missao.requisicoesTransporte, missao, EstadoRequisicao.EMATENDIMENTO);
		buscarPelaSequence(false, missao.getSequence());
	}

	protected Missao recuperarComplexoPeloPerfil(Missao missao) throws Exception {
		if (autorizacaoGI.ehAgente() || autorizacaoGI.ehAdministradorMissaoPorComplexo()) {
			RequisicaoTransporte req1 = RequisicaoTransporte.AR.findById(missao.requisicoesTransporte.get(0).getId());
			missao.cpComplexo = req1.getCpComplexo();
		} else
			missao.cpComplexo = recuperarComplexoPadrao();

		return missao;
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/iniciarMissaoRapido")
	public void iniciarMissaoRapido(@Valid Missao missao, List<RequisicaoTransporte> requisicoesTransporte_alt) throws Exception {
		DpPessoa dpPessoa = getCadastrante();
		Template template = Template.INICIORAPIDO;
		missao.cpOrgaoUsuario = getTitular().getOrgaoUsuario();
		missao.setSequence(missao.cpOrgaoUsuario);
		missao.responsavel = dpPessoa;

		if (requisicoesTransporte_alt == null || requisicoesTransporte_alt.size() == 0) {
			missao.requisicoesTransporte = requisicoesTransporte_alt;
			validator.add(new I18nMessage("requisicoesTransporte", "missao.requisicoesTransporte.validation"));
		}

		boolean temRequisicaoDeServico = validarRequisicoesDeServico(missao, template);

		if (!temRequisicaoDeServico)
			verificarDisponibilidadeDeVeiculo(missao);

		checarCategoriaCNHVeiculoCondutor(missao);
		redirecionarSeErroAoSalvar(missao, template);
		missao.dataHora = Calendar.getInstance();
		missao.inicioRapido = PerguntaSimNao.SIM;
		missao = recuperarComplexoPeloPerfil(missao);
		checarCondutorPeloUsuarioAutenticado(missao);
		checarComplexo(missao.cpComplexo.getIdComplexo());

		missao.save();

		gravarAndamentos(dpPessoa, "PROGRAMADO POR INICIO RAPIDO PARA MISSAO NO. " + missao.getSequence(), missao.requisicoesTransporte, missao, EstadoRequisicao.PROGRAMADA);
		validarRequisicoesDeServico(missao, template);
		missao.estadoMissao = EstadoMissao.INICIADA;

		missao.save();
		gravarAndamentos(dpPessoa, "INICIO RAPIDO PELA MISSAO N." + missao.getSequence(), missao.requisicoesTransporte, missao, EstadoRequisicao.EMATENDIMENTO);

		result.redirectTo(this).buscarPelaSequence(false, missao.getSequence());
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
		checarComplexo(missao.cpComplexo.getIdComplexo());

		result.include("missao", missao);
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/cancelar/{id}")
	public void cancelar(Long id) throws Exception {
		Missao missao = Missao.AR.findById(id);
		checarCondutorPeloUsuarioAutenticado(missao);
		checarComplexo(missao.cpComplexo.getIdComplexo());

		result.include("missao", missao);
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/cancelarMissao")
	public void cancelarMissao(@Valid Missao missao) throws Exception {
		verificarJustificativaPreenchida(missao);

		DpPessoa dpPessoa = getCadastrante();
		redirecionarSeErroAoCancelar(missao);
		missao.cpOrgaoUsuario = getTitular().getOrgaoUsuario();
		missao.responsavel = dpPessoa;
		missao.estadoMissao = EstadoMissao.CANCELADA;
		checarCondutorPeloUsuarioAutenticado(missao);
		checarComplexo(missao.cpComplexo.getIdComplexo());
		missao.cpComplexo = missao.requisicoesTransporte.get(0).getCpComplexo();
		missao.save();

		gravarAndamentos(dpPessoa, "MISSAO CANCELADA", missao.requisicoesTransporte, missao, EstadoRequisicao.NAOATENDIDA);

		listarFiltrado(EstadoMissao.CANCELADA);
	}

	private void verificarJustificativaPreenchida(Missao missao) {
		if (missao.justificativa.isEmpty()) {
			validator.add(new I18nMessage("justificativa", "missoes.justificativa.validation"));
		}
	}

	private void verificarDisponibilidadeDeVeiculo(Missao m) throws Exception {
		Boolean veiculoEstaDisponivel = Veiculo.estaDisponivel(m);
		if (!veiculoEstaDisponivel) {
			validator.add(new I18nMessage("veiculo", "missoes.veiculoEstaDisponivel.validation", m.veiculo.getDadosParaExibicao()));
		}

	}

	private void verificarDisponibilidadeDeCondutor(Missao m) throws Exception {
		Boolean condutorEstaDisponivel = Condutor.estaDisponivel(m);
		if (!condutorEstaDisponivel) {
			validator.add(new I18nMessage("condutor", "missoes.condutorEstaDisponivel.validation", m.condutor.getDadosParaExibicao()));
		}
	}

	private void verificarDatasInicialFinal(Missao m) {
		Boolean dataSaidaAntesDeDataRetorno = m.dataHoraSaida.before(m.dataHoraRetorno);
		if (!dataSaidaAntesDeDataRetorno) {
			validator.add(new I18nMessage("dataHoraRetorno", "missoes.dataSaidaAntesDeDataRetorno.validation"));
		}
	}

	@SuppressWarnings("unchecked")
	private void redirecionarSeErroAoSalvar(Missao missao, Template template) throws Exception {
		if (validator.hasErrors()) {
			montarCombos();
			if (missao.requisicoesTransporte != null) {
				List<RequisicaoTransporte> requisicoesTransporte = (List<RequisicaoTransporte>) getRequest().getAttribute("requisicoesTransporte");
				for (int i = 0; i < missao.requisicoesTransporte.size(); i++)
					if (missao.requisicoesTransporte.get(i) != null) {
						RequisicaoTransporte req = RequisicaoTransporte.AR.findById(missao.requisicoesTransporte.get(i).getId());
						missao.requisicoesTransporte.set(i, req);
					}

				requisicoesTransporte.removeAll(missao.requisicoesTransporte);
				result.include("requisicoesTransporte", requisicoesTransporte);
				result.include("estadoRequisicao", EstadoRequisicao.ATENDIDA);
			}

			if (missao.dataHoraSaida != null) {
				String dataHoraSaidaStr = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(missao.dataHoraSaida.getTime());
				if (!autorizacaoGI.ehAdministrador() && !autorizacaoGI.ehAdministradorMissao() && !autorizacaoGI.ehAdministradorMissaoPorComplexo()) {
					List<Condutor> condutores = new ArrayList<Condutor>();
					condutores.add(missao.condutor);

					List<Veiculo> veiculos = new ArrayList<Veiculo>();
					veiculos.add(missao.veiculo);

					result.include("condutores", condutores);
					result.include("veiculos", veiculos);
				} else {
					result.include("condutores", listarCondutoresDisponiveis(missao.getId(), getTitular().getOrgaoUsuario().getId(), dataHoraSaidaStr, missao.inicioRapido));
					result.include("veiculos", listarVeiculosDisponiveis(missao.getId(), getTitular().getOrgaoUsuario().getId(), dataHoraSaidaStr));
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
		if (req == null)
			result.redirectTo(this).incluir();

		Missao missao = new Missao();
		missao.inicioRapido = PerguntaSimNao.SIM;
		missao.requisicoesTransporte = new ArrayList<RequisicaoTransporte>();
		for (int cont = 0; cont < req.length; cont++)
			missao.requisicoesTransporte.add((RequisicaoTransporte) RequisicaoTransporte.AR.findById(req[cont]));

		removerRequisicoesDoRenderArgs(missao.requisicoesTransporte);

		renderTemplate(Template.INICIORAPIDO, missao);
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
		checarComplexo(missao.cpComplexo.getIdComplexo());
		MenuMontador.instance(result).recuperarMenuMissao(id, missao.estadoMissao);

		result.include("mostrarBotoesEditar", true);
		result.include("mostrarDadosProgramada", true);

		if(missao.estadoMissao.equals("INICIADA"))
			result.include("mostrarDadosIniciada", true);

		if(missao.estadoMissao.equals("FINALIZADA")) {
			result.include("mostrarDadosIniciada", true);
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
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@RoleAgente
	@Path("/ler/{id}")
	public void ler(Long id) throws Exception {
		Missao missao = Missao.AR.findById(id);
		montarDadosParaAMissao(missao);
		MenuMontador.instance(result).recuperarMenuMissao(id, missao.estadoMissao);
		checarCondutorPeloUsuarioAutenticado(missao);
		checarComplexo(missao.cpComplexo.getIdComplexo());
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
		List<Condutor> condutores = (autorizacaoGI.ehAdministradorMissaoPorComplexo() ? Condutor.listarEscaladosDoComplexo(true, autorizacaoGI.recuperarComplexoAdministrador(), getTitular().getOrgaoUsuario()) : Condutor.listarEscalados(true, getTitular().getOrgaoUsuario()));

		result.include("condutoresEscalados", condutores);
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/excluir/{id}")
	public void excluir(Long id) throws Exception {
		Missao missao = Missao.AR.findById(id);
		checarCondutorPeloUsuarioAutenticado(missao);
		checarComplexo(missao.cpComplexo.getIdComplexo());
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

		if (veiculosDisp.equals(""))
			veiculosDisponiveis = listarVeiculosDisponiveis(idMissao, getTitular().getOrgaoUsuario().getId(), dataSaida);
		else
			veiculosDisponiveis = listarVeiculosDisponiveis(veiculosDisp);

		StringBuffer htmlSelectVeiculos = new StringBuffer();
		htmlSelectVeiculos.append("<select id='selveiculosdisponiveis' name='" + nomePropriedade.toString() + "' size='1' ");

		if (autorizacaoGI.ehAgente())
			htmlSelectVeiculos.append(selectDesabilitado);

		htmlSelectVeiculos.append(">");

		for (Veiculo veiculo : veiculosDisponiveis) {
			htmlSelectVeiculos.append("<option value='" + veiculo.getId() + "'");
			if (missao != null && veiculo.equals(missao.veiculo))
				htmlSelectVeiculos.append(opcaoSelecionada);

			htmlSelectVeiculos.append(">" + veiculo.getDadosParaExibicao());
		}

		htmlSelectVeiculos.append("</option>" + "</select>");

		List<Condutor> condutoresDisponiveis = listarCondutoresDisponiveis(idMissao, getTitular().getOrgaoUsuario().getId(), dataSaida, inicioRapido);
		StringBuffer htmlSelectCondutores = new StringBuffer();
		htmlSelectCondutores.append("<select id='selcondutoresdisponiveis' name='" + nomePropriedade1.toString() + "' size='1' ");

		if (autorizacaoGI.ehAgente())
			htmlSelectCondutores.append(selectDesabilitado);

		htmlSelectCondutores.append(">");

		for (Condutor condutor : condutoresDisponiveis) {
			htmlSelectCondutores.append("<option value='" + condutor.getId() + "'");
			if (missao != null && condutor.equals(missao.condutor))
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

		Query qry = Missao.AR.em().createQuery(qrl);
		try {
			missoes = (List<Missao>) qry.getResultList();
		} catch (NoResultException ex) {
			missoes = null;
		}

		result.include("missoes", missoes);
	}

	protected void checarCategoriaCNHVeiculoCondutor(Missao missao) throws Exception {
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

		validator.add(new I18nMessage("categoriaCnhCondutor", "missao.categoriaCNHCondutorErradaParaVeiculo.validation"));
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/incluirComRequisicoes")
	public void incluirComRequisicoes(Long[] req) throws Exception {
		if (req == null) {
			result.redirectTo(this).incluir();
		}
		Missao missao = new Missao();
		missao.inicioRapido = PerguntaSimNao.NAO;
		missao.requisicoesTransporte = new ArrayList<RequisicaoTransporte>();

		for (int cont = 0; cont < req.length; cont++)
			missao.requisicoesTransporte.add((RequisicaoTransporte) RequisicaoTransporte.AR.findById(req[cont]));

		removerRequisicoesDoRenderArgs(missao.requisicoesTransporte);
		result.include("missao", missao);

		result.forwardTo(this).incluir();
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/incluir")
	public void incluir() {
		Missao missao = new Missao();
		missao.inicioRapido = PerguntaSimNao.NAO;
		MenuMontador.instance(result).recuperarMenuMissao(missao.getId(), missao.estadoMissao);

		result.include("missao", missao);
	}

	private void removerRequisicoesDoRenderArgs(List<RequisicaoTransporte> requisicoesTransporte) {
		@SuppressWarnings("unchecked")
		List<RequisicaoTransporte> requisicoes = (List<RequisicaoTransporte>) getRequest().getAttribute("requisicoesTransporte");

		if (requisicoes != null)
			requisicoes.removeAll(requisicoesTransporte);

		result.include("requisicoesTransporte", requisicoes);
	}

	protected void recuperarPelaSigla(String sequence, Boolean popUp) throws Exception {
		Missao missao = Missao.buscar(sequence);
		montarDadosParaAMissao(missao);
		MenuMontador.instance(result).recuperarMenuMissao(missao.getId(), missao.estadoMissao);

		if (popUp != null)
			result.include("mostrarMenu", !popUp);
		else
			result.include("mostrarMenu", true);

		checarCondutorPeloUsuarioAutenticado(missao);
		checarComplexo(missao.cpComplexo.getIdComplexo());
	}

	protected void montarDadosParaAMissao(Missao missao) throws Exception {
		removerRequisicoesDoRenderArgs(missao.requisicoesTransporte);
		SimpleDateFormat formatar = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String dataHoraSaidaStr = formatar.format(missao.dataHoraSaida.getTime());
		List<Condutor> condutores = listarCondutoresDisponiveis(missao.getId(), getTitular().getOrgaoUsuario().getId(), dataHoraSaidaStr, missao.inicioRapido);
		boolean encontrouCondutor = false;

		if (condutores != null && !condutores.isEmpty())
			for (Condutor condutor : condutores) {
				if (condutor.getId().equals(missao.condutor.getId())) {
					encontrouCondutor = true;
					break;
				}
			}
		else
			condutores = new ArrayList<Condutor>();

		if (!encontrouCondutor)
			condutores.add(missao.condutor);

		String veiculosDisp = "";
		for (RequisicaoTransporte req : missao.requisicoesTransporte)
			if (req.getServicoVeiculo() != null) {
				req = RequisicaoTransporte.AR.findById(req.getId());
				veiculosDisp += req.getServicoVeiculo().getVeiculo().getId() + ", ";
			}

		List<Veiculo> veiculos = new ArrayList<Veiculo>();

		if (veiculosDisp.equals(""))
			veiculos = listarVeiculosDisponiveis(missao.getId(), getTitular().getOrgaoUsuario().getId(), dataHoraSaidaStr);
		else
			veiculos = listarVeiculosDisponiveis(veiculosDisp);

		boolean encontrouVeiculo = false;

		if (veiculos != null && !veiculos.isEmpty())
			for (Veiculo veiculo : veiculos) {
				if (veiculo.getId().equals(missao.veiculo.getId())) {
					encontrouVeiculo = true;
					break;
				}
			}
		else
			veiculos = new ArrayList<Veiculo>();

		if (!encontrouVeiculo)
			veiculos.add(missao.veiculo);

		result.include("condutores", condutores);
		result.include("veiculos", veiculos);
		result.include("missao", missao);

		EstadoRequisicao estadoRequisicao = EstadoRequisicao.ATENDIDA;
		EstadoMissao estadoMissao = EstadoMissao.PROGRAMADA;

		EstadoRequisicao[] estados = new EstadoRequisicao[missao.requisicoesTransporte.size()];
		for (int i = 0; i < estados.length; i++)
			estados[i] = EstadoRequisicao.ATENDIDA;

		result.include("estados", estados);
		result.include("estadoRequisicao", estadoRequisicao);
		result.include("estadoMissao", estadoMissao);
	}

	protected void checarCondutorPeloUsuarioAutenticado(Missao missao) throws Exception {
		if (autorizacaoGI.ehAgente()) {
			if (missao.getId() == 0)
				throw new Exception(new I18nMessage("missoes", "missoes.autorizacaoGI.ehAgente.exception").getMessage());

			if (!getTitular().equivale(missao.condutor.getDpPessoa()))
				try {
					throw new Exception(new I18nMessage("missoes", "missoes.autorizacaoGI.semAcesso.exception").getMessage());
				} catch (Exception e) {
					throw new AplicacaoException(e.getMessage());
				}
		}
	}

	private List<Veiculo> listarVeiculosDisponiveis(String veiculosDisp) throws NumberFormatException, Exception {
		List<Veiculo> veiculos = new ArrayList<Veiculo>();
		veiculosDisp = veiculosDisp.substring(0, veiculosDisp.length() - 2);
		List<String> itens = Arrays.asList(veiculosDisp.split("\\s*,\\s*"));

		if (itens.size() > 0)
			for (String item : itens) {
				Veiculo itemVeiculo = new Veiculo();
				itemVeiculo = Veiculo.AR.findById(Long.parseLong(item));

				if (!veiculos.contains(itemVeiculo))
					veiculos.add(itemVeiculo);
			}

		return veiculos;
	}

	private List<Veiculo> listarVeiculosDisponiveis(Long idMissao, Long idOrgao, String dataSaida) {
		List<Veiculo> veiculos = Veiculo.listarDisponiveis(dataSaida, idMissao, idOrgao);
		return veiculos;
	}

	private List<Condutor> listarCondutoresDisponiveis(Long idMissao, Long idOrgao, String dataSaida, PerguntaSimNao inicioRapido) throws Exception {
		List<Condutor> condutores = Condutor.listarDisponiveis(dataSaida, idMissao, idOrgao, inicioRapido);
		return condutores;
	}

	private void checarComplexo(Long idComplexo) throws Exception {
		if (autorizacaoGI.ehAdministradorMissaoPorComplexo()) {
			if (!getComplexoAdministrado().getIdComplexo().equals(idComplexo))
				try {
					throw new Exception(new I18nMessage("missoes", "missoes.autorizacaoGI.semAcesso.exception").getMessage());
				} catch (Exception e) {
					new AplicacaoException(e.getMessage());
				}
			else if (autorizacaoGI.ehAprovador())
				if (!recuperarComplexoPadrao().getIdComplexo().equals(idComplexo))
					try {
						throw new Exception(new I18nMessage("missoes", "missoes.autorizacaoGI.semAcesso.exception").getMessage());
					} catch (Exception e) {
						new AplicacaoException(e.getMessage());
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