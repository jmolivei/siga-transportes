package br.gov.jfrj.siga.tp.vraptor;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import play.mvc.Router;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.validator.I18nMessage;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.tp.auth.AutorizacaoGI;
import br.gov.jfrj.siga.tp.model.Condutor;
import br.gov.jfrj.siga.tp.model.EstadoMissao;
import br.gov.jfrj.siga.tp.model.EstadoRequisicao;
import br.gov.jfrj.siga.tp.model.EstadoServico;
import br.gov.jfrj.siga.tp.model.Missao;
import br.gov.jfrj.siga.tp.model.RequisicaoTransporte;
import br.gov.jfrj.siga.tp.model.SelecaoDocumento;
import br.gov.jfrj.siga.tp.model.ServicoVeiculo;
import br.gov.jfrj.siga.tp.model.TpDao;
import br.gov.jfrj.siga.tp.util.CondutorFiltro;
import br.gov.jfrj.siga.tp.util.FormataCaminhoDoContextoUrl;
import br.gov.jfrj.siga.vraptor.SigaObjects;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;


@Resource
@Path("/app/application/")
public class ApplicationController extends TpController {

    private AutorizacaoGI autorizacaoGI;

    private MissaoController missaoController;

    private RequisicaoController requisicaoController;

    private ServicoVeiculoController servicoVeiculoController;

    public ApplicationController(HttpServletRequest request, Result result, CpDao dao, Validator validator, SigaObjects so, EntityManager em, AutorizacaoGI autorizacaoGI, MissaoController missaoController, RequisicaoController requisicaoController, ServicoVeiculoController servicoVeiculoController) {
        super(request, result, TpDao.getInstance(), validator, so, em);
        this.autorizacaoGI = autorizacaoGI;
        this.missaoController = missaoController;
        this.requisicaoController = requisicaoController;
        this.servicoVeiculoController = servicoVeiculoController;
    }

    @Path("index")
    public void index() throws Exception {
		if (autorizacaoGI.ehAdministrador() || autorizacaoGI.ehAdministradorMissao() || autorizacaoGI.ehAdministradorMissaoPorComplexo() ) {
			result.forwardTo(RequisicaoController.class).listarFiltrado(EstadoRequisicao.AUTORIZADA,EstadoRequisicao.NAOATENDIDA);
		}
		else if (autorizacaoGI.ehAgente()) {
		    result.forwardTo(RequisicaoController.class).listarFiltrado(EstadoRequisicao.AUTORIZADA,EstadoRequisicao.NAOATENDIDA);
		}
		else if (autorizacaoGI.ehAprovador()) {
		    result.forwardTo(RequisicaoController.class).listarPAprovar();
		}
		else{
		    result.forwardTo(RequisicaoController.class).listar();
		}
	}

    @Path("selecionar/{sigla}")
	public void selecionar(String sigla) {
		SelecaoDocumento sel = new SelecaoDocumento();
		sel.sigla = montarSigla(sigla);
		sel.id = 0L;
		sel.descricao = "";
		result.include("sel", sel);
	}

    @Path("exibir/{sigla}")
	public void exibir(String sigla) throws Exception {

		String[] partesDoCodigo=null;
		try {
			partesDoCodigo = sigla.split("[-/]");

		} catch (Exception e) {
			throw new Exception(new I18nMessage("application","application.exibir.sigla.exception").getMessage());
		}

		result.include("sigla", partesDoCodigo[4]);

		if (partesDoCodigo[1].equals("TP")) {
			if (partesDoCodigo[4].equals("M")) {
				missaoController.recuperarPelaSigla(sigla,!autorizacaoGI.ehAdministrador());
			}

			if (partesDoCodigo[4].equals("R")) {
				RequisicaoTransporte req = requisicaoController.recuperarPelaSigla(sigla, !autorizacaoGI.ehAdministrador());
				requisicaoController.carregarTiposDeCarga(req);
				requisicaoController.carregarFinalidades();
			}

			if (partesDoCodigo[4].equals("S")) {
			    servicoVeiculoController.recuperarPelaSigla(sigla, ! autorizacaoGI.ehAdministrador());
			}
		}

	}

    @Path("emdesenvolvimento")
	public void emdesenvolvimento() {

	}

    @Path("selecionarPessoa")
	public void selecionarPessoa() {
		CondutorFiltro filtro = new CondutorFiltro();
		filtro.condutorFiltro = new DpPessoa();
		filtro.lotaCondutorFiltro = new DpLotacao();
		result.include("filtro", filtro);
	}

    @Path("selecionarPessoa/{sigla}/{tipo}/{nome}")
	public void selecionarSiga(String sigla, String tipo, String nome) throws Exception {
	    //TODO: Funcionamento incerto
	    result.redirectTo("/siga/app/" + tipo + "/selecionar?" + "propriedade="
                + tipo + nome + "&sigla=" + URLEncoder.encode(sigla, "UTF-8"));
	}

    @Path("buscarSiga/{sigla}/{tipo}/{nome}")
	public void buscarSiga(String sigla, String tipo, String nome) 	throws Exception {
        //TODO: Funcionamento incerto
	    result.redirectTo("/siga/app/" + tipo + "/buscar?" + "propriedade=" + tipo
				+ nome + "&sigla=" + URLEncoder.encode(sigla, "UTF-8"));
	}

    @Path("exibirManualUsuario")
	public void exibirManualUsuario() throws Exception {

	}

    @Path("exibirManualUsuarioDeGabinete")
	public void exibirManualUsuarioDeGabinete() throws Exception {

	}

