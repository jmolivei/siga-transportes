package br.gov.jfrj.siga.tp.vraptor;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.gov.jfrj.siga.cp.CpConfiguracao;
import br.gov.jfrj.siga.cp.CpServico;
import br.gov.jfrj.siga.cp.CpTipoConfiguracao;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.tp.model.TpDao;
import br.gov.jfrj.siga.vraptor.SigaObjects;

@Resource
@Path("app/configuracaoGI")
public class ConfiguracaoGIController extends TpController {
	
	public ConfiguracaoGIController(HttpServletRequest request, Result result,
			CpDao dao, Validator validator, SigaObjects so, EntityManager em) {
		super(request, result, dao, validator, so, em);
	}

	@Path("/listarPorOrgaoUsuario")
	public void listarPorOrgaoUsuario() {
		pesquisar(getTitular().getOrgaoUsuario().getIdOrgaoUsu());
	}
	
	@Path("/listarPorOrgaoUsuario/{idOrgaoUsu}")
	public void listarPorOrgaoUsuario(Long idOrgaoUsu) {
		pesquisar(idOrgaoUsu);
	}

	private void pesquisar(Long idOrgaoUsu) {
		CpOrgaoUsuario cpOrgaoUsuario = TpDao.findById(CpOrgaoUsuario.class, idOrgaoUsu);
		List<CpOrgaoUsuario> cpOrgaoUsuarios = TpDao.findAll(CpOrgaoUsuario.class);
		String SERVICO_COMPLEXO_ADMINISTRADOR = "SIGA-TP-ADMMISSAOCOMPLEXO";
		CpServico cpServico = TpDao.find(CpServico.class, "siglaServico",SERVICO_COMPLEXO_ADMINISTRADOR).first();
		long TIPO_CONFIG_COMPLEXO_PADRAO = 303;
		
		// TODO  HD martelada!
//		CpTipoConfiguracao tpConf = TpDao.findById(CpTipoConfiguracao.class, TIPO_CONFIG_COMPLEXO_PADRAO);
		CpTipoConfiguracao tpConf = CpTipoConfiguracao.AR.find().first();
		
		
		//	Object[] parametros =  {idOrgaoUsu,cpSituacaoConfiguracaoPode, cpServico};
		//	List<CpConfiguracao> cpConfiguracoesCp =  CpConfiguracao.find("(dpPessoa in (select d from DpPessoa d where d.orgaoUsuario.idOrgaoUsu = ?) and cpSituacaoConfiguracao = ? and cpServico = ? and hisIdcFim is null )", parametros).fetch();
		Object[] parametros =  {idOrgaoUsu, cpServico};
		List<CpConfiguracao> cpConfiguracoesCp =  TpDao.find(CpConfiguracao.class, "(dpPessoa in (select d from DpPessoa d where d.orgaoUsuario.idOrgaoUsu = ?) and cpServico = ? and hisIdcFim is null )", parametros).fetch();
		// Recuperando Configuração Pode para uma lotação específica
		//	Object[] parametros1 =  {cpSituacaoConfiguracaoPode, idOrgaoUsu,tpConf};
		//	List<CpConfiguracao> cpConfiguracoesCl = CpConfiguracao.find("((lotacao is not null  and cpSituacaoConfiguracao = ?) and orgaoUsuario.idOrgaoUsu = ?  and cpTipoConfiguracao = ? and hisIdcFim is null  )", parametros1).fetch();
		Object[] parametros1 =  {idOrgaoUsu,tpConf};
		List<CpConfiguracao> cpConfiguracoesCl = TpDao.find(CpConfiguracao.class, "((lotacao is not null  ) and orgaoUsuario.idOrgaoUsu = ?  and cpTipoConfiguracao = ? and hisIdcFim is null  )", parametros1).fetch();
		// Recuperando Configuração default para um Órgão específico
		Object[] parametros2 =  { idOrgaoUsu,tpConf};
		//	List<CpConfiguracao> cpConfiguracoesCo = CpConfiguracao.find("((cpSituacaoConfiguracao = ?) and orgaoUsuario.idOrgaoUsu = ?  and cpTipoConfiguracao = ? and hisIdcFim is null  )", parametros2).fetch();
		List<CpConfiguracao> cpConfiguracoesCo = TpDao.find(CpConfiguracao.class, "( lotacao is null and orgaoUsuario.idOrgaoUsu = ?  and cpTipoConfiguracao = ? and hisIdcFim is null  )", parametros2).fetch();		
		List<CpConfiguracao> cpConfiguracoes = new ArrayList<CpConfiguracao>();
		cpConfiguracoes.addAll(cpConfiguracoesCp);
		cpConfiguracoes.addAll(cpConfiguracoesCl);
		cpConfiguracoes.addAll(cpConfiguracoesCo);
		
		result.include("cpConfiguracoes", cpConfiguracoes);
		result.include("cpOrgaoUsuario", cpOrgaoUsuario);
		result.include("cpOrgaoUsuarios", cpOrgaoUsuarios);
	}	
	
