package br.gov.jfrj.siga.tp.vraptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.validator.I18nMessage;
import br.com.caelum.vraptor.view.Results;
import br.gov.jfrj.siga.base.AplicacaoException;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.tp.enums.Template;
import br.gov.jfrj.siga.tp.model.Andamento;
import br.gov.jfrj.siga.tp.model.Avaria;
import br.gov.jfrj.siga.tp.model.EstadoRequisicao;
import br.gov.jfrj.siga.tp.model.EstadoServico;
import br.gov.jfrj.siga.tp.model.Missao;
import br.gov.jfrj.siga.tp.model.RequisicaoTransporte;
import br.gov.jfrj.siga.tp.model.ServicoVeiculo;
import br.gov.jfrj.siga.tp.model.TiposDeServico;
import br.gov.jfrj.siga.tp.model.TpDao;
import br.gov.jfrj.siga.tp.model.Veiculo;
import br.gov.jfrj.siga.tp.util.SigaTpException;
import br.gov.jfrj.siga.vraptor.SigaObjects;

@Resource
@Path("/app/servicoveiculo/")
public class ServicoVeiculoController extends TpController {

    private static final String SERVICO_STR = "servico";

    public ServicoVeiculoController(HttpServletRequest request, Result result, Validator validator, SigaObjects so, EntityManager em) {
        super(request, result, TpDao.getInstance(), validator, so, em);
    }

    // @RoleAdmin
    // @RoleAdminFrota
    @Path("incluir")
    public void incluir() throws Exception {
        ServicoVeiculo servico = new ServicoVeiculo();
        EstadoServico estadoServico = EstadoServico.AGENDADO;
        MenuMontador.instance(result).recuperarMenuServicoVeiculo(servico.getId(), estadoServico);
        montarCombos(servico.getId());
        result.include(SERVICO_STR, servico);
        result.include("estadoServico", estadoServico);
    }

    // @RoleAdmin
    // @RoleAdminFrota
    @Path("cancelar")
    public void cancelar() throws Exception {
        ServicoVeiculo servico = new ServicoVeiculo();
        EstadoServico estadoServico = EstadoServico.CANCELADO;
        MenuMontador.instance(result).recuperarMenuServicoVeiculo(servico.getId(), estadoServico);
        montarCombos(servico.getId());
        result.include(servico);
        result.include(estadoServico);
    }

    // @RoleAdmin
    // @RoleAdminFrota
    @Path("salvar")
    public void salvar(@Valid ServicoVeiculo servico, List<Avaria> avarias) throws Exception {
        DpPessoa dpPessoa = getCadastrante();
        servico.setCpOrgaoUsuario(getTitular().getOrgaoUsuario());
        servico.setSequence(servico.getCpOrgaoUsuario());
        servico.setExecutor(dpPessoa);
        Template template;
        boolean novoServico = false;

        if (servico.getId() == 0) {
            novoServico = true;
            servico.setDataHora(Calendar.getInstance());
            servico.setSituacaoServico(EstadoServico.AGENDADO);
            servico.setDataHoraInicio(servico.getDataHoraInicioPrevisto());
            servico.setDataHoraFim(servico.getDataHoraFimPrevisto());
            template = Template.INCLUIR;
        } else {
            servico.setUltimaAlteracao(Calendar.getInstance());
            template = Template.EDITAR;
        }

        if ((servico.getSituacaoServico() == EstadoServico.REALIZADO) && avarias != null && !avarias.isEmpty()) {
            for (Avaria avaria : avarias) {
                avaria = Avaria.AR.findById(avaria.getId());
                avaria.setDataDeSolucao(Calendar.getInstance());
                avaria.save();
                redirecionarSeErroAoSalvar(servico, template);
            }
        }

        if (servico.getDataHoraInicioPrevisto() != null && servico.getDataHoraFimPrevisto() != null
                && (!servico.ordemDeDatasPrevistas(servico.getDataHoraInicioPrevisto(), servico.getDataHoraFimPrevisto()))) {
            validator.add(new I18nMessage("dataHoraFimPrevisto", "servicosVeiculo.dataHoraFimPrevisto.validation"));
            redirecionarSeErroAoSalvar(servico, template);
        }

        if (servico.getSituacaoServico() == EstadoServico.CANCELADO) {
            verificarMotivoCancelamentoPreenchido(servico);
            if (servico.getTiposDeServico().getGeraRequisicao()) {
                String descricaoRequisicao = "Cancelado pelo servico " + servico.getSequence();
                servico.getRequisicaoTransporte().cancelar(dpPessoa, descricaoRequisicao);
            }
            redirecionarSeErroAoSalvar(servico, template);
        }

        if (servico.getDataHoraInicio() != null && servico.getDataHoraFim() != null) {
            if (!servico.ordemDeDatasPrevistas(servico.getDataHoraInicio(), servico.getDataHoraFim())) {
                validator.add(new I18nMessage("dataHoraFim", "servicosVeiculo.dataHoraFim.validation"));
            }
            redirecionarSeErroAoSalvar(servico, template);
        }

        if (novoServico && servico.getTiposDeServico().getGeraRequisicao()) {
            verificarDescricaoPreenchida(servico);
            redirecionarSeErroAoSalvar(servico, template);

            List<Missao> missoes = Missao.retornarMissoes("veiculo.id", servico.getVeiculo().getId(), servico.getCpOrgaoUsuario().getId(), servico.getDataHoraInicio(), servico.getDataHoraFim());
            StringBuilder listaMissoes = new StringBuilder();

            String delimitador = "";
            for (Missao item : missoes) {
                listaMissoes.append(delimitador);
                listaMissoes.append(item.getSequence());
                delimitador = ",";
            }

            error(!missoes.isEmpty(), "LinkErroVeiculo", listaMissoes.toString());

            redirecionarSeErroAoSalvar(servico, template);

            servico.setRequisicaoTransporte(gravarRequisicao(servico));

            String descricaoRequisicao = "Aberta para o servico " + servico.getSequence();
            gravarAndamentosRequisicao(EstadoRequisicao.ABERTA, dpPessoa, descricaoRequisicao, servico.getRequisicaoTransporte());

            descricaoRequisicao = "Autorizada para o servico " + servico.getSequence();
            gravarAndamentosRequisicao(EstadoRequisicao.AUTORIZADA, dpPessoa, descricaoRequisicao, servico.getRequisicaoTransporte());
        }

        servico.save();
        montarCombos(servico.getId());
        result.redirectTo(this).listarFiltrado(servico.getSituacaoServico());
    }

