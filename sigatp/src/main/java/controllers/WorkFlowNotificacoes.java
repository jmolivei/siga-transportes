package controllers;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import play.Logger;
import play.Play;
import play.jobs.Job;
import play.jobs.On;
import play.mvc.Router;
import br.gov.jfrj.siga.base.Correio;
import br.gov.jfrj.siga.base.SigaBaseProperties;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.tp.model.Andamento;
import br.gov.jfrj.siga.tp.model.Condutor;
import br.gov.jfrj.siga.tp.util.FormataCaminhoDoContextoUrl;

//@Every("1mn")
@On("cron.iniciow")
public class WorkFlowNotificacoes extends Job<Object>  {
	private static final String espacosHtml = "&nbsp;&nbsp;&nbsp;&nbsp;";
	
	public void doJob() {
		String executa = Play.configuration.getProperty("cron.executaw").toString();
		if (executa.toUpperCase().equals("TRUE")) {
			verificarAndamentoDaRequisicao();
		}
		else {
			Logger.info("Serviço de Nofitificação do WorkFlow desligado");
		}
		Logger.info("Serviço de Nofitificação do WorkFlow finalizado");
	}
	
	private void verificarAndamentoDaRequisicao() {
		List<Andamento> andamentos = new ArrayList<Andamento>();
		String tituloEmail = "Notificacoes do andamento de requisições para o WorkFlow do SIGA-DOC";
		String tipoNotificacao = "notificadas ao SIGA-DOC";
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_YEAR, -7);
			andamentos = Andamento.find("dataNotificacaoWorkFlow IS NULL and requisicaoTransporte in (select r from RequisicaoTransporte where origemExterna = true").fetch();
			notificarAndamentos(andamentos, tituloEmail, tipoNotificacao);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}


	private static void notificarAndamentos(List<Andamento> andamentos, String titulo, String notificacao) throws Exception  {
		Condutor condutor = new Condutor();
		HashMap<Condutor, String> dadosCondutor = new HashMap<Condutor, String>();
		
		for(Andamento item : andamentos) {
			String sequencia = item.requisicaoTransporte.getSequence() + " " + item.requisicaoTransporte.id + ",";

			if (dadosCondutor.containsKey(condutor)) {
				dadosCondutor.put(condutor, dadosCondutor.get(condutor) + sequencia);
			}
			else {
				dadosCondutor.put(condutor, sequencia);
			}
		}
		
		if (dadosCondutor.size() > 0) {
			enviarEmail(titulo, notificacao, dadosCondutor);
		}
	}

	public static String substituirMarcacoesMensagem(String titulo, String notificacao, String lista, Object pessoa) {
		String sexo = "";
		String nome = "";
		String parteMensagem = "";
		Boolean plural = lista.split(",").length > 1 ? true : false;
		String mensagem; 
				
		if (pessoa.getClass().equals(Condutor.class)) {
			sexo = ((Condutor)pessoa).getDpPessoa().getSexo().toUpperCase();
			nome = ((Condutor)pessoa).getNome();
			
			if (titulo.contains("Missoes")) {
				parteMensagem = plural ? "as miss&otilde;es " : "a miss&atilde;o "; 

				if (notificacao.contains("Nao finalizada")) {
					parteMensagem += "abaixo, caso j&aacute; tenha/m sido realizada/s, " + 
								    "precisa/m ser finalizada/s.<br>";
					
				}
				else if (notificacao.contains("Nao iniciada")) {
					parteMensagem += "abaixo precisa/m ser iniciada/s ou cancelada/s.<br>";
				}
			}
		}
		else if(pessoa.getClass().equals(DpPessoa.class)) {
			sexo = ((DpPessoa)pessoa).getSexo().toUpperCase();
			nome = ((DpPessoa)pessoa).getNomePessoa();
			
			if (titulo.contains("Requisicoes")) {
				parteMensagem = plural ? "as requisi&ccedil;&otilde;es " : "a requisi&ccedil;&atilde;o ";

				if (notificacao.contains("Pendente aprovar")) {
					parteMensagem += "abaixo precisa/m ser autorizada/s ou rejeitada/s.<br>";
				}
			}
		}
		
		mensagem = sexo.equals("F") ? "Prezada Sra. " : "Prezado Sr. " + nome + ", ";
		mensagem += parteMensagem.replaceAll("/s", plural ? "s" : "").replaceAll("/m", plural ? "m" : "");
		return mensagem;
	}
	
	private static String retirarTagsHtml(String conteudo) {
		String retorno = conteudo.replace("<br>", "\n");
		retorno = retorno.replace("&aacute", "á");
		retorno = retorno.replace("&eacute", "é");
		retorno = retorno.replace("&oacute", "ó");
		retorno = retorno.replace("&iacute", "í");
		retorno = retorno.replace("&uacute", "ú");
		retorno = retorno.replace("&atilde", "ã");
		retorno = retorno.replace("&otilde", "õ");
		retorno = retorno.replace("&ccedil", "ç");
		retorno = retorno.replace("<html>", "");
		retorno = retorno.replace("</html>", "");
		retorno = retorno.replace("<p>", "");
		retorno = retorno.replace("</p>", "\n");
		retorno = retorno.replace(espacosHtml, "");
		retorno = retorno.replace("</a href=", "");
		retorno = retorno.replace(">", "");
		retorno = retorno.replace("'", "");
		retorno = retorno.replace("</a>", "");
		return retorno;
	}
	
	@SuppressWarnings("unchecked")
	private static void enviarEmail(String titulo, String notificacao, HashMap<?, String> dados) throws Exception {
		String hostName = InetAddress.getLocalHost().getHostName();
		final String finalMensagem = "Att.<br>M&oacute;dulo de Transportes do Siga.<br><br>" +
		   		"Aten&ccedil;&atilde;o: esta &eacute; uma mensagem autom&aacute;tica. Por favor, n&atilde;o responda.";
		
		Set<Object> itensKey = (Set<Object>) dados.keySet();

		for(Object item : itensKey){
			String mensagemAlterada = substituirMarcacoesMensagem(titulo, notificacao, dados.get(item), item);
			String conteudoHTML = "<html>" + mensagemAlterada;
    		String[] lista = dados.get(item).split(",");

			for (String itemLista : lista) {
	    		Boolean primeiraVez = true;
				String sequence =  itemLista.substring(0, itemLista.indexOf(" "));
				String id = itemLista.substring(itemLista.indexOf(" ") + 1);
				List<String> parametros = new ArrayList<String>();

				if (titulo.contains("Missoes")) {
					if (notificacao.contains("Nao finalizada")) {
						parametros.add("id," + id + ",Missoes.finalizar,Finalizar");
					}
					else if (notificacao.contains("Nao iniciada")) {
						parametros.add("id," + id + ",Missoes.iniciar,Iniciar");
						parametros.add("id," + id + ",Missoes.cancelar,Cancelar");
					}
				}
				
				if (titulo.contains("Requisicoes")) {
					if (notificacao.contains("Pendente aprovar")) {
						parametros.add("id," + id + ",Andamentos.autorizar,Autorizar");
						parametros.add("id," + id + ",Andamentos.rejeitar,Rejeitar");
					}
				}
				
				for (String parametro : parametros) {
					String[] itens = parametro.split(",");
					Map<String,Object> param = new HashMap<String, Object>();
					param.put(itens[0], itens[1]);
					
					FormataCaminhoDoContextoUrl formata = new FormataCaminhoDoContextoUrl();
					String caminhoUrl = formata.retornarCaminhoContextoUrl(Router.getFullUrl(itens[2],param));
					
					conteudoHTML += (primeiraVez ? "<p>" + sequence : "") + espacosHtml + 
								    "<a href='" + "http://" + hostName + caminhoUrl + "'>" + itens[3] + "</a>" + 
								    espacosHtml;
					primeiraVez = false;
				}
			}

			conteudoHTML += "</p>";
			String remetente = SigaBaseProperties.getString("servidor.smtp.usuario.remetente");
			String assunto = titulo;
			String email = "";
			String destinatario[];
			email = Play.configuration.getProperty("cron.listaEmailw").toString();
			destinatario = email.split(",");

		
			conteudoHTML += finalMensagem + "</html>";
			String conteudo = retirarTagsHtml(conteudoHTML);
			
			Correio.enviar(remetente, destinatario, assunto, conteudo, conteudoHTML);
			SimpleDateFormat fr = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			Calendar calendar = Calendar.getInstance();
			Logger.info(fr.format(calendar.getTime()) + " - Email enviado para " + email + ", assunto: " + assunto);
		}
	}
}