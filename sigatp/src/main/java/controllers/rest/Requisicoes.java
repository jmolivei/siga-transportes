package controllers.rest;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.type.TypeReference;

import play.data.validation.Error;
import play.data.validation.Validation;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Http;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.tp.model.Andamento;
import br.gov.jfrj.siga.tp.model.TpDao;
import br.gov.jfrj.siga.tp.model.EstadoRequisicao;
import br.gov.jfrj.siga.tp.model.FinalidadeRequisicao;
import br.gov.jfrj.siga.tp.model.RequisicaoTransporte;
import br.gov.jfrj.siga.tp.rest.RequisicaoTransporteRest;
import controllers.AutorizacaoGIAntigo;

public class Requisicoes extends Controller {
	
	public static void ver(Long id) throws Exception {
		RequisicaoTransporteRest req = RequisicaoTransporteRest.recuperar(id);
		
		if(req == null) {
			throw new Exception(Messages.get("requisicoesRest.reqNull.exception"));
		}
		
		//ObjectWriter
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		
		String json = ow.writeValueAsString(req);
		
		renderText(json);
	}
	
	public static void buscar(String codigoDocumento) throws Exception {
		RequisicaoTransporteRest req = RequisicaoTransporteRest.recuperarPelaSequence(codigoDocumento);
		
		if(req == null) {
			throw new Exception(Messages.get("requisicoesRest.reqNull.exception"));
		}
		
		//ObjectWriter
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		
		String json = ow.writeValueAsString(req);
		
		renderText(json);
	}
	
	public static void estaAutorizada(String codigoDocumento) throws Exception {
		renderText(estaNesseEstado(codigoDocumento,  Http.Request.current().actionMethod));
	}
	
	public static void estaRejeitada(String codigoDocumento) throws Exception {
		renderText(estaNesseEstado(codigoDocumento,  Http.Request.current().actionMethod));
	}
	
	public static void estaAtendida(String codigoDocumento) throws Exception {
		renderText(estaNesseEstado(codigoDocumento,  Http.Request.current().actionMethod));
	}
	
	private static Boolean estaNesseEstado(String sequence, String acaoExecutada) throws Exception {
		RequisicaoTransporteRest req = RequisicaoTransporteRest.recuperarPelaSequence(sequence);
		
		if(req == null) {
			throw new Exception(Messages.get("requisicoesRest.reqNull.exception"));
		}
		
		acaoExecutada = (acaoExecutada.replaceAll("esta","").toUpperCase());
		
		Boolean json = (req.getUltimoAndamento().equals(acaoExecutada));
		
		return json;
	}
	
	
	
	private static void validarRequisicao(RequisicaoTransporte requisicaoTransporte) throws Exception {
		
		if(requisicaoTransporte.dataHoraRetornoPrevisto != null) {
			if ((requisicaoTransporte.dataHoraSaidaPrevista != null) && (!requisicaoTransporte.ordemDeDatasCorreta())){
				Validation.addError("dataHoraRetornoPrevisto", "requisicaoTransporte.dataHoraRetornoPrevisto.validation");
			}
		}
		
		if((requisicaoTransporte.tiposDePassageiro == null) || (requisicaoTransporte.tiposDePassageiro.isEmpty())) {
			Validation.addError("tiposDePassageiros", "requisicaoTransporte.tiposDePassageiros.validation");
		}
		
		if(requisicaoTransporte.passageiros == null || requisicaoTransporte.passageiros.isEmpty()) {
			Validation.addError("passageiros", "requisicaoTransporte.passageirosNomeEContato.validation");
		}
		
		if(requisicaoTransporte.tipoFinalidade.ehOutra() && requisicaoTransporte.finalidade.isEmpty()) {
			Validation.addError("finalidade", "requisicaoTransporte.finalidade.validation");
		}
		
		validation.valid(requisicaoTransporte);

	}