    @Path("listar")
    public void listar() {
        CpOrgaoUsuario cpOrgaoUsuario = getTitular().getOrgaoUsuario();
        List<ServicoVeiculo> servicos = ServicoVeiculo.AR.find("cpOrgaoUsuario", cpOrgaoUsuario).fetch();
        EstadoServico situacaoServico = EstadoServico.AGENDADO;
        MenuMontador.instance(result).recuperarMenuServicosVeiculo(null);
        Collections.sort(servicos);
        result.include("servicos", servicos);
        result.include("situacaoServico", situacaoServico);
    }

    @Path({ "listarFiltrado/{estado}", "listarFiltrado" })
    public void listarFiltrado(EstadoServico parametroEstado) {
        EstadoServico estado = null != parametroEstado ? parametroEstado : EstadoServico.AGENDADO;
        CpOrgaoUsuario cpOrgaoUsuario = getTitular().getOrgaoUsuario();
        List<ServicoVeiculo> servicos = ServicoVeiculo.AR.find("cpOrgaoUsuario=? and situacaoServico = ?", cpOrgaoUsuario, estado).fetch();
        EstadoServico situacaoServico = EstadoServico.AGENDADO;
        MenuMontador.instance(result).recuperarMenuServicosVeiculo(estado);
        result.include("servicos", servicos);
        result.include("situacaoServico", situacaoServico);
        result.use(Results.page()).of(ServicoVeiculoController.class).listar();
    }

    private void verificarMotivoCancelamentoPreenchido(ServicoVeiculo servico) {
        Boolean erro = false;

        try {
            if (servico.getMotivoCancelamento().isEmpty()) {
                erro = true;
            }
        } catch (NullPointerException ex) {
            erro = true;
        }

        if (erro) {
            validator.add(new I18nMessage("motivoCancelamento", "servicosVeiculo.motivoCancelamento.validation"));
        }
    }

    private void verificarDescricaoPreenchida(ServicoVeiculo servico) {
        Boolean erro = false;

        try {
            if (servico.getDescricao().isEmpty()) {
                erro = true;
            }
        } catch (NullPointerException ex) {
            erro = true;
        }

        if (erro) {
            validator.add(new I18nMessage("descricao", "servicoVeiculo.descricao.validation"));
        }
    }

    // @RoleAdmin
    // @RoleAdminFrota
    @Path("editar/{id}")
    public void editar(Long id) throws Exception {
        ServicoVeiculo servico = ServicoVeiculo.AR.findById(id);
        montarCombos(servico.getId());
        MenuMontador.instance(result).recuperarMenuServicoVeiculo(id, servico.getSituacaoServico());
        result.include(SERVICO_STR, servico);
    }

    @Path("gravarAndamentosRequisicao/{estadoRequisicao}/{dpPessoa}")
    private void gravarAndamentosRequisicao(EstadoRequisicao estadoRequisicao, DpPessoa dpPessoa, String descricao, RequisicaoTransporte requisicaoTransporte) {
        Andamento andamento = new Andamento();
        andamento.setDescricao(descricao);
        andamento.setDataAndamento(Calendar.getInstance());
        andamento.setEstadoRequisicao(estadoRequisicao);
        andamento.setRequisicaoTransporte(requisicaoTransporte);
        andamento.setResponsavel(dpPessoa);
        andamento.save();

        requisicaoTransporte.addAndamento(andamento);
    }

