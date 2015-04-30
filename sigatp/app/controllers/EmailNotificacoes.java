package controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.Condutor;
import models.EstadoMissao;
import models.Missao;
import models.Parametro;
import play.Logger;
import play.jobs.Job;
import play.jobs.On;
import play.mvc.Router;
import uteis.FormatarTextoHtml;
import br.gov.jfrj.siga.base.Correio;
import br.gov.jfrj.siga.base.SigaBaseProperties;
import br.gov.jfrj.siga.dp.DpPessoa;

@On("cron.inicio")
public class EmailNotificacoes extends Job<Object>  {
	private static final String espacosHtml = "&nbsp;&nbsp;";
	
	public void doJob() {
		boolean executa = Boolean.parseBoolean(Parametro.buscarConfigSistemaEmVigor("cron.executa"));
		if (executa) {
			try {
				verificarVencimentoCarteira3MesesAntes();
				verificarMissoesProgramadas();
				verificarMissoesIniciadasMaisDe7Dias();
			} catch (Exception ex) {
				Logger.info("Erro no Serviço EmailNotificações " + ex.getMessage());
			}
		} else {
			Logger.info("Serviço EmailNotificações desligado");
		}
		Logger.info("Serviço EmailNotificações finalizado");
	}
	
	private void verificarMissoesProgramadas()  {
		List<Missao> missoes = new ArrayList<Missao>();
		String tituloEmail = "Miss\u00F5es Programadas n\u00E3o Iniciadas";
		String tipoNotificacao = "Nao iniciada";

		try {
			Calendar calendar = Calendar.getInstance();
			missoes = Missao.find("estadoMissao = ? and dataHoraSaida < ? " +
					"order by condutor", EstadoMissao.PROGRAMADA, calendar).fetch();
			if (missoes.size() > 0) {
				notificarMissoes(missoes, tituloEmail, tipoNotificacao);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void verificarMissoesIniciadasMaisDe7Dias()  {
		List<Missao> missoes = new ArrayList<Missao>();
		String tituloEmail = "Miss\u00F5es Iniciadas a mais de 7 Dias n\u00E3o Finalizadas";
		String tipoNotificacao = "Nao finalizada";
		
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_YEAR, -7);
			missoes = Missao.find("estadoMissao = ? and dataHoraSaida < ? " +
					"order by condutor", EstadoMissao.INICIADA, calendar).fetch();
			if (missoes.size() > 0) {
				notificarMissoes(missoes, tituloEmail, tipoNotificacao);
			}	

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static void notificarMissoes(List<Missao> missoes, String titulo, String notificacao) throws Exception  {
		Condutor condutor = new Condutor();
		HashMap<Condutor, String> dadosCondutor = new HashMap<Condutor, String>();
		
		for(Missao item : missoes) {
			condutor = item.condutor;
			String sequencia = item.getSequence() + " " + item.id + ",";

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
		String data = "";
		String parteMensagem = "";
		Boolean plural = lista.split(",").length > 1 ? true : false;
		String mensagem; 
		String tituloSemAcento = FormatarTextoHtml.removerAcentuacao(titulo);
		String notificacaoSemAcento = FormatarTextoHtml.removerAcentuacao(notificacao);
				
		if (pessoa.getClass().equals(Condutor.class)) {
			sexo = ((Condutor)pessoa).dpPessoa.getSexo().toUpperCase();
			nome = ((Condutor)pessoa).getNome();
			DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
			data = df.format(((Condutor) pessoa).getDataVencimentoCNH().getTime());
			
			if (tituloSemAcento.contains("Missoes")) {
				parteMensagem = plural ? "as miss&otilde;es " : "a miss&atilde;o ";

				if (notificacaoSemAcento.contains("Nao finalizada")) {
					parteMensagem += "abaixo, caso j&aacute; tenha/m sido realizada/s, " +
									 "precisa/m ser finalizada/s.<br>";

				} else if (notificacaoSemAcento.contains("Nao iniciada")) {
					parteMensagem += "abaixo precisa/m ser iniciada/s ou cancelada/s.<br>";
				}
			} else if (titulo.contains("Carteira")) {
				parteMensagem += "informamos que, de acordo com seu cadastro, sua carteira de habilita&ccedil;&atilde;o vencer&aacute; em " + data + ".";
			}
		}
		
		mensagem = sexo.equals("F") ? "Prezada Sra. " + nome + ", "	: "Prezado Sr. " + nome + ", ";
		mensagem += parteMensagem.replaceAll("/s", plural ? "s" : "").replaceAll("/m", plural ? "m" : "");
		return mensagem;
	}
	
	@SuppressWarnings("unchecked")
	private static void enviarEmail(String titulo, String notificacao, HashMap<?, String> dados) throws Exception {
		String hostName = System.getProperty(Parametro.buscarConfigSistemaEmVigor("caminhoHostnameStandalone"));
		final String finalMensagem = "<b>Mensagem autom&aacute;tica enviada pelo M&oacute;dulo de Transportes do Siga. Favor n&atilde;o responder.</b><br>";		
		Set<Object> itensKey = (Set<Object>) dados.keySet();

		for(Object item : itensKey){
			String conteudoHTML = "<html>" + substituirMarcacoesMensagem(titulo, notificacao, dados.get(item), item);
			String[] lista = dados.get(item).split(",");
			String tituloSemAcento = FormatarTextoHtml.removerAcentuacao(titulo);
			String notificacaoSemAcento = FormatarTextoHtml.removerAcentuacao(notificacao);

			for (String itemLista : lista) {
	    		Boolean primeiraVez = true;
				String sequence =  itemLista.substring(0, itemLista.indexOf(" "));
				String id = itemLista.substring(itemLista.indexOf(" ") + 1);
				List<String> parametros = new ArrayList<String>();

				if (tituloSemAcento.contains("Missoes")) {
					if (notificacaoSemAcento.contains("Nao finalizada")) {
						parametros.add("id," + id + ",Missoes.finalizar,Finalizar");
					}
					else if (notificacaoSemAcento.contains("Nao iniciada")) {
						parametros.add("id," + id + ",Missoes.iniciar,Iniciar");
						parametros.add("id," + id + ",Missoes.cancelar,Cancelar");
					}
				}
				
				for (String parametro : parametros) {
					String[] itens = parametro.split(",");
					Map<String,Object> param = new HashMap<String, Object>();
					param.put(itens[0], itens[1]);
					
					if (titulo.contains("Carteira")) {
						conteudoHTML += (primeiraVez ? "<p>" + sequence : "") + espacosHtml;
					} else {
						String caminhoUrl = Router.reverse(itens[2], param).url;
						conteudoHTML += (primeiraVez ? "<p>" + sequence : "")
								+ espacosHtml + "<a href='" 
								+ hostName + caminhoUrl + "'>" + itens[3]
								+ "</a>" + espacosHtml;
					}
					primeiraVez = false;
				}
			}

			conteudoHTML += "</p>";
			String remetente = SigaBaseProperties.getString("servidor.smtp.usuario.remetente");
			String assunto = titulo;
			String email = "";
			String destinatario[];
			boolean flagEmail = Boolean.parseBoolean(Parametro.buscarConfigSistemaEmVigor("cron.flagEmail"));

			if (!flagEmail) {
				if (item.getClass().equals(Condutor.class)) {
					email = ((Condutor)item).dpPessoa.getEmailPessoa();
				}
				else if (item.getClass().equals(DpPessoa.class)) {
					email = ((DpPessoa) item).getEmailPessoa();
				}
				destinatario = new String[1];
				destinatario[0] = email;				
			} 
			else {
				email = Parametro.buscarConfigSistemaEmVigor("cron.listaEmail");
				destinatario = email.split(",");
			}
		
			conteudoHTML += finalMensagem + "</html>";
			String conteudo = FormatarTextoHtml.retirarTagsHtml(conteudoHTML, espacosHtml);
			
			Correio.enviar(remetente, destinatario, assunto, conteudo, conteudoHTML);
			SimpleDateFormat fr = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			Calendar calendar = Calendar.getInstance();
			Logger.info(fr.format(calendar.getTime()) + " - Email enviado para " + email + ", assunto: " + assunto);
		}
		
		private void verificarVencimentoCarteira3MesesAntes() {
			List<Condutor> condutores = new ArrayList<Condutor>();
			String tituloEmail = "Aviso antecipado de vencimento de Carteira de motorista";
			String tipoNotificacao = "vencimento de carteira de motorista";		
			try {
				condutores = Condutor.listarTodos();
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.MONTH, +3);
				if (condutores.size() > 0) {
					notificarCondutores(condutores, tituloEmail, tipoNotificacao,calendar);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private static void notificarCondutores(List<Condutor> condutores, String titulo, String notificacao, Calendar cal) throws Exception {
			Condutor condutor = new Condutor();
			HashMap<Condutor, String> dadosCondutor = new HashMap<Condutor, String>();

			for (Condutor item : condutores) {
				String sequencia = item.getNome() + " " + item.id + " ,";
				condutor = item;
				if (((condutor.dataVencimentoCNH.getTimeInMillis()) > 0)) {
					if (condutor.dataVencimentoCNH.getTime().compareTo(descartarHora(cal.getTime())) == 0) {
						if (dadosCondutor.containsKey(condutor)) {
							dadosCondutor.put(condutor, dadosCondutor.get(condutor)	+ sequencia);
						} else {
							dadosCondutor.put(condutor, sequencia);
						}
					}
				}
			}
			if (dadosCondutor.size() > 0) {
				enviarEmail(titulo, notificacao, dadosCondutor);
			}
		}
		
		private static Date descartarHora(Date data) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(data);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			return cal.getTime();
		}
	}
}