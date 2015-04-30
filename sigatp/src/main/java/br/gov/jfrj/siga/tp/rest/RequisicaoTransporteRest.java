package br.gov.jfrj.siga.tp.rest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.gov.jfrj.siga.tp.model.FinalidadeRequisicao;
import br.gov.jfrj.siga.tp.model.RequisicaoTransporte;
import br.gov.jfrj.siga.tp.model.TipoDePassageiro;

public class RequisicaoTransporteRest {
	
	private RequisicaoTransporte requisicaoTransporte;
	
	public Long getId() {
		return requisicaoTransporte.id;
	}
	
	public String getSequence() {
		return requisicaoTransporte.getSequence();
	}
	
	public String getDataHoraSaidaPrevista() {
		SimpleDateFormat formatar = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		return formatar.format(requisicaoTransporte.dataHoraSaidaPrevista.getTime());
	}

	public String getDataHoraRetornoPrevisto() {
		if(requisicaoTransporte.dataHoraRetornoPrevisto == null) {
			return null;
		}
		SimpleDateFormat formatar = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		return formatar.format(requisicaoTransporte.dataHoraRetornoPrevisto.getTime());
	}
	
	public String getFinalidade() {
		return requisicaoTransporte.tipoFinalidade.getDescricao();
	}

	public static RequisicaoTransporteRest recuperar(Long id) throws Exception {
		RequisicaoTransporteRest retorno = new RequisicaoTransporteRest();
		retorno.requisicaoTransporte = RequisicaoTransporte.AR.findById(id);
		return retorno;
	}
	
	public static RequisicaoTransporteRest recuperarPelaSequence(String sequence) throws Exception {
		RequisicaoTransporteRest retorno = new RequisicaoTransporteRest();
		retorno.requisicaoTransporte = RequisicaoTransporte.buscar(sequence);
		return retorno;
	}
	
	public static RequisicaoTransporteRest recuperar(String sequence) throws Exception {
		RequisicaoTransporteRest retorno = new RequisicaoTransporteRest();
		retorno.requisicaoTransporte = RequisicaoTransporte.buscar(sequence);
		return retorno;
	}
	
	public String getDetalhesFinalidade() {
		return requisicaoTransporte.finalidade;
	}
	
	public String getPassageiros() {
		return requisicaoTransporte.passageiros;
	}
	
	public String[] getTiposDePassageiros() {
		List<String> retorno = new ArrayList<String>();
		for (Iterator<TipoDePassageiro> it = requisicaoTransporte.tiposDePassageiro.iterator(); it.hasNext();) {
			TipoDePassageiro tp = (TipoDePassageiro) it.next();
			retorno.add(tp.name());
		}
		return retorno.toArray(new String[retorno.size()]);
	}
	
	public Integer getNumeroDePassageiros() {
		return requisicaoTransporte.numeroDePassageiros;
	}

	public String getUltimoAndamento() {
		return requisicaoTransporte.getUltimoAndamento().estadoRequisicao.getDescricao();
	}
	
	public Long getIdInicialDpPessoaSolicitante() {
		return requisicaoTransporte.solicitante.getIdInicial();
	}
	
	// fim proxy propriedades
	
	// inicio procedimentos
	
	public static void converterParaRequisicao(RequisicaoTransporte requisicaoAPreencher, Map<String, String> map) throws Exception {
		requisicaoAPreencher.dataHoraSaidaPrevista = converterParaData(map.get("dataHoraSaidaPrevista"));
		requisicaoAPreencher.dataHoraRetornoPrevisto = converterParaData(map.get("dataHoraRetornoPrevisto"));
		requisicaoAPreencher.tipoFinalidade = FinalidadeRequisicao.buscar(map.get("finalidade"));
		requisicaoAPreencher.finalidade = map.get("detalhesFinalidade");
		requisicaoAPreencher.itinerarios = map.get("itinerarios");
		requisicaoAPreencher.tiposDePassageiro = converterParaTiposPassageiros(tratarTiposPassageiros(map.get("tiposDePassageiros")));
		requisicaoAPreencher.numeroDePassageiros = Integer.parseInt(map.get("numeroDePassageiros"));
		requisicaoAPreencher.passageiros = map.get("passageiros");
		
		if(requisicaoAPreencher.id.equals(new Long(0))) {
			requisicaoAPreencher.idSolicitante = Long.parseLong(map.get("idInicialDpPessoaSolicitante"));
		}
		
		requisicaoAPreencher.origemExterna = true;
	}
	
	private static List<TipoDePassageiro> converterParaTiposPassageiros(String[] tipos) {
		List<TipoDePassageiro> retorno = new ArrayList<TipoDePassageiro>();
		
		for (int i = 0; i < tipos.length; i++) {
			retorno.add(TipoDePassageiro.valueOf(tipos[i]));
		}
		
		return retorno;
	}
	
	private static Calendar converterParaData(String dataTexto) throws ParseException {
		if((dataTexto == null) || (dataTexto.equals("null"))) {
			return null;
		}
		Calendar retorno = Calendar.getInstance();
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		retorno.setTime(formato.parse(dataTexto));
		
		return retorno;
	}

	private static String[] tratarTiposPassageiros(String tipos) {
		return tipos
				.replace("[", "")
				.replace("]", "")
				.replace(" ", "")
				.replace("\"", "")
				.split(",");
	}

	public static RequisicaoTransporteRest recuperarEConverter(long idABuscar, Map<String, String> map) throws Exception {
		RequisicaoTransporte requisicaoARecuperar = recuperar(idABuscar).requisicaoTransporte;
		converterParaRequisicao(requisicaoARecuperar, map);
		RequisicaoTransporteRest retorno = new RequisicaoTransporteRest();
		retorno.requisicaoTransporte = requisicaoARecuperar;
		return retorno;
	}

	public static RequisicaoTransporte recuperarRequisicao(RequisicaoTransporteRest entrada) {
		return entrada.requisicaoTransporte;
	}
	


}