    @Path("gadget")
	public void gadget() {
		try {
			String titulo = "";
			Long idOrgaoUsu = Long.valueOf(getTitular().getOrgaoUsuario().getIdOrgaoUsu());
			List<String[]> lista = new ArrayList<String[]>();
			int total = 0;

			if (equals(true, autorizacaoGI.ehAdministrador(), autorizacaoGI.ehAdministradorMissao(), autorizacaoGI.ehAdministradorMissaoPorComplexo())) {
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

			else if (equals(true, autorizacaoGI.ehAgente())) {
				Long idCondutor = Condutor.recuperarLogado(getTitular(),getTitular().getOrgaoUsuario()).getId();
				EstadoMissao[] estados = {EstadoMissao.PROGRAMADA, EstadoMissao.INICIADA};
				String query = "condutor.id = ? and cpOrgaoUsuario.idOrgaoUsu = ? and (estadoMissao = ? or estadoMissao = ?)";
				List<Missao> missoes = Missao.AR.find(query, idCondutor, idOrgaoUsu, estados[0], estados[1]).fetch();

				for (EstadoMissao item : estados) {
					titulo = "Miss&otilde;es " + (item.equals(EstadoMissao.PROGRAMADA) ? "programadas" : "iniciadas");
					total = totalizarItemLista(missoes, item.getDescricao());
					if (total > 0) {
						lista.add(adicionarItemLista("missoes.listarfiltrado", "estado", item.getDescricao(), titulo, total));
					}
				}
			}

			else if (equals(true, autorizacaoGI.ehAprovador())) {
				EstadoRequisicao estado = EstadoRequisicao.ABERTA;
				List<RequisicaoTransporte> requisicoes = RequisicaoTransporte.listar(estado);
				if (requisicoes.size() > 0) {
					lista.add(adicionarItemLista("requisicoes.listarfiltrado", "estadoRequisicao", estado.getDescricao(),"Requisi&ccedil;&otilde;es a autorizar",requisicoes.size()));
				}
			}

			else if (equals(true, autorizacaoGI.ehAdministradorFrota())) {
				EstadoServico[] estados = {EstadoServico.AGENDADO, EstadoServico.INICIADO};
				String query = "cpOrgaoUsuario.idOrgaoUsu=? and (situacaoServico = ? or situacaoServico = ?)";
				List<ServicoVeiculo> servicos = ServicoVeiculo.AR.find(query, idOrgaoUsu, estados[0],estados[1]).fetch();

				for (EstadoServico item : estados) {
					titulo = "Servi&ccedil;os " + (item.equals(EstadoServico.AGENDADO) ? "agendados" : "iniciados");
					total = totalizarItemLista(servicos, item.getDescricao());
					if (total > 0) {
						lista.add(adicionarItemLista("servicosVeiculo.listarfiltrado", "estado", item.getDescricao(), titulo, total));
					}
				}
			}

			else {
				List<RequisicaoTransporte> requisicoes = RequisicaoTransporte.listarParaAgendamento(getTitular().getOrgaoUsuario());
				total = totalizarItemLista(requisicoes, "");
				if (requisicoes.size() > 0) {
					lista.add(adicionarItemLista("requisicoes.listar", "", "", "Requisi&ccedil;&otilde;es", total));
				}
			}

			result.include("lista", lista);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private String montarSigla(String sigla) {
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
    			retorno = getCadastrante().getOrgaoUsuario().getAcronimoOrgaoUsu().replace("-","").toString();
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

    private String[] adicionarItemLista(String template, String parametro, String descricao, String titulo, int total) {
		String[][] itemLista = new String[1][3];
		itemLista[0][0] = obterCaminhoUrl(template, parametro, descricao);
		itemLista[0][1] = titulo;
		itemLista[0][2] =  String.valueOf(total);
		return itemLista[0];
	}

	private String obterCaminhoUrl(String template, String parametro, String valor) {
	    //TODO Corrigir
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

	private Boolean equals(Object item, Object ... search) {
		for (Object object : search) {
			if (item.equals(object)) {
				return true;
			}
		}
		return false;
	}

	private <T> Integer totalizarItemLista(List<T> lista, String itemDescricao) {
		final String descricao = itemDescricao;
		int total = 0;

		try {
			if (lista.size() > 0) {
				List<T> itemFiltrado = Lists.newArrayList(Iterables.filter(lista, new Predicate<T>() {
					public boolean apply(T objeto) {
						if (objeto instanceof Missao) {
							Missao missao = ((Missao) objeto);
							return missao.getEstadoMissao().getDescricao().equals(descricao);
						}
						if (objeto instanceof RequisicaoTransporte) {
							Calendar ultimos7dias = Calendar.getInstance();
							ultimos7dias.add(Calendar.DATE, -7);
							RequisicaoTransporte requisicao = ((RequisicaoTransporte) objeto);
							if (descricao.equals("")) {
								return (requisicao.getDataHoraSaidaPrevista().after(ultimos7dias) &&
										requisicao.getCpOrgaoUsuario().getIdOrgaoUsu().equals(getTitular().getOrgaoUsuario().getIdOrgaoUsu()));
							} else {
								return (requisicao.getUltimoEstado().getDescricao().equals(descricao) &&
										requisicao.getDataHoraSaidaPrevista().after(ultimos7dias) &&
										requisicao.getCpOrgaoUsuario().getIdOrgaoUsu().equals(getTitular().getOrgaoUsuario().getIdOrgaoUsu()));
							}
						}
						if (objeto instanceof ServicoVeiculo) {
							ServicoVeiculo servico = ((ServicoVeiculo) objeto);
							return servico.getSituacaoServico().getDescricao().equals(descricao);
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