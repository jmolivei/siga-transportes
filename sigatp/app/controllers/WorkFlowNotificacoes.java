package controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.Andamento;
import models.EstadoRequisicao;
import models.Parametro;
import play.Logger;
import play.jobs.Job;
import play.jobs.On;
import play.mvc.Router;
import uteis.FormatarTextoHtml;
import br.gov.jfrj.siga.base.Correio;
import br.gov.jfrj.siga.base.SigaBaseProperties;
import br.gov.jfrj.siga.cp.CpConfiguracao;
import br.gov.jfrj.siga.cp.CpSituacaoConfiguracao;
import br.gov.jfrj.siga.dp.DpPessoa;

@On("cron.iniciow")
public class WorkFlowNotificacoes extends Job<Object>  {
	private static final String espacosHtml = "&nbsp;&nbsp;&nbsp;&nbsp;";
	
	public void doJob() {
		boolean executa = Boolean.parseBoolean(Parametro.buscarConfigSistemaEmVigor("cron.executaw"));
		if (executa) {
			try {
				notificarAndamentos();
			} catch (Exception ex) {
				Logger.info(ex.getMessage());
			}
		}
		else {
			Logger.info("Serviço de Nofitificação do WorkFlow desligado");
		}
		Logger.info("Serviço de Nofitificação do WorkFlow finalizado");
	}
	
	private static void notificarAndamentos() throws Exception  {
		String titulo = "Notifica\u00E7\u00F5es do Andamento de Requisi\u00E7\u00F5es de Transporte";
		List<Andamento> andamentos = Andamento.listarPorDataNotificacaoWorkFlow();
		
		for(Andamento item : andamentos) {
			Set<DpPessoa> lstPessoas = null;
			boolean notificar = false;
		
			if (item.estadoRequisicao.equals(EstadoRequisicao.ABERTA)) {
				lstPessoas = new HashSet<DpPessoa>(retornarConfiguracaoDpPessoa(item));
				notificar = true;
			}
			
			else if(item.estadoRequisicao.equals(EstadoRequisicao.REJEITADA) || item.estadoRequisicao.equals(EstadoRequisicao.AUTORIZADA) ||
				    item.estadoRequisicao.equals(EstadoRequisicao.PROGRAMADA)) {
				    
				lstPessoas = new HashSet<DpPessoa>();
				lstPessoas.add(item.responsavel);
				
				if (!item.responsavel.getId().equals(item.requisicaoTransporte.solicitante.getId())) {
					lstPessoas.add(item.requisicaoTransporte.solicitante);
				}
				notificar = true;
			}

			if (notificar) {
				SimpleDateFormat fr = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				String sequencia = item.estadoRequisicao.getDescricao() + " " +  item.requisicaoTransporte.getSequence() + " " +
						item.requisicaoTransporte.id + " " + fr.format(item.requisicaoTransporte.dataHoraSaidaPrevista.getTime());

				if (enviarEmail(titulo, lstPessoas, sequencia)) {
					try {
						Andamento.gravarDataNotificacaoWorkFlow(item.id);
					}
					catch (Exception ex) {
						Logger.info("Falha ao gravar notificação: " + ex.getMessage());
					}
				}
			}
		}
	}
	
	private static Set<DpPessoa> retornarConfiguracaoDpPessoa(Andamento andamento) {
		List<CpConfiguracao> configuracoes = new ArrayList<CpConfiguracao>(); 
		Set<DpPessoa> setDpPessoa = new HashSet<DpPessoa>();
		List<DpPessoa> lstDpPessoa = new ArrayList<DpPessoa>();
		Object[] parametros = {"SIGA-TP-APR", "SIGA-TP-ADM", CpSituacaoConfiguracao.SITUACAO_PODE, andamento.requisicaoTransporte.cpOrgaoUsuario.getIdOrgaoUsu()};
		
		configuracoes = CpConfiguracao.find("cpServico.siglaServico in (?, ?) and cpSituacaoConfiguracao.idSitConfiguracao = ? and " +
			       							"hisDtFim is null and dpPessoa.orgaoUsuario.idOrgaoUsu = ? ", parametros).fetch();
	
		for (CpConfiguracao cpConfiguracao : configuracoes) {
			if (cpConfiguracao.getDpPessoa() != null) {
				setDpPessoa.add(cpConfiguracao.getDpPessoa());
			} else if (cpConfiguracao.getLotacao() != null) {
				lstDpPessoa = DpPessoa.find("lotacao.idLotacao = ? and dataFimPessoa is null ",	cpConfiguracao.getLotacao().getIdLotacao()).fetch();
				setDpPessoa.addAll(lstDpPessoa);
			}
		}
		
		return setDpPessoa;
	}
	
