package controllers;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import play.i18n.Messages;

import org.apache.commons.lang.StringUtils;

import play.mvc.Controller;
import play.mvc.Router;
import play.mvc.With;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.tp.model.Condutor;
import br.gov.jfrj.siga.tp.model.EstadoMissao;
import br.gov.jfrj.siga.tp.model.EstadoRequisicao;
import br.gov.jfrj.siga.tp.model.EstadoServico;
import br.gov.jfrj.siga.tp.model.Missao;
import br.gov.jfrj.siga.tp.model.RequisicaoTransporte;
import br.gov.jfrj.siga.tp.model.SelecaoDocumento;
import br.gov.jfrj.siga.tp.model.ServicoVeiculo;
import br.gov.jfrj.siga.tp.util.CondutorFiltro;
import br.gov.jfrj.siga.tp.util.FormataCaminhoDoContextoUrl;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@With(AutorizacaoGIAntigo.class)
public class Application extends Controller {

	public static void index() throws Exception {
		if (AutorizacaoGIAntigo.ehAdministrador() || AutorizacaoGIAntigo.ehAdministradorMissao() || AutorizacaoGIAntigo.ehAdministradorMissaoPorComplexo() ) {
			Requisicoes.listarFiltrado(EstadoRequisicao.AUTORIZADA,EstadoRequisicao.NAOATENDIDA);
		}

		if (AutorizacaoGIAntigo.ehAgente()) {
			Requisicoes.listarFiltrado(EstadoRequisicao.AUTORIZADA,EstadoRequisicao.NAOATENDIDA);
		}

		if (AutorizacaoGIAntigo.ehAprovador()) {
			Requisicoes.listarPAprovar();
		}

		Requisicoes.listar();

	}

	public static void selecionar(String sigla) {
		SelecaoDocumento sel = new SelecaoDocumento();
		sel.sigla = montarSigla(sigla);
		sel.id = 0L;
		sel.descricao = "";    	
		render("@selecionar", sel);
		render();
	}

	public static void exibir(String sigla) throws Exception {

		String[] partesDoCodigo=null;
		try {
			partesDoCodigo = sigla.split("[-/]");			

		} catch (Exception e) {
			throw new Exception(Messages.get("application.exibir.sigla.exception", sigla));
		}

		renderArgs.put("sigla", partesDoCodigo[4]);

		if (partesDoCodigo[1].equals("TP")) {
			if (partesDoCodigo[4].equals("M")) {
				Missoes.recuperarPelaSigla(sigla,!AutorizacaoGIAntigo.ehAdministrador());
			}

			if (partesDoCodigo[4].equals("R")) {
				RequisicaoTransporte req = Requisicoes.recuperarPelaSigla(sigla, !AutorizacaoGIAntigo.ehAdministrador());
				Requisicoes.carregarTiposDeCarga(req);
				Requisicoes.carregarFinalidades();
			}

			if (partesDoCodigo[4].equals("S")) {
				ServicosVeiculo.recuperarPelaSigla(sigla, ! AutorizacaoGIAntigo.ehAdministrador());
			}
		}
		render();
	}

	public static void emdesenvolvimento() {
		render();
	}

	private static String montarSigla(String sigla) {
		String retorno = "";
		sigla = sigla.trim().toUpperCase();

		//substitui o penúltimo "-" por "/" caso não tenha nenhum.
		if (StringUtils.countMatches(sigla, "-") > 0) {
			if (StringUtils.countMatches(sigla, "/") == 0) {
				int total = StringUtils.countMatches(sigla, "-");
				int posicao = StringUtils.ordinalIndexOf(sigla, "-", total-1);
				StringBuilder strSigla = new StringBuilder(sigla);
				strSigla.setCharAt(posicao, "/".charAt(0));
				sigla = strSigla.toString();
			}
		}

		//Formato TRF2-TP-2014/00001-R
		final Pattern p = Pattern.compile("^?([A-Z]{2})?-?(TP{1})-?([0-9]{4})?/?([0-9]{1,5})-?([MSR]{1})?$");
		final Matcher m = p.matcher(sigla);

		if (m.find()) {
			if (m.group(1) != null) {
				retorno = m.group(1).toString();
			}
			else {		
				retorno = AutorizacaoGIAntigo.cadastrante().getOrgaoUsuario().getAcronimoOrgaoUsu().replace("-","").toString();
			}

			retorno = retorno + "-" + m.group(2).toString();

			Calendar c1 = Calendar.getInstance();

			/*Quando o ano não é digitado, mgroup(3) recebe o 4 primeiros dígitos que deveriam ser de mgroup(4)
			Por isso é necessário verificar o tamanho da concatenação dos dois que deve ser menor ou igual a 5 */
			if (m.group(3) != null && m.group(4) != null) {
				if ((m.group(3).toString() + m.group(5).toString()).length() <= 5) {
					c1.set(Calendar.YEAR, Integer.valueOf(m.group(3)));
					c1.set(Calendar.DAY_OF_YEAR, 1);
				}
			}	

			retorno = retorno + "-" + String.format("%04d",c1.get(Calendar.YEAR));

			if (m.group(3) != null && m.group(4) != null) {
				if ((m.group(3).toString() + m.group(4).toString()).length() <= 5) {
					retorno = retorno + "/" + String.format("%05d", Integer.parseInt(m.group(3).toString() + m.group(4).toString()));
				}
				else {
					retorno = retorno + "/" + String.format("%05d",Integer.parseInt(m.group(4)));
				}
			}
			else if (m.group(3) != null && m.group(4) == null) {
				retorno = retorno + "/" + String.format("%05d",Integer.parseInt(m.group(3)));
			}
			else if (m.group(4) != null) {
				retorno = retorno + "/" + String.format("%05d",Integer.parseInt(m.group(4)));
			}
			else	{
				retorno = retorno + "/0";
			}

			retorno = retorno + "-" + m.group(5);

			return retorno;
		}

		return sigla;
	}