    private RequisicaoTransporte gravarRequisicao(ServicoVeiculo servico) throws Exception {
        RequisicaoTransporte requisicaoTransporte = new RequisicaoTransporte();
        requisicaoTransporte.setCpOrgaoUsuario(getTitular().getOrgaoUsuario());
        requisicaoTransporte.setSequence(requisicaoTransporte.getCpOrgaoUsuario());
        requisicaoTransporte.setSolicitante(getCadastrante());
        requisicaoTransporte.setDataHoraSaidaPrevista(servico.getDataHoraInicioPrevisto());
        requisicaoTransporte.setDataHoraRetornoPrevisto(servico.getDataHoraFimPrevisto());
        requisicaoTransporte.setFinalidade(servico.getDescricao());
        requisicaoTransporte.setPassageiros("Requisicao para Servico");
        requisicaoTransporte.setItinerarios("Requisicao para Servico");
        requisicaoTransporte.setCpComplexo(recuperarComplexoPadrao());

        if (requisicaoTransporte.getId() == 0) {
            requisicaoTransporte.setDataHora(Calendar.getInstance());
        }

        requisicaoTransporte.save();
        return requisicaoTransporte;
    }

    // @RoleAdmin
    // @RoleAdminFrota
    @Path("excluir/{id}")
    public void excluir(Long id) throws Exception {
        ServicoVeiculo servico = ServicoVeiculo.AR.findById(id);
        if (validator.hasErrors()) {
            return;
        }

        if (!servico.getTiposDeServico().getGeraRequisicao()) {
            servico.delete();
            result.redirectTo(ServicoVeiculoController.class).listar();
            return;
        }

        try {
            if (null != servico.getRequisicaoTransporte()) {
                servico.getRequisicaoTransporte().excluir(true);
            } else {
                throw new AplicacaoException("");
            }
        } catch (SigaTpException ex) {
            error(true, "requisicaoTransporte", ex.getMessage());
            redirecionarSeErroAoSalvar(servico, Template.LER);
        }
        result.redirectTo(ServicoVeiculoController.class).listar();
    }

    @Path("buscarServico/{popUp}/{sequence*}")
    public void buscarServico(Boolean popUp, String sequence) throws Exception {
        ServicoVeiculo servico = recuperarPelaSigla(sequence, popUp);
        result.include(SERVICO_STR, servico);
        result.forwardTo(ServicoVeiculoController.class).ler(servico.getId());
    }

    @Path("ler/{id}")
    public void ler(Long id) throws Exception {
        ServicoVeiculo servico = ServicoVeiculo.AR.findById(id);
        MenuMontador.instance(result).recuperarMenuServicoVeiculo(id, servico.getSituacaoServico());
        montarCombos(servico.getId());
        result.include(SERVICO_STR, servico);
    }

    private void redirecionarSeErroAoSalvar(ServicoVeiculo servico, Template template) throws Exception {
        if (validator.hasErrors()) {
            MenuMontador.instance(result).recuperarMenuServicosVeiculo(servico.getSituacaoServico());
            montarCombos(servico.getId());
            switch (template) {
            case LER:
                validator.onErrorUse(Results.page()).of(ServicoVeiculoController.class).ler(servico.getId());
                break;
            case EDITAR:
                validator.onErrorUse(Results.page()).of(ServicoVeiculoController.class).editar(servico.getId());
                break;
            case INCLUIR:
                validator.onErrorUse(Results.page()).of(ServicoVeiculoController.class).incluir();
                break;
            default:
                break;
            }
        }
    }

    private void montarCombos(Long id) throws Exception {
        List<Veiculo> veiculos = Veiculo.listarTodos(getTitular().getOrgaoUsuario());
        List<TiposDeServico> tiposDeServico = new ArrayList<TiposDeServico>(Arrays.asList(TiposDeServico.values()));
        List<EstadoServico> estadosServico = new ArrayList<EstadoServico>();
        List<Avaria> avarias = new ArrayList<Avaria>();

        if (id == 0) {
            estadosServico = (Arrays.asList(EstadoServico.AGENDADO));
        } else {
            ServicoVeiculo servico = ServicoVeiculo.AR.findById(id);

            avarias = Avaria.buscarPendentesPorVeiculo(servico.getVeiculo());

            if (servico.getSituacaoServico().equals(EstadoServico.AGENDADO)) {
                estadosServico = (Arrays.asList(EstadoServico.getValuesComboIniciarServico()));
            } else if (servico.getSituacaoServico().equals(EstadoServico.INICIADO)) {
                estadosServico = (Arrays.asList(EstadoServico.getValuesComboFinalizarServico()));
            } else {
                estadosServico = (Arrays.asList(servico.getSituacaoServico()));
            }
        }

        result.include("estadosServico", estadosServico);
        result.include("tiposDeServico", tiposDeServico);
        result.include("veiculos", veiculos);
        result.include("avarias", avarias);
    }

    protected ServicoVeiculo recuperarPelaSigla(String sigla, Boolean popUp) throws Exception {
        ServicoVeiculo servico = ServicoVeiculo.buscar(sigla);
        MenuMontador.instance(result).recuperarMenuServicoVeiculo(servico.getId(), servico.getSituacaoServico());
        if (popUp != null) {
            result.include("mostrarMenu", !popUp);
        } else {
            result.include("mostrarMenu", true);
        }

        return servico;
    }

}