	@Path("/incluir/{idOrgaoUsu}")
	public void incluir(Long idOrgaoUsu) throws Exception{
		
	}
	
	@Path("/editar/{idOrgaoUsu}")
	public void editar(Long idOrgaoUsu) throws Exception{
		
	}
	
//	public void incluir(Long idOrgaoUsu) throws Exception{
//		CpConfiguracao cpConfiguracao = new CpConfiguracao();
//
//		carregarDadosPerifericos(idOrgaoUsu);
//
//		cpConfiguracao.setOrgaoUsuario((CpOrgaoUsuario) renderArgs.get("cpOrgaoUsuario"));
//
//		/*
//		insert into corporativo.cp_configuracao (
//				id_configuracao, his_id_ini, id_tp_configuracao, id_sit_configuracao, id_servico, 
//				dt_ini_vig_configuracao, his_idc_ini,id_pessoa, id_complexo
//			) values(
//				corporativo.cp_configuracao_seq.nextval, 
//				corporativo.cp_configuracao_seq.currval,
//				200, 
//				1, 
//				(select id_servico from corporativo.cp_servico where sigla_servico='SIGA-TP-ADMMISSAOCOMPLEXO'),
//				sysdate,
//				13332,
//				(select id_pessoa from corporativo.dp_pessoa where matricula = '10596' and data_fim_pessoa is null and id_orgao_usu = 1),
//				1
//			); */
//		cpConfiguracao.setId(new Long(0L));
//		render(cpConfiguracao);
//	}
//
//	private static void carregarDadosPerifericos(Long idOrgaoUsu) {
//		CpOrgaoUsuario cpOrgaoUsuario = TpDao.findById(CpOrgaoUsuario.class,idOrgaoUsu);
//		long TIPO_CONFIG_COMPLEXO_PADRAO = 400;
//		CpTipoConfiguracao tpConf1 = TpDao.findById(CpTipoConfiguracao.class,TIPO_CONFIG_COMPLEXO_PADRAO);
//		long TIPO_CONFIG_UTILIZAR_SERVICO = 200;
//		CpTipoConfiguracao tpConf2 = TpDao.findById(CpTipoConfiguracao.class,TIPO_CONFIG_UTILIZAR_SERVICO);		
//
//
//		List<CpTipoConfiguracao> cpTiposConfiguracao = new ArrayList<CpTipoConfiguracao>();	
//		cpTiposConfiguracao.add(tpConf2);
//		cpTiposConfiguracao.add(tpConf1);
//
//		List<CpSituacaoConfiguracao> cpSituacoesConfiguracao = TpDao.findAll(CpSituacaoConfiguracao.class);
//
//		List<CpComplexo> cpComplexos = TpDao.find(CpComplexo.class, "orgaoUsuario = ?", cpOrgaoUsuario).fetch();
//
//		renderArgs.put("cpOrgaoUsuario",cpOrgaoUsuario);
//		renderArgs.put("cpTiposConfiguracao",cpTiposConfiguracao);
//		renderArgs.put("cpSituacoesConfiguracao",cpSituacoesConfiguracao);
//		renderArgs.put("cpComplexos",cpComplexos);
//	}
//
//	public static void editar(Long id) throws Exception{
//		CpConfiguracao cpConfiguracao = TpDao.findById(CpConfiguracao.class,id);
//
//		if (cpConfiguracao.getOrgaoUsuario() != null ) {
//			carregarDadosPerifericos(cpConfiguracao.getOrgaoUsuario().getIdOrgaoUsu());
//		} else if  (cpConfiguracao.getDpPessoa() != null) {
//			carregarDadosPerifericos(cpConfiguracao.getDpPessoa().getLotacao().getOrgaoUsuario().getIdOrgaoUsu());
//			cpConfiguracao.setOrgaoUsuario(cpConfiguracao.getDpPessoa().getLotacao().getOrgaoUsuario());
//		} else if (cpConfiguracao.getLotacao() != null) {
//			
//			carregarDadosPerifericos(cpConfiguracao.getLotacao().getOrgaoUsuario().getIdOrgaoUsu());
//			cpConfiguracao.setOrgaoUsuario(cpConfiguracao.getLotacao().getOrgaoUsuario());
//		}
//
//		render(cpConfiguracao);
//	}
//	
//	public static void excluir(Long id) throws Exception{
//		CpConfiguracao cpConfiguracao = TpDao.findById(CpConfiguracao.class,id);
//
//		cpConfiguracao.delete();
//
//		if (cpConfiguracao.getOrgaoUsuario() != null ) {
//			pesquisar(cpConfiguracao.getOrgaoUsuario().getIdOrgaoUsu());
//		} else if (cpConfiguracao.getDpPessoa() != null) {
//			pesquisar(cpConfiguracao.getDpPessoa().getLotacao().getOrgaoUsuario().getIdOrgaoUsu());
//		} else if (cpConfiguracao.getLotacao() != null) {
//			pesquisar(cpConfiguracao.getLotacao().getOrgaoUsuario().getIdOrgaoUsu());
//		}
//	}
//
//	public static void salvar(@Valid CpConfiguracao cpConfiguracao) throws Exception{
//
//		if(Validation.hasErrors()) 
//		{
//			carregarDadosPerifericos(cpConfiguracao.getOrgaoUsuario().getIdOrgaoUsu());
//			render(cpConfiguracao);
//		}
//		
//		CpConfiguracao cpConfiguracaoNova = new CpConfiguracao();
//		CpConfiguracao cpConfiguracaoAnterior = TpDao.findById(CpConfiguracao.class,cpConfiguracao.getId());
//		if (cpConfiguracaoAnterior != null) {
//			if (cpConfiguracaoAnterior.getConfiguracaoInicial() == null) {
//				cpConfiguracaoAnterior.setConfiguracaoInicial(cpConfiguracaoAnterior);
//				cpConfiguracaoAnterior.save();
//			}
//			cpConfiguracaoAnterior.setHisDtFim(new Date());	
//			cpConfiguracaoAnterior.setHisIdcFim(AutorizacaoGIAntigo.idc());
//			cpConfiguracaoAnterior.save();		
//
//			cpConfiguracaoNova.setConfiguracaoInicial(cpConfiguracaoAnterior.getConfiguracaoInicial());
//		}
//
//		if (cpConfiguracao.getCpTipoConfiguracao().getIdTpConfiguracao() == 200) {
//			String SERVICO_COMPLEXO_ADMINISTRADOR = "SIGA-TP-ADMMISSAOCOMPLEXO";
//			cpConfiguracaoNova.setCpServico((CpServico) TpDao.find(CpServico.class, "siglaServico",SERVICO_COMPLEXO_ADMINISTRADOR).first());
//		}	
//
//		cpConfiguracaoNova.setCpSituacaoConfiguracao(cpConfiguracao.getCpSituacaoConfiguracao());
//		cpConfiguracaoNova.setCpTipoConfiguracao(cpConfiguracao.getCpTipoConfiguracao());
//		cpConfiguracaoNova.setComplexo(cpConfiguracao.getComplexo());
//		cpConfiguracaoNova.setOrgaoUsuario(cpConfiguracao.getOrgaoUsuario());
//		cpConfiguracaoNova.setLotacao(cpConfiguracao.getLotacao());	
//		cpConfiguracaoNova.setDpPessoa(cpConfiguracao.getDpPessoa());			
//		cpConfiguracaoNova.setHisDtIni(new Date());	
//		cpConfiguracaoNova.setHisIdcIni(AutorizacaoGIAntigo.idc());
//		cpConfiguracaoNova.save();
//		if (cpConfiguracaoNova.getConfiguracaoInicial() == null) {
//			cpConfiguracaoNova.setConfiguracaoInicial(cpConfiguracaoNova);
//			cpConfiguracaoNova.save();
//		}			
//
//		if (cpConfiguracaoNova.getOrgaoUsuario() != null ) {
//			pesquisar(cpConfiguracaoNova.getOrgaoUsuario().getIdOrgaoUsu());
//		} else if (cpConfiguracaoNova.getDpPessoa() != null) {
//			pesquisar(cpConfiguracaoNova	.getDpPessoa().getLotacao().getOrgaoUsuario().getIdOrgaoUsu());
//		} else if (cpConfiguracaoNova.getLotacao() != null) {
//			pesquisar(cpConfiguracaoNova.getLotacao().getOrgaoUsuario().getIdOrgaoUsu());
//		}
//
//	}
}
