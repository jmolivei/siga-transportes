package br.gov.jfrj.siga.tp.vraptor;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.type.TypeReference;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.validator.I18nMessage;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.view.Results;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.tp.model.Andamento;
import br.gov.jfrj.siga.tp.model.EstadoRequisicao;
import br.gov.jfrj.siga.tp.model.FinalidadeRequisicao;
import br.gov.jfrj.siga.tp.model.RequisicaoTransporte;
import br.gov.jfrj.siga.tp.model.TpDao;
import br.gov.jfrj.siga.tp.rest.RequisicaoTransporteRest;
import br.gov.jfrj.siga.vraptor.SigaObjects;
import controllers.AutorizacaoGIAntigo;

@Resource
@Path("/app/requisicaoRest")
public class RequisicaoRestController extends TpController {

    private static final String REQUISICOES_REST_REQ_NULL_EXCEPTION = "requisicoesRest.reqNull.exception";
    private static final String REQUISICAO_REST = "requisicaoRest";

    public RequisicaoRestController(HttpServletRequest request, Result result,
            CpDao dao, Validator validator, SigaObjects so, EntityManager em) {
        super(request, result, TpDao.getInstance(), validator, so, em);
    }

    @Path("/ver/{id}")
    public void ver(Long id) throws Exception {
        RequisicaoTransporteRest req = RequisicaoTransporteRest.recuperar(id);
        
        if(req == null) 
            throw new Exception(new I18nMessage(REQUISICAO_REST, REQUISICOES_REST_REQ_NULL_EXCEPTION).getMessage());
        
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        
        String json = ow.writeValueAsString(req);
        
        result.use(Results.http()).body(json);
    }
    
    @Path("/buscar/{codigoDocumento*}")
    public void buscar(String codigoDocumento) throws Exception {
        RequisicaoTransporteRest req = RequisicaoTransporteRest.recuperarPelaSequence(codigoDocumento);
        
        if(req == null) 
            throw new Exception(new I18nMessage(REQUISICAO_REST, REQUISICOES_REST_REQ_NULL_EXCEPTION).getMessage());
        
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        
        String json = ow.writeValueAsString(req);
        
        result.use(Results.http()).body(json);
    }
    
    @Path("/estaAutorizada/{codigoDocumento*}")
    public void estaAutorizada(String codigoDocumento) throws Exception {
        result.use(Results.http()).body(estaNesseEstado(codigoDocumento, EstadoRequisicao.AUTORIZADA).toString());
    }
    
    @Path("/estaRejeitada/{codigoDocumento*}")
    public void estaRejeitada(String codigoDocumento) throws Exception {
        result.use(Results.http()).body(estaNesseEstado(codigoDocumento, EstadoRequisicao.REJEITADA).toString());
    }
    
    @Path("/estaAtendida/{codigoDocumento*}")
    public void estaAtendida(String codigoDocumento) throws Exception {
        result.use(Results.http()).body(estaNesseEstado(codigoDocumento, EstadoRequisicao.ATENDIDA).toString());
    }
    
    private Boolean estaNesseEstado(String sequence, EstadoRequisicao estadoRequisicao) throws Exception {
        RequisicaoTransporteRest req = RequisicaoTransporteRest.recuperarPelaSequence(sequence);
        
        if(req == null) 
            throw new Exception(new I18nMessage(REQUISICAO_REST, REQUISICOES_REST_REQ_NULL_EXCEPTION).getMessage());
        
        return req.getUltimoAndamento().equals(estadoRequisicao.getDescricao());
    }
    
    private void validarRequisicao(RequisicaoTransporte requisicaoTransporte) throws Exception {
        if(requisicaoTransporte.getDataHoraRetornoPrevisto() != null && (requisicaoTransporte.getDataHoraSaidaPrevista() != null) && (!requisicaoTransporte.ordemDeDatasCorreta()))
            validator.add(new I18nMessage("dataHoraRetornoPrevisto", "requisicaoTransporte.dataHoraRetornoPrevisto.validation"));
        
        if((requisicaoTransporte.getTiposDePassageiro() == null) || (requisicaoTransporte.getTiposDePassageiro().isEmpty()))
            validator.add(new I18nMessage("tiposDePassageiros", "requisicaoTransporte.tiposDePassageiros.validation"));
        
        if(requisicaoTransporte.getPassageiros() == null || requisicaoTransporte.getPassageiros().isEmpty()) 
            validator.add(new I18nMessage("passageiros", "requisicaoTransporte.passageirosNomeEContato.validation"));
        
        if(requisicaoTransporte.getTipoFinalidade().ehOutra() && requisicaoTransporte.getFinalidade().isEmpty())
            validator.add(new I18nMessage("finalidade", "requisicaoTransporte.finalidade.validation"));
    }

