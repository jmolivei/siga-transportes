package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import play.data.validation.Valid;
import play.data.validation.Validation;
import play.mvc.Controller;
import play.mvc.With;
import br.gov.jfrj.siga.cp.CpComplexo;
import br.gov.jfrj.siga.cp.CpConfiguracao;
import br.gov.jfrj.siga.cp.CpServico;
import br.gov.jfrj.siga.cp.CpSituacaoConfiguracao;
import br.gov.jfrj.siga.cp.CpTipoConfiguracao;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;

@With(AutorizacaoGI.class)
public class ConfiguracoesGI extends Controller {

	public static void listar() {
		pesquisar(AutorizacaoGI.titular().getOrgaoUsuario().getIdOrgaoUsu());
	}

	public static void pesquisar(Long idOrgaoUsu) {
		CpOrgaoUsuario cpOrgaoUsuario = CpOrgaoUsuario.findById(idOrgaoUsu);
		List<CpOrgaoUsuario> cpOrgaoUsuarios = CpOrgaoUsuario.findAll();
		String SERVICO_COMPLEXO_ADMINISTRADOR = "SIGA-TP-ADMMISSAOCOMPLEXO";
		CpServico cpServico = CpServico.find("siglaServico",SERVICO_COMPLEXO_ADMINISTRADOR).first();
		long TIPO_CONFIG_COMPLEXO_PADRAO = 400;
		CpTipoConfiguracao tpConf = CpTipoConfiguracao.findById(TIPO_CONFIG_COMPLEXO_PADRAO);
		CpSituacaoConfiguracao cpSituacaoConfiguracaoPode = CpSituacaoConfiguracao.findById(1L);
		CpSituacaoConfiguracao cpSituacaoConfiguracaoPadrao = CpSituacaoConfiguracao.findById(5L);
		//	Object[] parametros =  {idOrgaoUsu,cpSituacaoConfiguracaoPode, cpServico};
		//	List<CpConfiguracao> cpConfiguracoesCp =  CpConfiguracao.find("(dpPessoa in (select d from DpPessoa d where d.orgaoUsuario.idOrgaoUsu = ?) and cpSituacaoConfiguracao = ? and cpServico = ? and hisIdcFim is null )", parametros).fetch();
		Object[] parametros =  {idOrgaoUsu, cpServico};
		List<CpConfiguracao> cpConfiguracoesCp =  CpConfiguracao.find("(dpPessoa in (select d from DpPessoa d where d.orgaoUsuario.idOrgaoUsu = ?) and cpServico = ? and hisIdcFim is null )", parametros).fetch();
		// Recuperando Configura��o Pode para uma lota��o espec�fica
		//	Object[] parametros1 =  {cpSituacaoConfiguracaoPode, idOrgaoUsu,tpConf};
		//	List<CpConfiguracao> cpConfiguracoesCl = CpConfiguracao.find("((lotacao is not null  and cpSituacaoConfiguracao = ?) and orgaoUsuario.idOrgaoUsu = ?  and cpTipoConfiguracao = ? and hisIdcFim is null  )", parametros1).fetch();
		Object[] parametros1 =  {idOrgaoUsu,tpConf};
		List<CpConfiguracao> cpConfiguracoesCl = CpConfiguracao.find("((lotacao is not null  ) and orgaoUsuario.idOrgaoUsu = ?  and cpTipoConfiguracao = ? and hisIdcFim is null  )", parametros1).fetch();
		// Recuperando Configura��o default para um �rg�o espec�fico
		Object[] parametros2 =  { idOrgaoUsu,tpConf};
		//	List<CpConfiguracao> cpConfiguracoesCo = CpConfiguracao.find("((cpSituacaoConfiguracao = ?) and orgaoUsuario.idOrgaoUsu = ?  and cpTipoConfiguracao = ? and hisIdcFim is null  )", parametros2).fetch();
		List<CpConfiguracao> cpConfiguracoesCo = CpConfiguracao.find("( lotacao is null and orgaoUsuario.idOrgaoUsu = ?  and cpTipoConfiguracao = ? and hisIdcFim is null  )", parametros2).fetch();		
		List<CpConfiguracao> cpConfiguracoes = new ArrayList<CpConfiguracao>();
		cpConfiguracoes.addAll(cpConfiguracoesCp);
		cpConfiguracoes.addAll(cpConfiguracoesCl);
		cpConfiguracoes.addAll(cpConfiguracoesCo);
		renderTemplate("@listar",cpConfiguracoes,cpOrgaoUsuario,cpOrgaoUsuarios);
	}	


	public static void incluir(Long idOrgaoUsu) throws Exception{
		CpConfiguracao cpConfiguracao = new CpConfiguracao();

		carregarDadosPerifericos(idOrgaoUsu);

		cpConfiguracao.setOrgaoUsuario((CpOrgaoUsuario) renderArgs.get("cpOrgaoUsuario"));

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
		render(cpConfiguracao);
	}

	private static void carregarDadosPerifericos(Long idOrgaoUsu) {
		CpOrgaoUsuario cpOrgaoUsuario = CpOrgaoUsuario.findById(idOrgaoUsu);
		long TIPO_CONFIG_COMPLEXO_PADRAO = 400;
		CpTipoConfiguracao tpConf1 = CpTipoConfiguracao.findById(TIPO_CONFIG_COMPLEXO_PADRAO);
		long TIPO_CONFIG_UTILIZAR_SERVICO = 200;
		CpTipoConfiguracao tpConf2 = CpTipoConfiguracao.findById(TIPO_CONFIG_UTILIZAR_SERVICO);		


		List<CpTipoConfiguracao> cpTiposConfiguracao = new ArrayList<CpTipoConfiguracao>();	
		cpTiposConfiguracao.add(tpConf2);
		cpTiposConfiguracao.add(tpConf1);

		List<CpSituacaoConfiguracao> cpSituacoesConfiguracao = CpSituacaoConfiguracao.findAll();

		List<CpComplexo> cpComplexos = CpComplexo.find("orgaoUsuario = ?", cpOrgaoUsuario).fetch();

		renderArgs.put("cpOrgaoUsuario",cpOrgaoUsuario);
		renderArgs.put("cpTiposConfiguracao",cpTiposConfiguracao);
		renderArgs.put("cpSituacoesConfiguracao",cpSituacoesConfiguracao);
		renderArgs.put("cpComplexos",cpComplexos);
	}