	public static void incluir(String body) throws Exception {
		RequisicaoTransporte req = new RequisicaoTransporte();
	 
		Map<String, String> map = transformarDadosRecebidos(body);
		
		RequisicaoTransporteRest.converterParaRequisicao(req, map);
		
		validarRequisicao(req);
		
		if(Validation.hasErrors()) {
			// exibir o erro na saida
			List<Error> listaErros = Validation.errors();
			Error[] erros = listaErros.toArray(new Error[listaErros.size()]);
			
			StringBuffer mensagemErro = new StringBuffer();
			mensagemErro.append("Erro! ");
			mensagemErro.append(new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(erros));
			renderText(mensagemErro.toString());
			return; //precisa?
		}
		
		DpPessoa dpPessoa = recuperaPessoa(req.idSolicitante);
		
		req.solicitante = dpPessoa;
		req.cpOrgaoUsuario = dpPessoa.getOrgaoUsuario();
		req.cpComplexo = AutorizacaoGIAntigo.recuperarComplexoPadrao(dpPessoa);
		
		checarSolicitante(req, dpPessoa, true);
		
		req.dataHora = Calendar.getInstance();
		req.setSequence(req.cpOrgaoUsuario);
		
		req.solicitante = dpPessoa;
		
		hackSimularUsuarioLogadoParaRevInfo(dpPessoa);
		
		req.save();
		
		//gravar andamento
		req.refresh();
		Andamento andamento = new Andamento();
		andamento.descricao = "NOVA REQUISICAO";
		andamento.dataAndamento = Calendar.getInstance();
		andamento.estadoRequisicao = EstadoRequisicao.ABERTA;
		andamento.requisicaoTransporte = req;
		andamento.responsavel = dpPessoa;
		andamento.save();
		
		ver(req.id);
	}

	private static Map<String, String> transformarDadosRecebidos(String body)
			throws IOException, JsonParseException, JsonMappingException {
		Map<String,String> map = new HashMap<String,String>();
		ObjectMapper mapper = new ObjectMapper();
		map = mapper.readValue(body, new TypeReference<HashMap<String,String>>(){});
		return map;
	}
	
	private static void hackSimularUsuarioLogadoParaRevInfo(DpPessoa usuario) {
		renderArgs.put("cadastrante", usuario);
	}

	public static void alterar(Long id, String body) throws Exception {
		Map<String, String> map = transformarDadosRecebidos(body);
		
		long idABuscar = Long.parseLong(map.get("id"));
		RequisicaoTransporteRest requisicaoRestAAlterar = RequisicaoTransporteRest.recuperarEConverter(idABuscar, map);
		RequisicaoTransporte requisicaoAAlterar = RequisicaoTransporteRest.recuperarRequisicao(requisicaoRestAAlterar);
		
		validarRequisicao(requisicaoAAlterar);
		
		if(Validation.hasErrors()) {
			// exibir o erro na saida
			List<Error> listaErros = Validation.errors();
			Error[] erros = listaErros.toArray(new Error[listaErros.size()]);
			
			StringBuffer mensagemErro = new StringBuffer();
			mensagemErro.append("Erro! ");
			mensagemErro.append(new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(erros));
			renderText(mensagemErro.toString());
			return; //precisa?
		}
		
		DpPessoa dpPessoa = recuperaPessoa(Long.parseLong(map.get("idInicialDpPessoaSolicitante")));
		
		checarSolicitante(requisicaoAAlterar, dpPessoa, true);
		
		hackSimularUsuarioLogadoParaRevInfo(dpPessoa);
		
		requisicaoAAlterar.save();
		
		ver(requisicaoAAlterar.id);
		
	}
	
	public static void excluir() throws Exception {
		renderText("Opera&ccedil;&atilde;o n&atilde;o suportada");
	}
	
	protected static void carregarFinalidades() {
		renderArgs.put("finalidades", FinalidadeRequisicao.listarTodos());
	}

	private static void checarSolicitante(RequisicaoTransporte req, DpPessoa pessoaAcesso, Boolean escrita) throws Exception {
		if (! pessoaAcesso.getIdInicial().equals(req.idSolicitante) && escrita) {
			throw new Exception(Messages.get("requisicoes.checarSolicitante.exception"));
		}
	}

	private static DpPessoa recuperaPessoa(Long idSolicitante) throws Exception {
		DpPessoa dpPessoa = DpPessoa.AR.findById(idSolicitante);
		return 	DpPessoa.AR.find("idPessoaIni = ? and dataFimPessoa = null",dpPessoa.getIdInicial()).first();
	}
}
