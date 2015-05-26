package br.gov.jfrj.siga.tp.vraptor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.view.Results;
import br.gov.jfrj.siga.cp.CpComplexo;
import br.gov.jfrj.siga.cp.CpConfiguracao;
import br.gov.jfrj.siga.cp.CpServico;
import br.gov.jfrj.siga.cp.CpSituacaoConfiguracao;
import br.gov.jfrj.siga.cp.CpTipoConfiguracao;
import br.gov.jfrj.siga.cp.model.DpLotacaoSelecao;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.vraptor.SigaObjects;

@Resource
@Path("app/configuracaoGI")
public class ConfiguracaoGIController extends TpController {
	
	public ConfiguracaoGIController(HttpServletRequest request, Result result,
			CpDao dao, Validator validator, SigaObjects so, EntityManager em) {
		super(request, result, dao, validator, so, em);
	}

	@Path("/listarPorOrgaoUsuario")
	public void listarPorOrgaoUsuario() throws Exception {
		result.redirectTo(ConfiguracaoGIController.class).listarPorOrgaoUsuario(getTitular().getOrgaoUsuario().getIdOrgaoUsu());
	}
	
	@Path("/listarPorOrgaoUsuario/{idOrgaoUsu}")
	public void listarPorOrgaoUsuario(Long idOrgaoUsu) throws Exception {
		pesquisar(idOrgaoUsu);
	}

	@SuppressWarnings("unchecked")
	private void pesquisar(Long idOrgaoUsu) throws Exception {
		CpOrgaoUsuario cpOrgaoUsuario = CpOrgaoUsuario.AR.findById(idOrgaoUsu);
		List<CpOrgaoUsuario> cpOrgaoUsuarios = CpOrgaoUsuario.AR.findAll();
		String SERVICO_COMPLEXO_ADMINISTRADOR = "SIGA-TP-ADMMISSAOCOMPLEXO";
		CpServico cpServico = CpServico.AR.find("siglaServico",SERVICO_COMPLEXO_ADMINISTRADOR).first();
		//TODO  HD martelada! #400
		Long TIPO_CONFIG_COMPLEXO_PADRAO = 300L;
		CpTipoConfiguracao tpConf = CpTipoConfiguracao.AR.findById(TIPO_CONFIG_COMPLEXO_PADRAO);
		//	Object[] parametros =  {idOrgaoUsu,cpSituacaoConfiguracaoPode, cpServico};
		//	List<CpConfiguracao> cpConfiguracoesCp =  CpConfiguracao.find("(dpPessoa in (select d from DpPessoa d where d.orgaoUsuario.idOrgaoUsu = ?) and cpSituacaoConfiguracao = ? and cpServico = ? and hisIdcFim is null )", parametros).fetch();
		Object[] parametros =  {idOrgaoUsu, cpServico};
		List<CpConfiguracao> cpConfiguracoesCp =  CpConfiguracao.AR.find("(dpPessoa in (select d from DpPessoa d where d.orgaoUsuario.idOrgaoUsu = ?) and cpServico = ? and hisIdcFim is null )", parametros).fetch();
		// Recuperando Configuração Pode para uma lotação específica
		//	Object[] parametros1 =  {cpSituacaoConfiguracaoPode, idOrgaoUsu,tpConf};
		//	List<CpConfiguracao> cpConfiguracoesCl = CpConfiguracao.find("((lotacao is not null  and cpSituacaoConfiguracao = ?) and orgaoUsuario.idOrgaoUsu = ?  and cpTipoConfiguracao = ? and hisIdcFim is null  )", parametros1).fetch();
		Object[] parametros1 =  {idOrgaoUsu,tpConf};
		List<CpConfiguracao> cpConfiguracoesCl = CpConfiguracao.AR.find("((lotacao is not null) and orgaoUsuario.idOrgaoUsu = ?  and cpTipoConfiguracao = ? and hisIdcFim is null  )", parametros1).fetch();
		// Recuperando Configuração default para um Órgão específico
		Object[] parametros2 =  { idOrgaoUsu,tpConf};
		//	List<CpConfiguracao> cpConfiguracoesCo = CpConfiguracao.find("((cpSituacaoConfiguracao = ?) and orgaoUsuario.idOrgaoUsu = ?  and cpTipoConfiguracao = ? and hisIdcFim is null  )", parametros2).fetch();
		List<CpConfiguracao> cpConfiguracoesCo = CpConfiguracao.AR.find("( lotacao is null and orgaoUsuario.idOrgaoUsu = ?  and cpTipoConfiguracao = ? and hisIdcFim is null )", parametros2).fetch();		
		List<CpConfiguracao> cpConfiguracoes = new ArrayList<CpConfiguracao>();
		cpConfiguracoes.addAll(cpConfiguracoesCp);
		cpConfiguracoes.addAll(cpConfiguracoesCl);
		cpConfiguracoes.addAll(cpConfiguracoesCo);
		
		result.include("cpConfiguracoes", cpConfiguracoes);
		result.include("cpOrgaoUsuario", cpOrgaoUsuario);
		result.include("cpOrgaoUsuarios", cpOrgaoUsuarios);
		
		result.include("lotacaoSel", new DpLotacaoSelecao());
	}
	
