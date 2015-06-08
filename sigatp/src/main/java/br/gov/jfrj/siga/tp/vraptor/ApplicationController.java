package br.gov.jfrj.siga.tp.vraptor;

import java.io.UnsupportedEncodingException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.validator.I18nMessage;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.tp.auth.AutorizacaoGI;
import br.gov.jfrj.siga.tp.exceptions.ApplicationControllerException;
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
@Path("/app/application")
public class ApplicationController extends TpController {

    private final class PredicateImplementation<T> implements Predicate<T> {
        private final String descricao;

        private PredicateImplementation(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public boolean apply(T objeto) {
            return applyMissao(objeto) || applyRequisicaoTransporte(objeto) || applyServico(objeto);
        }

        private boolean applyServico(T obj) {
            if (obj instanceof ServicoVeiculo) {
                ServicoVeiculo servico = (ServicoVeiculo) obj;
                return servico.getSituacaoServico().getDescricao().equals(descricao);
            } else {
                return false;
            }
        }

        private boolean applyRequisicaoTransporte(T obj) {
            if (obj instanceof RequisicaoTransporte) {
                RequisicaoTransporte requisicao = (RequisicaoTransporte) obj;
                Calendar ultimos7dias = Calendar.getInstance();
                ultimos7dias.add(Calendar.DATE, -7);
                if ("".equals(descricao)) {
                    return requisicao.getDataHoraSaidaPrevista().after(ultimos7dias) && requisicao.getCpOrgaoUsuario().getIdOrgaoUsu().equals(getTitular().getOrgaoUsuario().getIdOrgaoUsu());
                } else {
                    return requisicao.getUltimoEstado().getDescricao().equals(descricao) && requisicao.getDataHoraSaidaPrevista().after(ultimos7dias)
                            && requisicao.getCpOrgaoUsuario().getIdOrgaoUsu().equals(getTitular().getOrgaoUsuario().getIdOrgaoUsu());
                }
            } else {
                return false;
            }
        }

        private boolean applyMissao(T obj) {
            if (obj instanceof Missao) {
                Missao missao = (Missao) obj;
                return missao.getEstadoMissao().getDescricao().equals(descricao);
            } else {
                return false;
            }
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationController.class);

    private AutorizacaoGI autorizacaoGI;

    private MissaoController missaoController;

    private RequisicaoController requisicaoController;

    private ServicoVeiculoController servicoVeiculoController;

    public ApplicationController(HttpServletRequest request, Result result, Validator validator, SigaObjects so, EntityManager em, AutorizacaoGI autorizacaoGI, MissaoController missaoController,
            RequisicaoController requisicaoController, ServicoVeiculoController servicoVeiculoController) {
        super(request, result, TpDao.getInstance(), validator, so, em);
        this.autorizacaoGI = autorizacaoGI;
        this.missaoController = missaoController;
        this.requisicaoController = requisicaoController;
        this.servicoVeiculoController = servicoVeiculoController;
    }

    @Path("/index")
    public void index() throws ApplicationControllerException {
        try {
            if (autorizacaoGI.ehAdministrador() || autorizacaoGI.ehAdministradorMissao() || autorizacaoGI.ehAdministradorMissaoPorComplexo()) {
                result.forwardTo(RequisicaoController.class).listarFiltrado(EstadoRequisicao.AUTORIZADA, EstadoRequisicao.NAOATENDIDA);
            } else if (autorizacaoGI.ehAgente()) {
                result.forwardTo(RequisicaoController.class).listarFiltrado(EstadoRequisicao.AUTORIZADA, EstadoRequisicao.NAOATENDIDA);
            } else if (autorizacaoGI.ehAprovador()) {
                result.forwardTo(RequisicaoController.class).listarPAprovar();
            } else {
                result.forwardTo(RequisicaoController.class).listar();
            }
        } catch (Exception e) {
            throw new ApplicationControllerException(e);
        }
    }

    @Path("selecionar/{sigla}")
    public void selecionar(String sigla) {
        SelecaoDocumento sel = new SelecaoDocumento();
        sel.setSigla(montarSigla(sigla));
        sel.setId(0L);
        sel.setDescricao("");
        result.include("sel", sel);
    }

    @Path("/exibir/{sigla}")
    public void exibir(String sigla) throws ApplicationControllerException {

        String[] partesDoCodigo = null;
        try {
            partesDoCodigo = sigla.split("[-/]");

        } catch (Exception e) {
            throw new ApplicationControllerException(new I18nMessage("application", "application.exibir.sigla.exception").getMessage(), e);
        }

        result.include("sigla", partesDoCodigo[4]);

        try {
            if ("TP".equals(partesDoCodigo[1])) {
                if ("M".equals(partesDoCodigo[4])) {
                    missaoController.recuperarPelaSigla(sigla, !autorizacaoGI.ehAdministrador());
                }

                if ("R".equals(partesDoCodigo[4])) {
                    RequisicaoTransporte req = requisicaoController.recuperarPelaSigla(sigla, !autorizacaoGI.ehAdministrador());
                    requisicaoController.carregarTiposDeCarga(req);
                    requisicaoController.carregarFinalidades();
                }

                if ("S".equals(partesDoCodigo[4])) {
                    servicoVeiculoController.recuperarPelaSigla(sigla, !autorizacaoGI.ehAdministrador());
                }
            }
        } catch (Exception e) {
            throw new ApplicationControllerException(e);
        }
    }

    @Path("/emDesenvolvimento")
    public void emDesenvolvimento() {

    }

    @Path("/selecionarPessoa")
    public void selecionarPessoa() {
        CondutorFiltro filtro = new CondutorFiltro();
        filtro.condutorFiltro = new DpPessoa();
        filtro.lotaCondutorFiltro = new DpLotacao();
        result.include("filtro", filtro);
    }

    @Path("/selecionarPessoa/{sigla}/{tipo}/{nome}")
    public void selecionarSiga(String sigla, String tipo, String nome) throws ApplicationControllerException {
        try {
            result.redirectTo("/siga/app/" + tipo + "/selecionar?" + "propriedade=" + tipo + nome + "&sigla=" + URLEncoder.encode(sigla, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new ApplicationControllerException(e);
        }
    }

    @Path("/buscarSiga/{sigla}/{tipo}/{nome}")
    public void buscarSiga(String sigla, String tipo, String nome) throws ApplicationControllerException {
        try {
            result.redirectTo("/siga/app/" + tipo + "/buscar?" + "propriedade=" + tipo + nome + "&sigla=" + URLEncoder.encode(sigla, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new ApplicationControllerException(e);
        }
    }

    @Path("/exibirManualUsuario")
    public void exibirManualUsuario() {

    }

    @Path("/exibirManualUsuarioDeGabinete")
    public void exibirManualUsuarioDeGabinete() {

    }

    @Path("/gadget")
    public void gadget() {
        try {
            Long idOrgaoUsu = Long.valueOf(getTitular().getOrgaoUsuario().getIdOrgaoUsu());
            List<String[]> lista = new ArrayList<String[]>();

            if (equalsSearch(true, autorizacaoGI.ehAdministrador(), autorizacaoGI.ehAdministradorMissao(), autorizacaoGI.ehAdministradorMissaoPorComplexo())) {
                adicionarRequisicoesAdministrador(lista);
            }

            else if (equalsSearch(true, autorizacaoGI.ehAgente())) {
                adicionarRequisicoesAgente(idOrgaoUsu, lista);
            }

            else if (equalsSearch(true, autorizacaoGI.ehAprovador())) {
                adicionarRequisicoesAprovador(lista);
            }

            else if (equalsSearch(true, autorizacaoGI.ehAdministradorFrota())) {
                adicionarRequisicoesAdministradorFrota(idOrgaoUsu, lista);
            }

            else {
                adicionarRequisicoes(lista);
            }

            result.include("lista", lista);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private void adicionarRequisicoes(List<String[]> lista) throws ApplicationControllerException {
        List<RequisicaoTransporte> requisicoes;
        try {
            requisicoes = RequisicaoTransporte.listarParaAgendamento(getTitular().getOrgaoUsuario());
            Integer total = totalizarItemLista(requisicoes, "");
            if (!requisicoes.isEmpty()) {
                lista.add(adicionarItemLista("requisicoes.listar", "", "", "Requisi&ccedil;&otilde;es", total));
            }
        } catch (Exception e) {
            throw new ApplicationControllerException(e);
        }
    }

    private void adicionarRequisicoesAdministradorFrota(Long idOrgaoUsu, List<String[]> lista) {
        EstadoServico[] estados = { EstadoServico.AGENDADO, EstadoServico.INICIADO };
        String query = "cpOrgaoUsuario.idOrgaoUsu=? and (situacaoServico = ? or situacaoServico = ?)";
        List<ServicoVeiculo> servicos = ServicoVeiculo.AR.find(query, idOrgaoUsu, estados[0], estados[1]).fetch();

        for (EstadoServico item : estados) {
            String titulo = "Servi&ccedil;os " + (item.equals(EstadoServico.AGENDADO) ? "agendados" : "iniciados");
            Integer total = totalizarItemLista(servicos, item.getDescricao());
            if (total > 0) {
                lista.add(adicionarItemLista("servicosVeiculo.listarfiltrado", "estado", item.getDescricao(), titulo, total));
            }
        }
    }

    private void adicionarRequisicoesAprovador(List<String[]> lista) {
        EstadoRequisicao estado = EstadoRequisicao.ABERTA;
        List<RequisicaoTransporte> requisicoes = RequisicaoTransporte.listar(estado);
        if (!requisicoes.isEmpty()) {
            lista.add(adicionarItemLista("requisicoes.listarfiltrado", "estadoRequisicao", estado.getDescricao(), "Requisi&ccedil;&otilde;es a autorizar", requisicoes.size()));
        }
    }

    private void adicionarRequisicoesAgente(Long idOrgaoUsu, List<String[]> lista) {
        String titulo;
        int total;
        Long idCondutor = Condutor.recuperarLogado(getTitular(), getTitular().getOrgaoUsuario()).getId();
        EstadoMissao[] estados = { EstadoMissao.PROGRAMADA, EstadoMissao.INICIADA };
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

    private void adicionarRequisicoesAdministrador(List<String[]> lista) {
        String titulo;
        int total;
        EstadoRequisicao[] estados = { EstadoRequisicao.AUTORIZADA, EstadoRequisicao.NAOATENDIDA };
        List<RequisicaoTransporte> requisicoes = RequisicaoTransporte.listar(estados);

        for (EstadoRequisicao item : estados) {
            titulo = "Requisi&ccedil;&otilde;es " + (item.equals(EstadoRequisicao.AUTORIZADA) ? "autorizadas" : "nao atendidas");
            total = totalizarItemLista(requisicoes, item.getDescricao());
            if (total > 0) {
                lista.add(adicionarItemLista("requisicoes.listarfiltrado", "estadoRequisicao", item.getDescricao(), titulo, total));
            }
        }
    }

    private String montarSigla(String sigla) {
        String retorno = "";
        String siglaUpper = sigla.trim().toUpperCase();

        // substitui o penúltimo "-" por "/" caso não tenha nenhum.
        if (StringUtils.countMatches(siglaUpper, "-") > 0 && StringUtils.countMatches(siglaUpper, "/") == 0) {
            int total = StringUtils.countMatches(siglaUpper, "-");
            int posicao = StringUtils.ordinalIndexOf(siglaUpper, "-", total - 1);
            StringBuilder strSigla = new StringBuilder(siglaUpper);
            strSigla.setCharAt(posicao, "/".charAt(0));
            siglaUpper = strSigla.toString();
        }

        // Formato TRF2-TP-2014/00001-R
        final Pattern p = Pattern.compile("^?([A-Z]{2})?-?(TP{1})-?([0-9]{4})?/?([0-9]{1,5})-?([MSR]{1})?$");
        final Matcher m = p.matcher(siglaUpper);

        if (m.find()) {
            if (m.group(1) != null) {
                retorno = m.group(1).toString();
            } else {
                retorno = getCadastrante().getOrgaoUsuario().getAcronimoOrgaoUsu().replace("-", "").toString();
            }

            retorno = retorno + "-" + m.group(2).toString();

            Calendar c1 = Calendar.getInstance();

            if (m.group(3) != null && m.group(4) != null && (m.group(3).toString() + m.group(5).toString()).length() <= 5) {
                c1.set(Calendar.YEAR, Integer.valueOf(m.group(3)));
                c1.set(Calendar.DAY_OF_YEAR, 1);
            }

            retorno = retorno + "-" + String.format("%04d", c1.get(Calendar.YEAR));

            if (m.group(3) != null && m.group(4) != null) {
                if ((m.group(3).toString() + m.group(4).toString()).length() <= 5) {
                    retorno = retorno + "/" + String.format("%05d", Integer.parseInt(m.group(3).toString() + m.group(4).toString()));
                } else {
                    retorno = retorno + "/" + String.format("%05d", Integer.parseInt(m.group(4)));
                }
            } else if (m.group(3) != null && m.group(4) == null) {
                retorno = retorno + "/" + String.format("%05d", Integer.parseInt(m.group(3)));
            } else if (m.group(4) != null) {
                retorno = retorno + "/" + String.format("%05d", Integer.parseInt(m.group(4)));
            } else {
                retorno = retorno + "/0";
            }

            retorno = retorno + "-" + m.group(5);

            return retorno;
        }

        return siglaUpper;
    }

    private String[] adicionarItemLista(String template, String parametro, String descricao, String titulo, int total) {
        String[][] itemLista = new String[1][3];
        itemLista[0][0] = obterCaminhoUrl(template, parametro, descricao);
        itemLista[0][1] = titulo;
        itemLista[0][2] = String.valueOf(total);
        return itemLista[0];
    }

    private String obterCaminhoUrl(String template, String parametro, String valor) {
        FormataCaminhoDoContextoUrl formata = new FormataCaminhoDoContextoUrl();
        if (!"".equals(parametro) && !"".equals(valor)) {
            Map<String, Object> param = new HashMap<String, Object>();
            param.put(parametro, valor);
            return null;
            // return formata.retornarCaminhoContextoUrl(Router.reverse(template, param).url);
        } else {
            return null;
            // return formata.retornarCaminhoContextoUrl(Router.reverse(template).url);
        }
    }

    private Boolean equalsSearch(Object item, Object... search) {
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
            if (!lista.isEmpty()) {
                List<T> itemFiltrado = Lists.newArrayList(Iterables.filter(lista, new PredicateImplementation<T>(descricao)));
                total = itemFiltrado.size();
            }
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        return total;
    }
}