	public static void editar(Long id) throws Exception{
		CpConfiguracao cpConfiguracao = CpConfiguracao.findById(id);

		if (cpConfiguracao.getOrgaoUsuario() != null ) {
			carregarDadosPerifericos(cpConfiguracao.getOrgaoUsuario().getIdOrgaoUsu());
		} else if  (cpConfiguracao.getDpPessoa() != null) {
			carregarDadosPerifericos(cpConfiguracao.getDpPessoa().getLotacao().getOrgaoUsuario().getIdOrgaoUsu());
			cpConfiguracao.setOrgaoUsuario(cpConfiguracao.getDpPessoa().getLotacao().getOrgaoUsuario());
		} else if (cpConfiguracao.getLotacao() != null) {
			
			carregarDadosPerifericos(cpConfiguracao.getLotacao().getOrgaoUsuario().getIdOrgaoUsu());
			cpConfiguracao.setOrgaoUsuario(cpConfiguracao.getLotacao().getOrgaoUsuario());
		}

		render(cpConfiguracao);
	}
	
	public static void excluir(Long id) throws Exception{
		CpConfiguracao cpConfiguracao = CpConfiguracao.findById(id);

		cpConfiguracao.delete();

		if (cpConfiguracao.getOrgaoUsuario() != null ) {
			pesquisar(cpConfiguracao.getOrgaoUsuario().getIdOrgaoUsu());
		} else if (cpConfiguracao.getDpPessoa() != null) {
			pesquisar(cpConfiguracao.getDpPessoa().getLotacao().getOrgaoUsuario().getIdOrgaoUsu());
		} else if (cpConfiguracao.getLotacao() != null) {
			pesquisar(cpConfiguracao.getLotacao().getOrgaoUsuario().getIdOrgaoUsu());
		}
	}

	public static void salvar(@Valid CpConfiguracao cpConfiguracao) throws Exception{

		if(Validation.hasErrors()) 
		{
			carregarDadosPerifericos(cpConfiguracao.getOrgaoUsuario().getIdOrgaoUsu());
			render(cpConfiguracao);
		}
		
		CpConfiguracao cpConfiguracaoNova = new CpConfiguracao();
		CpConfiguracao cpConfiguracaoAnterior = CpConfiguracao.findById(cpConfiguracao.getId());
		if (cpConfiguracaoAnterior != null) {
			if (cpConfiguracaoAnterior.getConfiguracaoInicial() == null) {
				cpConfiguracaoAnterior.setConfiguracaoInicial(cpConfiguracaoAnterior);
				cpConfiguracaoAnterior.save();
			}
			cpConfiguracaoAnterior.setHisDtFim(new Date());	
			cpConfiguracaoAnterior.setHisIdcFim(AutorizacaoGI.idc());
			cpConfiguracaoAnterior.save();		

			cpConfiguracaoNova.setConfiguracaoInicial(cpConfiguracaoAnterior.getConfiguracaoInicial());
		}

		if (cpConfiguracao.getCpTipoConfiguracao().getIdTpConfiguracao() == 200) {
			String SERVICO_COMPLEXO_ADMINISTRADOR = "SIGA-TP-ADMMISSAOCOMPLEXO";
			cpConfiguracaoNova.setCpServico((CpServico) CpServico.find("siglaServico",SERVICO_COMPLEXO_ADMINISTRADOR).first());
		}	

		cpConfiguracaoNova.setCpSituacaoConfiguracao(cpConfiguracao.getCpSituacaoConfiguracao());
		cpConfiguracaoNova.setCpTipoConfiguracao(cpConfiguracao.getCpTipoConfiguracao());
		cpConfiguracaoNova.setComplexo(cpConfiguracao.getComplexo());
		cpConfiguracaoNova.setOrgaoUsuario(cpConfiguracao.getOrgaoUsuario());
		cpConfiguracaoNova.setLotacao(cpConfiguracao.getLotacao());	
		cpConfiguracaoNova.setDpPessoa(cpConfiguracao.getDpPessoa());			
		cpConfiguracaoNova.setHisDtIni(new Date());	
		cpConfiguracaoNova.setHisIdcIni(AutorizacaoGI.idc());
		cpConfiguracaoNova.save();
		if (cpConfiguracaoNova.getConfiguracaoInicial() == null) {
			cpConfiguracaoNova.setConfiguracaoInicial(cpConfiguracaoNova);
			cpConfiguracaoNova.save();
		}			

		if (cpConfiguracaoNova.getOrgaoUsuario() != null ) {
			pesquisar(cpConfiguracaoNova.getOrgaoUsuario().getIdOrgaoUsu());
		} else if (cpConfiguracaoNova.getDpPessoa() != null) {
			pesquisar(cpConfiguracaoNova	.getDpPessoa().getLotacao().getOrgaoUsuario().getIdOrgaoUsu());
		} else if (cpConfiguracaoNova.getLotacao() != null) {
			pesquisar(cpConfiguracaoNova.getLotacao().getOrgaoUsuario().getIdOrgaoUsu());
		}

	}

}