	@Path("/incluir/{idOrgaoUsu}")
	public void incluir(Long idOrgaoUsu) throws Exception{
		CpConfiguracao cpConfiguracao = new CpConfiguracao();

		carregarDadosPerifericos(idOrgaoUsu);

		cpConfiguracao.setOrgaoUsuario((CpOrgaoUsuario) result.included().get("cpOrgaoUsuario"));

		/*
		insert into corporativo.cp_configuracao (
				id_configuracao, his_id_ini, id_tp_configuracao, id_sit_configuracao, id_servico, 
				dt_ini_vig_configuracao, his_idc_ini,id_pessoa, id_complexo
			) values(
				corporativo.cp_configuracao_seq.nextval, 
				corporativo.cp_configuracao_seq.currval,
				200, 
				1, 
				(select id_servico from corporativo.cp_servico where sigla_servico='SIGA-TP-ADMMISSAOCOMPLEXO'),
				sysdate,
				13332,
				(select id_pessoa from corporativo.dp_pessoa where matricula = '10596' and data_fim_pessoa is null and id_orgao_usu = 1),
				1
			); */
		cpConfiguracao.setId(new Long(0L));
		result.include("cpConfiguracao", cpConfiguracao);
	}
	
	@Path("/editar/{id}")
	public void editar(Long id) throws Exception{
		CpConfiguracao cpConfiguracao = CpConfiguracao.AR.findById(id);

		if (cpConfiguracao.getOrgaoUsuario() != null )
			carregarDadosPerifericos(cpConfiguracao.getOrgaoUsuario().getIdOrgaoUsu());
		else if (cpConfiguracao.getDpPessoa() != null) {
			carregarDadosPerifericos(cpConfiguracao.getDpPessoa().getLotacao().getOrgaoUsuario().getIdOrgaoUsu());
			cpConfiguracao.setOrgaoUsuario(cpConfiguracao.getDpPessoa().getLotacao().getOrgaoUsuario());
		} else if (cpConfiguracao.getLotacao() != null) {
			carregarDadosPerifericos(cpConfiguracao.getLotacao().getOrgaoUsuario().getIdOrgaoUsu());
			cpConfiguracao.setOrgaoUsuario(cpConfiguracao.getLotacao().getOrgaoUsuario());
		}

		result.include("cpConfiguracao", cpConfiguracao);
	}
	
	@SuppressWarnings("unchecked")
	private void carregarDadosPerifericos(Long idOrgaoUsu) throws Exception {
		CpOrgaoUsuario cpOrgaoUsuario = CpOrgaoUsuario.AR.findById(idOrgaoUsu);
		long TIPO_CONFIG_COMPLEXO_PADRAO = 400;
		CpTipoConfiguracao tpConf1 = CpTipoConfiguracao.AR.findById(TIPO_CONFIG_COMPLEXO_PADRAO);
		long TIPO_CONFIG_UTILIZAR_SERVICO = 200;
		CpTipoConfiguracao tpConf2 = CpTipoConfiguracao.AR.findById(TIPO_CONFIG_UTILIZAR_SERVICO);		

		List<CpTipoConfiguracao> cpTiposConfiguracao = new ArrayList<CpTipoConfiguracao>();	
		cpTiposConfiguracao.add(tpConf2);
		cpTiposConfiguracao.add(tpConf1);

		List<CpSituacaoConfiguracao> cpSituacoesConfiguracao = CpSituacaoConfiguracao.AR.findAll();
		List<CpComplexo> cpComplexos = CpComplexo.AR.find(" orgaoUsuario.idOrgaoUsu = ? ", idOrgaoUsu).fetch();

		result.include("cpOrgaoUsuario",cpOrgaoUsuario);
		result.include("cpTiposConfiguracao",cpTiposConfiguracao);
		result.include("cpSituacoesConfiguracao",cpSituacoesConfiguracao);
		result.include("cpComplexos",cpComplexos);
	}
	
