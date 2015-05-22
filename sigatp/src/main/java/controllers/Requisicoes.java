package controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import play.data.validation.Valid;
import play.data.validation.Validation;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Scope.RenderArgs;
import play.mvc.With;
import br.gov.jfrj.siga.cp.CpComplexo;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdmin;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissao;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissaoComplexo;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAprovador;
import br.gov.jfrj.siga.tp.model.Andamento;
import br.gov.jfrj.siga.tp.model.TpDao;
import br.gov.jfrj.siga.tp.model.EstadoRequisicao;
import br.gov.jfrj.siga.tp.model.FinalidadeRequisicao;
import br.gov.jfrj.siga.tp.model.RequisicaoTransporte;
import br.gov.jfrj.siga.tp.model.TipoDePassageiro;
import br.gov.jfrj.siga.tp.util.MenuMontador;
import br.gov.jfrj.siga.tp.util.SigaTpException;

@With(AutorizacaoGIAntigo.class)
public class Requisicoes extends Controller {

	public static void listar() throws Exception {
		carregarRequisicoesUltimosSeteDiasPorEstados(null);
		RenderArgs.current().put("estadoRequisicao",EstadoRequisicao.PROGRAMADA);
		MenuMontador.instance().recuperarMenuListarRequisicoes(null);
		render();
	}

	@RoleAprovador
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void listarPAprovar() throws Exception {
		EstadoRequisicao estadosRequisicao[] = {EstadoRequisicao.ABERTA,EstadoRequisicao.AUTORIZADA,EstadoRequisicao.REJEITADA};
		carregarRequisicoesUltimosSeteDiasPorEstados(estadosRequisicao);
		RenderArgs.current().put("estadoRequisicao",EstadoRequisicao.ABERTA);
		MenuMontador.instance().recuperarMenuListarPAprovarRequisicoes(null);
		List<CpComplexo> complexos = TpDao.find(CpComplexo.class, "orgaoUsuario", AutorizacaoGIAntigo.titular().getOrgaoUsuario()).fetch();
		render(complexos);
	}

	@RoleAdmin
	@RoleAdminMissao
	public static void salvarNovoComplexo(Long[] req, CpComplexo novoComplexo) throws Exception {
		if (req == null) {
			throw new Exception(Messages.get("requisicoes.salvarNovoComplexo.exception"));
		}

		for (int cont = 0; cont < req.length; cont++) {
			RequisicaoTransporte requisicao = RequisicaoTransporte.AR.findById(req[cont]);
			requisicao.setCpComplexo(novoComplexo);
			requisicao.save();
		}

		listarPAprovar();
	}

	private static void carregarRequisicoesUltimosSeteDiasPorEstados(EstadoRequisicao[] estadosRequisicao) throws Exception {
		Calendar ultimos7dias = Calendar.getInstance();
		ultimos7dias.add(Calendar.DATE, -7);
		Object[] parametros = {ultimos7dias, ultimos7dias, AutorizacaoGIAntigo.titular().getOrgaoUsuario()};
		recuperarRequisicoes("((dataHoraRetornoPrevisto is null and dataHoraSaidaPrevista >= ?) or (dataHoraRetornoPrevisto >= ?)) and cpOrgaoUsuario = ? ", parametros, estadosRequisicao);
	}