	private static Boolean enviarEmail(String titulo, Set<?> objeto, String linha) throws Exception {
		String hostName = System.getProperty(Parametro.buscarConfigSistemaEmVigor("caminhoHostnameStandalone"));
		final String finalMensagem = "<b>Mensagem autom&aacute;tica enviada pelo M&oacute;dulo de Transportes do Siga. Favor n&atilde;o responder.</b><br>";
		Boolean emailEnviado = false;
		String[] itensLinha = linha.split(" ");
		List<String> parametros = new ArrayList<String>();

		String conteudoHTML = "<html> <p> A requisi&ccedil;&atilde;o de Transporte" + espacosHtml + itensLinha[1] + 
							  espacosHtml + "com sa&iacute;da solicitada para" + espacosHtml + itensLinha[3] + 
							  espacosHtml + "&agrave;s" + espacosHtml + itensLinha[4];

		if (itensLinha[0].equals(EstadoRequisicao.ABERTA.toString())) {
			conteudoHTML += espacosHtml + "est&aacute; pendente para an&aacute;lise de APROVA&Ccedil;&Atilde;O/REJEI&Ccedil;&Atilde;O." +
						"<br>Para aprovar/rejeitar a requisi&ccedil;&atilde;o, clique em:" + espacosHtml;
			parametros.add("id," + itensLinha[2] + ",Andamentos.autorizar,Autorizar");
			parametros.add("id," + itensLinha[2] + ",Andamentos.rejeitar,Rejeitar");
			
			for (String parametro : parametros) {
				String[] itens = parametro.split(",");
				Map<String,Object> param = new HashMap<String, Object>();
				param.put(itens[0], itens[1]);
				String caminhoUrl = Router.reverse(itens[2],param).url;
				conteudoHTML += "<a href='" + hostName + caminhoUrl + "'>" + itens[3] + "</a>" + (itens[2].contains("autorizar") ? " ou " : "");
			}
		}
		else if (itensLinha[0].equals(EstadoRequisicao.AUTORIZADA.toString()) || itensLinha[0].equals(EstadoRequisicao.REJEITADA.toString()) || 
				itensLinha[0].equals(EstadoRequisicao.PROGRAMADA.toString())) {
			conteudoHTML += espacosHtml + "foi" + espacosHtml + itensLinha[0];
		}

		String remetente = SigaBaseProperties.getString("servidor.smtp.usuario.remetente");
		String email = "";
		
		boolean flagEmail = Boolean.parseBoolean(Parametro.buscarConfigSistemaEmVigor("cron.flagEmailw"));

		if (!flagEmail) {
			for (Object pessoa : objeto) {
				if (objeto.getClass().equals(DpPessoa.class)) {
					email = ((DpPessoa) pessoa).getEmailPessoa() + ",";
				}
			}
			email = email.substring(0, email.length()-1);
		} else {
			email = Parametro.buscarConfigSistemaEmVigor("cron.listaEmailw");
		}
	
		conteudoHTML += ".</p>" + finalMensagem + "</html>";
		String conteudo = FormatarTextoHtml.retirarTagsHtml(conteudoHTML, espacosHtml);
		
		try {
			Correio.enviar(remetente, email.split(","), titulo, conteudo, conteudoHTML);
			emailEnviado = true;
			SimpleDateFormat fr = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			Calendar calendar = Calendar.getInstance();
			Logger.info(fr.format(calendar.getTime()) + " - Email enviado para " + email + ", assunto: " + titulo);		}
		catch(Exception ex) {
			Logger.info("Falha ao enviar email:" + ex.getMessage());
		}

		return emailEnviado;
	}
}