	@Path("/excluir/{id}")
	public void excluir(Long id) throws Exception{
		CpConfiguracao cpConfiguracao = CpConfiguracao.AR.findById(id);

		cpConfiguracao.delete();

		redirecionaParaListagem(cpConfiguracao);
	}
	
	public void salvar(@Valid CpConfiguracao cpConfiguracao) throws Exception {
		if(validator.hasErrors()) {
			carregarDadosPerifericos(cpConfiguracao.getOrgaoUsuario().getIdOrgaoUsu());
			//TODO  HD listagem ou edição?
			validator.onErrorUse(Results.page()).of(ConfiguracaoGIController.class).editar(cpConfiguracao.getOrgaoUsuario().getIdOrgaoUsu());
		}
		CpConfiguracao cpConfiguracaoNova = new CpConfiguracao();
		CpConfiguracao cpConfiguracaoAnterior = CpConfiguracao.AR.findById(cpConfiguracao.getId());
		if (cpConfiguracaoAnterior != null) {
			if (cpConfiguracaoAnterior.getConfiguracaoInicial() == null) {
				cpConfiguracaoAnterior.setConfiguracaoInicial(cpConfiguracaoAnterior);
				cpConfiguracaoAnterior.save();
			}
			cpConfiguracaoAnterior.setHisDtFim(new Date());	
			cpConfiguracaoAnterior.setHisIdcFim(getIdentidadeCadastrante());
			cpConfiguracaoAnterior.save();		

			cpConfiguracaoNova.setConfiguracaoInicial(cpConfiguracaoAnterior.getConfiguracaoInicial());
		}

		if (cpConfiguracao.getCpTipoConfiguracao().getIdTpConfiguracao() == 200) {
			String SERVICO_COMPLEXO_ADMINISTRADOR = "SIGA-TP-ADMMISSAOCOMPLEXO";
			cpConfiguracaoNova.setCpServico((CpServico) CpServico.AR.find("siglaServico",SERVICO_COMPLEXO_ADMINISTRADOR).first());
		}	

		cpConfiguracaoNova.setCpSituacaoConfiguracao(cpConfiguracao.getCpSituacaoConfiguracao());
		cpConfiguracaoNova.setCpTipoConfiguracao(cpConfiguracao.getCpTipoConfiguracao());
		cpConfiguracaoNova.setComplexo(cpConfiguracao.getComplexo());
		cpConfiguracaoNova.setOrgaoUsuario(cpConfiguracao.getOrgaoUsuario());
		cpConfiguracaoNova.setLotacao(cpConfiguracao.getLotacao());	
		cpConfiguracaoNova.setDpPessoa(cpConfiguracao.getDpPessoa());			
		cpConfiguracaoNova.setHisDtIni(new Date());	
		cpConfiguracaoNova.setHisIdcIni(getIdentidadeCadastrante());
		cpConfiguracaoNova.save();
		if (cpConfiguracaoNova.getConfiguracaoInicial() == null) {
			cpConfiguracaoNova.setConfiguracaoInicial(cpConfiguracaoNova);
			cpConfiguracaoNova.save();
		}			

		redirecionaParaListagem(cpConfiguracaoNova);
	}

	private void redirecionaParaListagem(CpConfiguracao cpConfiguracao) throws Exception {
		if (cpConfiguracao.getOrgaoUsuario() != null ) 
			result.redirectTo(this).listarPorOrgaoUsuario(cpConfiguracao.getOrgaoUsuario().getIdOrgaoUsu());
		else if (cpConfiguracao.getDpPessoa() != null) 
			result.redirectTo(this).listarPorOrgaoUsuario(cpConfiguracao.getDpPessoa().getLotacao().getOrgaoUsuario().getIdOrgaoUsu());
		else if (cpConfiguracao.getLotacao() != null) 
			result.redirectTo(this).listarPorOrgaoUsuario(cpConfiguracao.getLotacao().getOrgaoUsuario().getIdOrgaoUsu());
	}
	
}