	protected static void recuperarRequisicoes(String criterioBusca, Object[] parametros, EstadoRequisicao[] estadosRequisicao) throws Exception {
		if (! AutorizacaoGIAntigo.ehAdministrador() && ! AutorizacaoGIAntigo.ehAdministradorMissao()  && ! AutorizacaoGIAntigo.ehAdministradorMissaoPorComplexo() && ! AutorizacaoGIAntigo.ehAprovador()) {
			criterioBusca = criterioBusca + " and solicitante.idPessoaIni = ?";
			Object [] parametrosFiltrado = new Object[parametros.length + 1];
			for (int i = 0; i < parametros.length; i++) {
				parametrosFiltrado[i] = parametros[i];
			}
			parametrosFiltrado[parametros.length] = AutorizacaoGIAntigo.titular().getIdInicial();
			parametros = parametrosFiltrado;
		} else {

			if (AutorizacaoGIAntigo.ehAdministradorMissaoPorComplexo() || AutorizacaoGIAntigo.ehAprovador()) {
				criterioBusca = criterioBusca + " and cpComplexo = ?";
				Object [] parametrosFiltrado = new Object[parametros.length + 1];
				for (int i = 0; i < parametros.length; i++) {
					parametrosFiltrado[i] = parametros[i];
				}
				if (AutorizacaoGIAntigo.ehAdministradorMissaoPorComplexo()) {
					parametrosFiltrado[parametros.length] = AutorizacaoGIAntigo.getComplexoAdministrado();
				} else {
					parametrosFiltrado[parametros.length] = AutorizacaoGIAntigo.recuperarComplexoPadrao();
				}

				parametros = parametrosFiltrado;
			}
		}
		criterioBusca = criterioBusca + " order by dataHoraSaidaPrevista desc";

		List<RequisicaoTransporte> requisicoesTransporte = RequisicaoTransporte.AR.find(criterioBusca, parametros).fetch();
		if (estadosRequisicao != null) {
			filtrarRequisicoes(requisicoesTransporte,estadosRequisicao);
		}
		RenderArgs.current().put("requisicoesTransporte",requisicoesTransporte);
	}

	private static void filtrarRequisicoes(
			List<RequisicaoTransporte> requisicoesTransporte, EstadoRequisicao[] estadosRequisicao ) {
		Boolean filtrarRequisicao;
		for (Iterator<RequisicaoTransporte> iterator = requisicoesTransporte.iterator(); iterator.hasNext();) {
			filtrarRequisicao = true;
			RequisicaoTransporte requisicaoTransporte = (RequisicaoTransporte) iterator.next();
			for (EstadoRequisicao estadoRequisicao : estadosRequisicao) {
				if (requisicaoTransporte.getUltimoAndamento().getEstadoRequisicao().equals(estadoRequisicao))
				{
					filtrarRequisicao = false;
					break;
				}
			}
			if (filtrarRequisicao)
			{
				iterator.remove();
			}

		}
		// Collections.sort(requisicoesTransporte);
	}