    @Path("/incluir/{body*}")
    public void incluir(String body) throws Exception {
        RequisicaoTransporte req = new RequisicaoTransporte();
     
        Map<String, String> map = transformarDadosRecebidos(body);
        
        RequisicaoTransporteRest.converterParaRequisicao(req, map);
        
        validarRequisicao(req);
        
        if(validator.hasErrors()) {
            // exibir o erro na saida
            List<Message> listaErros = validator.getErrors();
            Error[] erros = listaErros.toArray(new Error[listaErros.size()]);
            
            StringBuffer mensagemErro = new StringBuffer("Erro! ")
                    .append(new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(erros));
            result.use(Results.http()).body(mensagemErro.toString());
        }
        
        DpPessoa dpPessoa = recuperaPessoa(req.getIdSolicitante());
        
        req.setSolicitante(dpPessoa);
        req.setCpOrgaoUsuario(dpPessoa.getOrgaoUsuario());
        req.setCpComplexo(AutorizacaoGIAntigo.recuperarComplexoPadrao(dpPessoa));
        
        checarSolicitante(req, dpPessoa, true);
        
        req.setDataHora(Calendar.getInstance());
        req.setSequence(req.getCpOrgaoUsuario());
        
        req.setSolicitante(dpPessoa);
        
        hackSimularUsuarioLogadoParaRevInfo(dpPessoa);
        
        req.save();
        
        //gravar andamento
        req.refresh();
        Andamento andamento = new Andamento();
        andamento.setDescricao("NOVA REQUISICAO");
        andamento.setDataAndamento(Calendar.getInstance());
        andamento.setEstadoRequisicao(EstadoRequisicao.ABERTA);
        andamento.setRequisicaoTransporte(req);
        andamento.setResponsavel(dpPessoa);
        andamento.save();
        
        ver(req.getId());
    }

    private Map<String, String> transformarDadosRecebidos(String body)
            throws IOException {
        Map<String,String> map = new HashMap<String,String>();
        ObjectMapper mapper = new ObjectMapper();
        map = mapper.readValue(body, new TypeReference<HashMap<String,String>>(){
        });
        return map;
    }
    
    private void hackSimularUsuarioLogadoParaRevInfo(DpPessoa usuario) {
        result.include("cadastrante", usuario);
    }

    @Path("/alterar/{body*}")
    public void alterar(String body) throws Exception {
        Map<String, String> map = transformarDadosRecebidos(body);
        
        long idABuscar = Long.parseLong(map.get("id"));
        RequisicaoTransporteRest requisicaoRestAAlterar = RequisicaoTransporteRest.recuperarEConverter(idABuscar, map);
        RequisicaoTransporte requisicaoAAlterar = RequisicaoTransporteRest.recuperarRequisicao(requisicaoRestAAlterar);
        
        validarRequisicao(requisicaoAAlterar);
        
        if(validator.hasErrors()) {
            List<Message> listaErros = validator.getErrors();
            Error[] erros = listaErros.toArray(new Error[listaErros.size()]);
            
            StringBuffer mensagemErro = new StringBuffer("Erro! ")
                    .append(new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(erros));
            result.use(Results.http()).body(mensagemErro.toString());
        }
        
        DpPessoa dpPessoa = recuperaPessoa(Long.parseLong(map.get("idInicialDpPessoaSolicitante")));
        
        checarSolicitante(requisicaoAAlterar, dpPessoa, true);
        
        hackSimularUsuarioLogadoParaRevInfo(dpPessoa);
        
        requisicaoAAlterar.save();
        
        ver(requisicaoAAlterar.getId());
    }
    
    @Path("/excluir")
    public void excluir() throws Exception {
        result.use(Results.http()).body("Opera&ccedil;&atilde;o n&atilde;o suportada");
    }
    
    protected void carregarFinalidades() {
        result.include("finalidedes", FinalidadeRequisicao.listarTodos());
    }

    private void checarSolicitante(RequisicaoTransporte req, DpPessoa pessoaAcesso, Boolean escrita) throws Exception {
        if (! pessoaAcesso.getIdInicial().equals(req.getIdSolicitante()) && escrita) 
            throw new Exception(new I18nMessage(REQUISICAO_REST, "requisicoes.checarSolicitante.exception").getMessage());
    }

    private DpPessoa recuperaPessoa(Long idSolicitante) throws Exception {
        DpPessoa dpPessoa = DpPessoa.AR.findById(idSolicitante);
        return  DpPessoa.AR.find("idPessoaIni = ? and dataFimPessoa = null",dpPessoa.getIdInicial()).first();
    }
}