	public static void selecionarPessoa() {
		CondutorFiltro filtro = new CondutorFiltro();
		filtro.condutorFiltro = new DpPessoa();
		filtro.lotaCondutorFiltro = new DpLotacao();
		render(filtro);
	}

	public static void selecionarSiga(String sigla, String tipo, String nome) throws Exception {
		redirect("/siga/" + tipo + "/selecionar.action?" + "propriedade="
				+ tipo + nome + "&sigla=" + URLEncoder.encode(sigla, "UTF-8"));
	}

	public static void buscarSiga(String sigla, String tipo, String nome) 	throws Exception {
		redirect("/siga/" + tipo + "/buscar.action?" + "propriedade=" + tipo
				+ nome + "&sigla=" + URLEncoder.encode(sigla, "UTF-8"));
	}

	public static void exibirManualUsuario() throws Exception {
		render();
	}

	public static void exibirManualUsuarioDeGabinete() throws Exception {
		render();
	}

	public static void gadget() {
		try {
			String titulo = "";
			Long idOrgaoUsu = Long.valueOf(AutorizacaoGIAntigo.titular().getOrgaoUsuario().getIdOrgaoUsu());
			List<String[]> lista = new ArrayList<String[]>();
			int total = 0;

			if (equals(true, AutorizacaoGIAntigo.ehAdministrador(), AutorizacaoGIAntigo.ehAdministradorMissao(), AutorizacaoGIAntigo.ehAdministradorMissaoPorComplexo())) {
				EstadoRequisicao[] estados = {EstadoRequisicao.AUTORIZADA, EstadoRequisicao.NAOATENDIDA};
				List<RequisicaoTransporte> requisicoes = RequisicaoTransporte.listar(estados);

				for (EstadoRequisicao item : estados) {
					titulo = "Requisi&ccedil;&otilde;es " + (item.equals(EstadoRequisicao.AUTORIZADA) ? "autorizadas" :	"nao atendidas");
					total = totalizarItemLista(requisicoes, item.getDescricao());
					if (total > 0) {
						lista.add(adicionarItemLista("requisicoes.listarfiltrado", "estadoRequisicao", item.getDescricao(), titulo, total));
					}
				}
			}

			else if (equals(true, AutorizacaoGIAntigo.ehAgente())) {
				Long idCondutor = Condutor.recuperarLogado(AutorizacaoGIAntigo.titular(),AutorizacaoGIAntigo.titular().getOrgaoUsuario()).getId();
				EstadoMissao[] estados = {EstadoMissao.PROGRAMADA, EstadoMissao.INICIADA};
				String query = "condutor.id = ? and cpOrgaoUsuario.idOrgaoUsu = ? and (estadoMissao = ? or estadoMissao = ?)";
				List<ServicosVeiculo> missoes = Missao.AR.find(query, idCondutor, idOrgaoUsu, estados[0], estados[1]).fetch();

				for (EstadoMissao item : estados) {
					titulo = "Miss&otilde;es " + (item.equals(EstadoMissao.PROGRAMADA) ? "programadas" : "iniciadas");
					total = totalizarItemLista(missoes, item.getDescricao());
					if (total > 0) {
						lista.add(adicionarItemLista("missoes.listarfiltrado", "estado", item.getDescricao(), titulo, total));
					}
				}
			}

			else if (equals(true, AutorizacaoGIAntigo.ehAprovador())) {
				EstadoRequisicao estado = EstadoRequisicao.ABERTA;
				List<RequisicaoTransporte> requisicoes = RequisicaoTransporte.listar(estado);
				if (requisicoes.size() > 0) {
					lista.add(adicionarItemLista("requisicoes.listarfiltrado", "estadoRequisicao", estado.getDescricao(),"Requisi&ccedil;&otilde;es a autorizar",requisicoes.size()));
				}
			}

			else if (equals(true, AutorizacaoGIAntigo.ehAdministradorFrota())) {
				EstadoServico[] estados = {EstadoServico.AGENDADO, EstadoServico.INICIADO};
				String query = "cpOrgaoUsuario.idOrgaoUsu=? and (situacaoServico = ? or situacaoServico = ?)"; 
				List<ServicosVeiculo> servicos = ServicoVeiculo.find(query, idOrgaoUsu, estados[0],estados[1]).fetch();

				for (EstadoServico item : estados) {
					titulo = "Servi&ccedil;os " + (item.equals(EstadoServico.AGENDADO) ? "agendados" : "iniciados");
					total = totalizarItemLista(servicos, item.getDescricao());
					if (total > 0) {
						lista.add(adicionarItemLista("servicosVeiculo.listarfiltrado", "estado", item.getDescricao(), titulo, total));
					}
				}
			}

			else {
				List<RequisicaoTransporte> requisicoes = RequisicaoTransporte.listarParaAgendamento(AutorizacaoGIAntigo.titular().getOrgaoUsuario());
				total = totalizarItemLista(requisicoes, "");
				if (requisicoes.size() > 0) {
					lista.add(adicionarItemLista("requisicoes.listar", "", "", "Requisi&ccedil;&otilde;es", total));
				}
			}

			render(lista);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		render();
	}

	private static String[] adicionarItemLista(String template, String parametro, String descricao, String titulo, int total) {
		String[][] itemLista = new String[1][3];
		itemLista[0][0] = obterCaminhoUrl(template, parametro, descricao);
		itemLista[0][1] = titulo;
		itemLista[0][2] =  String.valueOf(total);
		return itemLista[0];
	}

	private static String obterCaminhoUrl(String template, String parametro, String valor) {
		FormataCaminhoDoContextoUrl formata = new FormataCaminhoDoContextoUrl();
		if (!parametro.equals("") && !valor.equals("")) {
			Map<String,Object> param=new HashMap<String,Object>();
			param.put(parametro, valor);
			return formata.retornarCaminhoContextoUrl(Router.reverse(template,param).url);
		}
		else {
			return formata.retornarCaminhoContextoUrl(Router.reverse(template).url);
		}
	}

	private static Boolean equals(Object item, Object ... search) {
		for (Object object : search) {
			if (item.equals(object)) {
				return true;
			}
		}
		return false;
	}

	private static <T> Integer totalizarItemLista(List<T> lista, String itemDescricao) {
		final String descricao = itemDescricao;
		int total = 0;

		try {
			if (lista.size() > 0) {
				List<T> itemFiltrado = Lists.newArrayList(Iterables.filter(lista, new Predicate<T>() {
					public boolean apply(T objeto) {
						if (objeto instanceof Missao) { 
							Missao missao = ((Missao) objeto);
							return missao.estadoMissao.getDescricao().equals(descricao);
						}
						if (objeto instanceof RequisicaoTransporte) {
							Calendar ultimos7dias = Calendar.getInstance();
							ultimos7dias.add(Calendar.DATE, -7);
							RequisicaoTransporte requisicao = ((RequisicaoTransporte) objeto);
							if (descricao.equals("")) {
								return (requisicao.dataHoraSaidaPrevista.after(ultimos7dias) && 
										requisicao.cpOrgaoUsuario.getIdOrgaoUsu().equals(AutorizacaoGIAntigo.titular().getOrgaoUsuario().getIdOrgaoUsu()));
							} else {
								return (requisicao.ultimoEstado.getDescricao().equals(descricao) &&
										requisicao.dataHoraSaidaPrevista.after(ultimos7dias) && 
										requisicao.cpOrgaoUsuario.getIdOrgaoUsu().equals(AutorizacaoGIAntigo.titular().getOrgaoUsuario().getIdOrgaoUsu()));
							}
						}
						if (objeto instanceof ServicoVeiculo) {
							ServicoVeiculo servico = ((ServicoVeiculo) objeto); 
							return servico.situacaoServico.getDescricao().equals(descricao);
						}
						return false;
					}
				}));

				total = itemFiltrado.size();
			} 
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return total;
	}
}