	public static void listarFiltrado(EstadoRequisicao estadoRequisicao, EstadoRequisicao estadoRequisicaoP) throws Exception {
		if (estadoRequisicaoP == null) { estadoRequisicaoP = estadoRequisicao; }
		EstadoRequisicao estadosRequisicao[] = {estadoRequisicao,estadoRequisicaoP};
		carregarRequisicoesUltimosSeteDiasPorEstados(estadosRequisicao);
		MenuMontador.instance().recuperarMenuListarRequisicoes(estadoRequisicao, estadoRequisicaoP);
		renderTemplate("@listar",estadoRequisicao);
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@RoleAprovador
	public static void listarPAprovarFiltrado(EstadoRequisicao estadoRequisicao) throws Exception {
		EstadoRequisicao estadosRequisicao[] = {estadoRequisicao};
		carregarRequisicoesUltimosSeteDiasPorEstados(estadosRequisicao);
		MenuMontador.instance().recuperarMenuListarPAprovarRequisicoes(estadoRequisicao);
		renderTemplate("@listarPAprovar",estadoRequisicao);
	}

	public static void salvar(RequisicaoTransporte requisicaoTransporte, TipoDePassageiro[] tiposDePassageiros, boolean checkRetorno, boolean checkSemPassageiros) throws Exception {
		if ((requisicaoTransporte.getDataHoraSaidaPrevista() != null) && (requisicaoTransporte.getDataHoraRetornoPrevisto() != null) && (!requisicaoTransporte.ordemDeDatasCorreta())){
			Validation.addError("dataHoraRetornoPrevisto", "requisicaoTransporte.dataHoraRetornoPrevisto.validation");
		}

		if(!checkSemPassageiros) {
			if((tiposDePassageiros == null) || (tiposDePassageiros.length == 0)) {
				Validation.addError("tiposDePassageiros", "requisicaoTransporte.tiposDePassageiros.validation");
			}
		}
		requisicaoTransporte.setTiposDePassageiro(converterTiposDePassageiros(tiposDePassageiros));

		if(checkRetorno && requisicaoTransporte.getDataHoraRetornoPrevisto() == null) {
			Validation.addError("dataHoraRetornoPrevisto", "requisicaoTransporte.dataHoraRetornoPrevisto.validation");
		}

		if(!checkSemPassageiros && (requisicaoTransporte.getPassageiros() == null || requisicaoTransporte.getPassageiros().isEmpty())) {
			Validation.addError("passageiros", "requisicaoTransporte.passageiros.validation");
		}

		if(requisicaoTransporte.getTipoFinalidade().ehOutra() && requisicaoTransporte.getFinalidade().isEmpty()) {
			Validation.addError("finalidade", "requisicaoTransporte.finalidade.validation");
		}
		validation.valid(requisicaoTransporte);
		redirecionarSeErroAoSalvar(requisicaoTransporte, checkRetorno, checkSemPassageiros);

		DpPessoa dpPessoa = recuperaPessoa(requisicaoTransporte.getIdSolicitante());
		checarSolicitante(dpPessoa.getIdInicial(),AutorizacaoGIAntigo.recuperarComplexoPadrao().getIdComplexo(),true);

		requisicaoTransporte.setCpOrgaoUsuario(AutorizacaoGIAntigo.titular().getOrgaoUsuario());

		requisicaoTransporte.setCpComplexo(AutorizacaoGIAntigo.recuperarComplexoPadrao());

		requisicaoTransporte.setSequence(requisicaoTransporte.getCpOrgaoUsuario());
		boolean novaRequisicao = false;
		if(requisicaoTransporte.getId() == 0) {
			novaRequisicao  = true;
			requisicaoTransporte.setDataHora(Calendar.getInstance());
		}

		requisicaoTransporte.setSolicitante(recuperaPessoa(requisicaoTransporte.getIdSolicitante()));
		requisicaoTransporte.save();
		requisicaoTransporte.refresh();
		if (novaRequisicao) {
			Andamento andamento = new Andamento();
			andamento.setDescricao("NOVA REQUISICAO");
			andamento.setDataAndamento(Calendar.getInstance());
			andamento.setEstadoRequisicao(EstadoRequisicao.ABERTA);
			andamento.setRequisicaoTransporte(requisicaoTransporte);
			andamento.setResponsavel(AutorizacaoGIAntigo.cadastrante());
			andamento.save();
		}

		listar();
	}

	private static List<TipoDePassageiro> converterTiposDePassageiros(TipoDePassageiro[] tiposDePassageiros) {
		List<TipoDePassageiro> tiposParaSalvar = new ArrayList<TipoDePassageiro>();
		if((tiposDePassageiros == null) || (tiposDePassageiros.length == 0)) {
			tiposParaSalvar.add(TipoDePassageiro.NENHUM);
		} else {
			for (int i = 0; i < tiposDePassageiros.length; i++) {
				tiposParaSalvar.add(tiposDePassageiros[i]);
			}
		}
		return tiposParaSalvar;
	}

	public static void salvarAndamentos(@Valid RequisicaoTransporte requisicaoTransporte, boolean checkRetorno, boolean checkSemPassageiros) throws Exception {
		redirecionarSeErroAoSalvar(requisicaoTransporte, checkRetorno, checkSemPassageiros);
		checarSolicitante(requisicaoTransporte.getSolicitante().getIdInicial(),requisicaoTransporte.getCpComplexo().getIdComplexo(),true);
		requisicaoTransporte.setCpOrgaoUsuario(AutorizacaoGIAntigo.titular().getOrgaoUsuario());
		requisicaoTransporte.save();
		requisicaoTransporte.refresh();
		if (requisicaoTransporte.getId() == 0) {
			Andamento andamento = new Andamento();
			andamento.setDescricao("NOVA REQUISICAO");
			andamento.setDataAndamento(Calendar.getInstance());
			andamento.setEstadoRequisicao(EstadoRequisicao.ABERTA);
			andamento.setRequisicaoTransporte(requisicaoTransporte);
			andamento.setResponsavel(AutorizacaoGIAntigo.cadastrante());
			andamento.save();
		}

		listar();
	}

	private static void redirecionarSeErroAoSalvar(RequisicaoTransporte requisicaoTransporte, boolean checkRetorno, boolean checkSemPassageiros) {
		if(Validation.hasErrors())
		{
			MenuMontador.instance().recuperarMenuRequisicoes(requisicaoTransporte.getId(), false, false);
			String template = requisicaoTransporte.getId() > 0 ? "@editar" : "@incluir";
			carregarTiposDeCarga(requisicaoTransporte);
			carregarFinalidades();
			renderTemplate(template, requisicaoTransporte, checkRetorno, checkSemPassageiros);
		}
	}

	protected static void carregarFinalidades() {
		renderArgs.put("finalidades", FinalidadeRequisicao.listarTodos());
	}

	public static void incluir(){
		RequisicaoTransporte requisicaoTransporte = new RequisicaoTransporte();
		DpPessoa dpPessoa = AutorizacaoGIAntigo.titular();
		requisicaoTransporte.setSolicitante(dpPessoa);
		requisicaoTransporte.setIdSolicitante(dpPessoa.getId());

		carregarTiposDeCarga(requisicaoTransporte);

		carregarFinalidades();

		render(requisicaoTransporte);
	}

	public static void editar(Long id) throws Exception {
		RequisicaoTransporte requisicaoTransporte = RequisicaoTransporte.AR.findById(id);
		checarSolicitante(requisicaoTransporte.getSolicitante().getIdInicial(),requisicaoTransporte.getCpComplexo().getIdComplexo(),true);
		requisicaoTransporte.setIdSolicitante(requisicaoTransporte.getSolicitante().getId());
		//MenuMontador.instance().RecuperarMenuRequisicoes(id, renderArgs);

		carregarTiposDeCarga(requisicaoTransporte);

		carregarFinalidades();

		boolean checkRetorno = (requisicaoTransporte.getDataHoraRetornoPrevisto() == null ? false : true);



		render(requisicaoTransporte, checkRetorno);
	}

	private static void checarSolicitante(
			Long idSolicitante, Long idComplexo, Boolean escrita) throws Exception {
		if (! AutorizacaoGIAntigo.ehAdministrador() && ! AutorizacaoGIAntigo.ehAprovador() && ! AutorizacaoGIAntigo.ehAgente() && ! AutorizacaoGIAntigo.ehAdministradorMissao() && ! AutorizacaoGIAntigo.ehAdministradorMissaoPorComplexo()) {
			if (! AutorizacaoGIAntigo.titular().getIdInicial().equals(idSolicitante)) {
				try {
					throw new Exception(Messages.get("requisicoes.checarSolicitante.exception"));
				} catch (Exception e) {
					AutorizacaoGIAntigo.tratarExcecoes(e);
				}
			}
		} else if ( AutorizacaoGIAntigo.ehAgente()) {
			if (! AutorizacaoGIAntigo.titular().getIdInicial().equals(idSolicitante) && escrita) {
				try {
					throw new Exception(Messages.get("requisicoes.checarSolicitante.exception"));
				} catch (Exception e) {
					AutorizacaoGIAntigo.tratarExcecoes(e);
				}
			}
		} else if (AutorizacaoGIAntigo.ehAdministradorMissaoPorComplexo()) {
			if (! AutorizacaoGIAntigo.getComplexoAdministrado().getIdComplexo().equals(idComplexo) && escrita) {
				try {
					throw new Exception(Messages.get("requisicoes.checarSolicitante.exception"));
				} catch (Exception e) {
					AutorizacaoGIAntigo.tratarExcecoes(e);
				}
			} else if (AutorizacaoGIAntigo.ehAprovador()) {
				if (! AutorizacaoGIAntigo.recuperarComplexoPadrao().getIdComplexo().equals(idComplexo) && escrita) {
					try {
						throw new Exception(Messages.get("requisicoes.checarSolicitante.exception"));
					} catch (Exception e) {
						AutorizacaoGIAntigo.tratarExcecoes(e);
					}
				}
			}
		}
	}

	public static void ler(Long id) throws Exception {
		RequisicaoTransporte requisicaoTransporte = RequisicaoTransporte.AR.findById(id);
		checarSolicitante(requisicaoTransporte.getSolicitante().getIdInicial(), requisicaoTransporte.getCpComplexo().getIdComplexo(),false);
		requisicaoTransporte.setIdSolicitante(requisicaoTransporte.getSolicitante().getId());
		MenuMontador.instance().recuperarMenuRequisicoes(id, false, false);
		carregarTiposDeCarga(requisicaoTransporte);
		carregarFinalidades();

		render(requisicaoTransporte);
	}

	protected static void carregarTiposDeCarga(RequisicaoTransporte req) {
		TipoDePassageiro tipoDePassageiro = TipoDePassageiro.CARGA;
		boolean checkSemPassageiros = false;
		if((req != null) && (req.getTiposDePassageiro() != null)) {
			checkSemPassageiros = (req.getTiposDePassageiro().contains(TipoDePassageiro.NENHUM));
		}
		renderArgs.put("tipoDePassageiro", tipoDePassageiro);
		renderArgs.put("checkSemPassageiros", checkSemPassageiros);
	}

	public static void buscarPelaSequence(String sequence, boolean popUp) throws Exception {
		RequisicaoTransporte req = recuperarPelaSigla(sequence, popUp);

		carregarTiposDeCarga(req);

		carregarFinalidades();

		renderTemplate("@ler");
	}

	protected static RequisicaoTransporte recuperarPelaSigla(String sequence, boolean popUp) throws Exception {
		RequisicaoTransporte requisicaoTransporte = RequisicaoTransporte.buscar(sequence);
		checarSolicitante(requisicaoTransporte.getSolicitante().getIdInicial(),requisicaoTransporte.getCpComplexo().getIdComplexo(),false);
		MenuMontador.instance().recuperarMenuRequisicoes(requisicaoTransporte.getId(), popUp, false);
		requisicaoTransporte.setIdSolicitante(requisicaoTransporte.getSolicitante().getId());

		if(!popUp) {
			MenuMontador.instance().recuperarMenuRequisicoes(requisicaoTransporte.getId(), popUp, false);
		}

		renderArgs.put("requisicaoTransporte", requisicaoTransporte);
		if(requisicaoTransporte.getDataHoraRetornoPrevisto() != null) {
			renderArgs.put("checkRetorno", true);
		}

		return requisicaoTransporte;
	}

	public static void excluir(Long id) throws Exception{
		RequisicaoTransporte requisicaoTransporte = RequisicaoTransporte.AR.findById(id);
		checarSolicitante(requisicaoTransporte.getSolicitante().getIdInicial(),requisicaoTransporte.getCpComplexo().getIdComplexo(),true);
		requisicaoTransporte.setIdSolicitante(requisicaoTransporte.getSolicitante().getId());

		try {
			requisicaoTransporte.excluir(false);
		}
		catch (SigaTpException ex) {
			Validation.addError("requisicaoTransporte", ex.getMessage().toString());
			if(Validation.hasErrors()) {
				MenuMontador.instance().recuperarMenuRequisicoes(requisicaoTransporte.getId(), true, false);
				carregarTiposDeCarga(requisicaoTransporte);
				carregarFinalidades();
				renderTemplate("@ler", requisicaoTransporte);
			}
		}
		catch (Exception ex) {
			throw ex;
		}

		listar();
	}

	private static DpPessoa recuperaPessoa(Long idSolicitante) throws Exception {
		DpPessoa dpPessoa = DpPessoa.AR.findById(idSolicitante);
		return 	DpPessoa.AR.find("idPessoaIni = ? and dataFimPessoa = null",dpPessoa.getIdInicial()).first();
	}
}
