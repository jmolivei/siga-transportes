package controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import play.data.binding.As;
import play.mvc.Controller;
import play.mvc.With;
import br.gov.jfrj.siga.tp.model.Afastamento;
import br.gov.jfrj.siga.tp.model.DiaDaSemana;
import br.gov.jfrj.siga.tp.model.DiaDeTrabalho;
import br.gov.jfrj.siga.tp.model.EscalaDeTrabalho;
import br.gov.jfrj.siga.tp.model.Missao;
import br.gov.jfrj.siga.tp.model.Plantao;
import br.gov.jfrj.siga.tp.model.ServicoVeiculo;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@With(AutorizacaoGIAntigo.class)
public class Relatorios extends Controller {
	
	private static final int HORA_FINAL_EXPEDIENTE = 19;
	private static final int MINUTO_FINAL_EXPEDIENTE = 0;
	private static final int SEGUNDO_FINAL_EXPEDIENTE = 0;
	private static final int HORA_FINAL_DIA = 23;
	private static final int MINUTO_FINAL_DIA = 59;
	private static final int SEGUNDO_FINAL_DIA = 59;
	private static final int HORA_INICIAL_DIA = 0;
	private static final int MINUTO_INICIAL_DIA = 0;
	private static final int SEGUNDO_INICIAL_DIA = 0;
	

	public static void listarAgendaPorCondutorNoProximoDia(@As(lang={"*"}, value={"dd/MM/yyyy"}) Calendar dataPesquisa,Long idCondutor) throws ParseException{
		dataPesquisa.add(Calendar.DAY_OF_MONTH, 1);
		listarAgendaPorCondutor(dataPesquisa,idCondutor);
	}
	
	public static void listarAgendaPorCondutorNoDiaAnterior(@As(lang={"*"}, value={"dd/MM/yyyy"}) Calendar dataPesquisa,Long idCondutor) throws ParseException{
		dataPesquisa.add(Calendar.DAY_OF_MONTH, -1);
		listarAgendaPorCondutor(dataPesquisa,idCondutor);
	}
	
	public static void listarAgendaPorVeiculoNoProximoDia(@As(lang={"*"}, value={"dd/MM/yyyy"}) Calendar dataPesquisa,Long idVeiculo) throws ParseException{
		dataPesquisa.add(Calendar.DAY_OF_MONTH, 1);
		listarAgendaPorVeiculo(dataPesquisa,idVeiculo);
	}
	
	public static void listarAgendaPorVeiculoNoDiaAnterior( @As(lang={"*"}, value={"dd/MM/yyyy"}) Calendar dataPesquisa,Long idVeiculo) throws ParseException{
		dataPesquisa.add(Calendar.DAY_OF_MONTH, -1);
		listarAgendaPorVeiculo(dataPesquisa,idVeiculo);
	}
	
	public static void listarAgendaPorVeiculo(
			 @As(lang={"*"}, value={"dd/MM/yyyy"}) Calendar dataPesquisa,Long idVeiculo) throws ParseException {
	
		String registros = "";
		
		Calendar dataHoraPesquisa = Calendar.getInstance();
		SimpleDateFormat formatar = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		
		String strDataPesquisa=null;
		
		if (dataPesquisa != null) {
			dataHoraPesquisa = dataPesquisa;
		}

		strDataPesquisa = String.format("%02d",dataHoraPesquisa.get(Calendar.DAY_OF_MONTH)) + "/" + String.format("%02d",dataHoraPesquisa.get(Calendar.MONTH) + 1) + "/" + String.format("%04d",dataHoraPesquisa.get(Calendar.YEAR));
		dataHoraPesquisa.setTime(formatar.parse(strDataPesquisa + " 00:00"));
		
		List<Missao> missoes = Missao.buscarPorVeiculos(idVeiculo,strDataPesquisa);
		List<Missao> missoesFiltradas = filtrarPorOrgao(missoes, Missao.class);

		List<ServicoVeiculo> servicosVeiculos = ServicoVeiculo.buscarPorVeiculo(idVeiculo,strDataPesquisa);
		List<ServicoVeiculo> servicosFiltrados = filtrarPorOrgao(servicosVeiculos, ServicoVeiculo.class);
		
		//registros = gerarTimeLine(dataHoraPesquisa,"", new ArrayList<Afastamento>(),new ArrayList<Plantao>(), missoes, servicosVeiculos, "veiculo" );
		registros = gerarTimeLine(dataHoraPesquisa,"", new ArrayList<Afastamento>(),new ArrayList<Plantao>(), missoesFiltradas, servicosFiltrados, "veiculo" );
		
		renderArgs.put("dataPesquisa", dataHoraPesquisa);
		
		render(registros, idVeiculo);
	}
	
	public static void listarAgendaTodosCondutores() throws ParseException {
		listarAgendaPorCondutor(Calendar.getInstance(),null);
	}
	
	public static void listarAgendaTodosVeiculos() throws ParseException {
		listarAgendaPorVeiculo(Calendar.getInstance(),null);
	}

	public static void listarAgendaPorCondutor(@As(lang={"*"}, value={"dd/MM/yyyy"}) Calendar dataPesquisa,Long idCondutor) throws ParseException{
		
		String registros = "";
		
		Calendar dataHoraPesquisa = Calendar.getInstance();
		SimpleDateFormat formatar = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		
		String strDataPesquisa=null;
		
		if (dataPesquisa != null) {
			dataHoraPesquisa = dataPesquisa;
		}

		strDataPesquisa = String.format("%02d",dataHoraPesquisa.get(Calendar.DAY_OF_MONTH)) + "/" + String.format("%02d",dataHoraPesquisa.get(Calendar.MONTH) + 1) + "/" + String.format("%04d",dataHoraPesquisa.get(Calendar.YEAR));
		dataHoraPesquisa.setTime(formatar.parse(strDataPesquisa + " 00:00"));
		
		List<EscalaDeTrabalho>  escalas = EscalaDeTrabalho.buscarPorCondutores(idCondutor,strDataPesquisa);
		List<EscalaDeTrabalho> escalasFiltradas = filtrarPorOrgao(escalas, EscalaDeTrabalho.class);

		List<Afastamento>  afastamentos = Afastamento.buscarPorCondutores(idCondutor,strDataPesquisa);
		List<Afastamento> afastamentosFiltrados = filtrarPorOrgao(afastamentos, Afastamento.class);

		List<Plantao> plantoes = Plantao.buscarPorCondutores(idCondutor,strDataPesquisa);
		List<Plantao> plantoesFiltrados = filtrarPorOrgao(plantoes, Plantao.class); 

		List<Missao> missoes = Missao.buscarPorCondutores(idCondutor,strDataPesquisa);
		List<Missao> missoesFiltradas = filtrarPorOrgao(missoes, Missao.class);

		String delim = "";
		//for (EscalaDeTrabalho escala : escalas) {
		for (EscalaDeTrabalho escala : escalasFiltradas) {
		
			SimpleDateFormat formatar1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			String srtDataPesquisa = formatar1.format(dataHoraPesquisa.getTime());
			
			DiaDaSemana diaDePesquisa = DiaDaSemana.getDiaDaSemana(srtDataPesquisa);
			
			SimpleDateFormat formatoData1 = new SimpleDateFormat("yyyy,M,d,H,m,s");
			
			Calendar dataHoraInicioTemp = Calendar.getInstance();
			Calendar dataHoraFimTemp = recuperarDataEHora(escala.getDataVigenciaInicio(), HORA_FINAL_DIA, MINUTO_FINAL_DIA,SEGUNDO_FINAL_DIA);
			
			for (DiaDeTrabalho dia: escala.getDiasDeTrabalho()) {
				if (diaDePesquisa.isEquals(dia.getDiaEntrada())) {
					dataHoraInicioTemp = recuperarDataEHora(dataHoraPesquisa,dia.getHoraEntrada().get(Calendar.HOUR_OF_DAY), dia.getHoraEntrada().get(Calendar.MINUTE),dia.getHoraEntrada().get(Calendar.SECOND));
					if (diaDePesquisa.isEquals(dia.getDiaSaida())) {
						dataHoraFimTemp = recuperarDataEHora(dataHoraPesquisa, dia.getHoraSaida().get(Calendar.HOUR_OF_DAY), dia.getHoraSaida().get(Calendar.MINUTE),dia.getHoraSaida().get(Calendar.SECOND));
					} else {	
						dataHoraFimTemp = recuperarDataEHora(dataHoraPesquisa, HORA_FINAL_EXPEDIENTE, MINUTO_FINAL_EXPEDIENTE,SEGUNDO_FINAL_EXPEDIENTE);
					}
					
					registros += delim;			
					registros += "[ \'" + "Escalas" + "\', \'" + escala.getCondutor().getNome() + "\', new Date(" +	formatoData1.format(dataHoraInicioTemp.getTime()) + "), new Date(";
					registros += formatoData1.format(dataHoraFimTemp.getTime()) + ") ]";
					delim = ", ";
				}
			}
		}
		
		String registrosEscala=registros;
		//registros = gerarTimeLine(dataHoraPesquisa,registrosEscala, afastamentos, plantoes, missoes, new ArrayList<ServicoVeiculo>(), "condutor");
		registros = gerarTimeLine(dataHoraPesquisa,registrosEscala, afastamentosFiltrados, plantoesFiltrados, missoesFiltradas, new ArrayList<ServicoVeiculo>(), "condutor");
		
		renderArgs.put("dataPesquisa", dataHoraPesquisa);
		
	
		render(registros, idCondutor);
		
	}


	private static String gerarTimeLine(Calendar dataHoraPesquisa, String registros,
			List<Afastamento> afastamentos, List<Plantao> plantoes,
			List<Missao> missoes, List<ServicoVeiculo> servicosVeiculos, String entidade) {
		
		String delim = "";
		if (! registros.isEmpty()) 
			delim = ", ";
		
		for (Afastamento afastamento : afastamentos) {
			registros += delim;
			SimpleDateFormat formatoData = new SimpleDateFormat("yyyy,M,d,H,m,s");
			
			registros += "[ \'" + "Afastamentos" + "\', \'" + afastamento.getCondutor().getNome() + "\', new Date(" ;
			
			if (afastamento.getDataHoraInicio().get(Calendar.YEAR) == dataHoraPesquisa.get(Calendar.YEAR) &&
					afastamento.getDataHoraInicio().get(Calendar.MONTH) == dataHoraPesquisa.get(Calendar.MONTH) &&	
					afastamento.getDataHoraInicio().get(Calendar.DAY_OF_MONTH) == dataHoraPesquisa.get(Calendar.DAY_OF_MONTH) &&
					afastamento.getDataHoraInicio().after(dataHoraPesquisa)) {
				registros += formatoData.format(afastamento.getDataHoraInicio().getTime()) + "), new Date(";
			}
			else {
				Calendar dataHora = recuperarDataEHora(dataHoraPesquisa, HORA_INICIAL_DIA, MINUTO_INICIAL_DIA,SEGUNDO_INICIAL_DIA);
				registros += formatoData.format(dataHora.getTime()) + "), new Date(";				
			}
			
			if (afastamento.getDataHoraFim() != null){
				if (afastamento.getDataHoraFim().get(Calendar.YEAR) == dataHoraPesquisa.get(Calendar.YEAR) &&
					afastamento.getDataHoraFim().get(Calendar.MONTH) == dataHoraPesquisa.get(Calendar.MONTH) &&	
					afastamento.getDataHoraFim().get(Calendar.DAY_OF_MONTH) == dataHoraPesquisa.get(Calendar.DAY_OF_MONTH) &&
					afastamento.getDataHoraFim().after(dataHoraPesquisa)) {
					registros += formatoData.format(afastamento.getDataHoraFim().getTime()) + ") ]";
					delim = ", ";
					continue;
				}	
			}
			Calendar dataHora = recuperarDataEHora(dataHoraPesquisa, HORA_FINAL_EXPEDIENTE, MINUTO_FINAL_EXPEDIENTE,SEGUNDO_FINAL_EXPEDIENTE);
			registros += formatoData.format(dataHora.getTime()) + ") ]";
			delim = ", ";
		}

		for (Plantao plantao : plantoes) {
			registros += delim;
			SimpleDateFormat formatoData = new SimpleDateFormat("yyyy,M,d,H,m,s");
			
			registros += "[ \'" + "Plantoes" + "\', \'" + plantao.condutor.getNome() + "\', new Date(" ;
			
			if (plantao.dataHoraInicio.get(Calendar.YEAR) == dataHoraPesquisa.get(Calendar.YEAR) &&
					plantao.dataHoraInicio.get(Calendar.MONTH) == dataHoraPesquisa.get(Calendar.MONTH) &&	
					plantao.dataHoraInicio.get(Calendar.DAY_OF_MONTH) == dataHoraPesquisa.get(Calendar.DAY_OF_MONTH) &&
					plantao.dataHoraInicio.after(dataHoraPesquisa)) {
				registros += formatoData.format(plantao.dataHoraInicio.getTime()) + "), new Date(";
			}
			else {
				Calendar dataHora = recuperarDataEHora(dataHoraPesquisa, HORA_INICIAL_DIA, MINUTO_INICIAL_DIA,SEGUNDO_INICIAL_DIA);
				registros += formatoData.format(dataHora.getTime()) + "), new Date(";
				
			}
			
			if (plantao.dataHoraFim != null){
				if (plantao.dataHoraFim.get(Calendar.YEAR) == dataHoraPesquisa.get(Calendar.YEAR) &&
					plantao.dataHoraFim.get(Calendar.MONTH) == dataHoraPesquisa.get(Calendar.MONTH) &&	
					plantao.dataHoraFim.get(Calendar.DAY_OF_MONTH) == dataHoraPesquisa.get(Calendar.DAY_OF_MONTH) &&
					plantao.dataHoraFim.after(dataHoraPesquisa)) {
					registros += formatoData.format(plantao.dataHoraFim.getTime()) + ") ]";
					delim = ", ";
					continue;
				}	
			}
			Calendar dataHora = recuperarDataEHora(dataHoraPesquisa, HORA_FINAL_EXPEDIENTE, MINUTO_FINAL_EXPEDIENTE,SEGUNDO_FINAL_EXPEDIENTE);
			registros += formatoData.format(dataHora.getTime()) + ") ]";
			delim = ", ";
		}
		
		for (ServicoVeiculo servicoVeiculo : servicosVeiculos) {
			registros += delim;
			SimpleDateFormat formatoData = new SimpleDateFormat("yyyy,M,d,H,m,s");
			
			registros += "[ \'" + "Servicos" + "\', \'" + servicoVeiculo.veiculo.getPlaca() + "\', new Date(" ;
			
			if (servicoVeiculo.dataHoraInicio.get(Calendar.YEAR) == dataHoraPesquisa.get(Calendar.YEAR) &&
					servicoVeiculo.dataHoraInicio.get(Calendar.MONTH) == dataHoraPesquisa.get(Calendar.MONTH) &&	
					servicoVeiculo.dataHoraInicio.get(Calendar.DAY_OF_MONTH) == dataHoraPesquisa.get(Calendar.DAY_OF_MONTH) &&
					servicoVeiculo.dataHoraInicio.after(dataHoraPesquisa)) {
				registros += formatoData.format(servicoVeiculo.dataHoraInicio.getTime()) + "), new Date(";
			}
			else {
				Calendar dataHora = recuperarDataEHora(dataHoraPesquisa, HORA_INICIAL_DIA, MINUTO_INICIAL_DIA,SEGUNDO_INICIAL_DIA);
				registros += formatoData.format(dataHora.getTime()) + "), new Date(";
				
			}
			
			if (servicoVeiculo.dataHoraFim != null){
				if (servicoVeiculo.dataHoraFim.get(Calendar.YEAR) == dataHoraPesquisa.get(Calendar.YEAR) &&
					servicoVeiculo.dataHoraFim.get(Calendar.MONTH) == dataHoraPesquisa.get(Calendar.MONTH) &&	
					servicoVeiculo.dataHoraFim.get(Calendar.DAY_OF_MONTH) == dataHoraPesquisa.get(Calendar.DAY_OF_MONTH) &&
					servicoVeiculo.dataHoraFim.after(dataHoraPesquisa)) {
					registros += formatoData.format(servicoVeiculo.dataHoraFim.getTime()) + ") ]";
					delim = ", ";
					continue;
				}	
			}
			Calendar dataHora = recuperarDataEHora(dataHoraPesquisa, HORA_FINAL_EXPEDIENTE, MINUTO_FINAL_EXPEDIENTE,SEGUNDO_FINAL_EXPEDIENTE);
			registros += formatoData.format(dataHora.getTime()) + ") ]";
			delim = ", ";
		}

		String label="condutor";
		for (Missao missao : missoes) {
			registros += delim;
			SimpleDateFormat formatoData = new SimpleDateFormat("yyyy,M,d,H,m,s");
	
			if (entidade.equals("condutor")) {
				label = missao.getSequence() + "-" + missao.condutor.getNome();
			}
			else {
				label = missao.getSequence() + "-" + missao.veiculo.getPlaca();
			}
			
			registros += "[ \'" + "Missoes" + "\', \'" + label + "\', new Date(" ;
			
			if (missao.dataHoraSaida.get(Calendar.YEAR) == dataHoraPesquisa.get(Calendar.YEAR) &&
					missao.dataHoraSaida.get(Calendar.MONTH) == dataHoraPesquisa.get(Calendar.MONTH) &&	
					missao.dataHoraSaida.get(Calendar.DAY_OF_MONTH) == dataHoraPesquisa.get(Calendar.DAY_OF_MONTH) &&
					missao.dataHoraSaida.after(dataHoraPesquisa)) {
				registros += formatoData.format(missao.dataHoraSaida.getTime()) + "), new Date(";
			}
			else {
				Calendar dataHora = recuperarDataEHora(dataHoraPesquisa, HORA_INICIAL_DIA, MINUTO_INICIAL_DIA,SEGUNDO_INICIAL_DIA);
				registros += formatoData.format(dataHora.getTime()) + "), new Date(";
				
			}
			
			if (missao.dataHoraRetorno != null){
				if (missao.dataHoraRetorno.get(Calendar.YEAR) == dataHoraPesquisa.get(Calendar.YEAR) &&
					missao.dataHoraRetorno.get(Calendar.MONTH) == dataHoraPesquisa.get(Calendar.MONTH) &&	
					missao.dataHoraRetorno.get(Calendar.DAY_OF_MONTH) == dataHoraPesquisa.get(Calendar.DAY_OF_MONTH) &&
					missao.dataHoraRetorno.after(dataHoraPesquisa)) {
					registros += formatoData.format(missao.dataHoraRetorno.getTime()) + ") ]";
					delim = ", ";
					continue;
				}	
			}
			Calendar dataHora = recuperarDataEHora(dataHoraPesquisa, HORA_FINAL_EXPEDIENTE, MINUTO_FINAL_EXPEDIENTE,SEGUNDO_FINAL_EXPEDIENTE);
			registros += formatoData.format(dataHora.getTime()) + ") ]";
			delim = ", ";
		}
		return registros;
	}

	public static void listarMissoesEmAndamento() {
		List<Missao> missoes = Missao.buscarEmAndamento();
		List<Missao> missoesFiltradas = filtrarPorOrgao(missoes, Missao.class);
 		
		String registros = "";
		SimpleDateFormat formatoData = new SimpleDateFormat("yyyy,M,d,H,m,s");
		
//		for(int i=0; i < missoes.size(); i++){
		for(int i=0; i < missoesFiltradas.size(); i++){		
//			registros += "[ \'" + missoes.get(i).getSequence() + " <a href=\"a.htm\">t</a>\'"  + " , \'" + missoes.get(i).condutor.getNome() + "\', new Date(" +	formatoData.format(missoes.get(i).dataHoraSaida.getTime()) + "), new Date(";
			registros += "[ \'" + missoes.get(i).getSequence() + " - " + missoes.get(i).veiculo.getPlaca() + "\', \'" + missoes.get(i).condutor.getNome() + "\', new Date(" +	formatoData.format(missoes.get(i).dataHoraSaida.getTime()) + "), new Date(";
			
			
			if (missoes.get(i).dataHoraRetorno != null){
				registros += formatoData.format(missoes.get(i).dataHoraRetorno.getTime()) + ") ]";
			}
			else{
				Calendar dataHora = recuperarDataEHora(missoes.get(i).dataHoraSaida, HORA_FINAL_EXPEDIENTE, MINUTO_FINAL_EXPEDIENTE,SEGUNDO_FINAL_EXPEDIENTE);
				registros += formatoData.format(dataHora.getTime()) + ") ]";
			}
			if (i < missoes.size()-1) registros += ", ";
		}
		render(registros);
	}	
	
	public static void listarDadosDaMissao(Long id) throws Exception {
		Missao missao = Missao.AR.findById(id);
		render(missao);
	}
	
	private static <T> List<T> filtrarPorOrgao(List<T> lista, final Class<T> classe) {
		List<T> listaFiltrada = Lists.newArrayList(Iterables.filter(lista, new Predicate<T>() {
				public boolean apply(T objeto) {
					if (classe.equals(Plantao.class)) {
						Plantao obj = (Plantao)objeto;
						return obj.condutor.getCpOrgaoUsuario().getId().equals(AutorizacaoGIAntigo.titular().getOrgaoUsuario().getId());
					}
					else if(classe.equals(Afastamento.class)) {
						Afastamento obj = (Afastamento)objeto;
						return obj.getCondutor().getCpOrgaoUsuario().getId().equals(AutorizacaoGIAntigo.titular().getOrgaoUsuario().getId());
					}
					else if(classe.equals(EscalaDeTrabalho.class)) {
						EscalaDeTrabalho obj = (EscalaDeTrabalho)objeto;
						return obj.getCondutor().getCpOrgaoUsuario().getId().equals(AutorizacaoGIAntigo.titular().getOrgaoUsuario().getId());
					}
					else if(classe.equals(ServicoVeiculo.class)) {
						ServicoVeiculo obj = (ServicoVeiculo)objeto;
						return obj.cpOrgaoUsuario.getId().equals(AutorizacaoGIAntigo.titular().getOrgaoUsuario().getId());					
					}
					else if(classe.equals(Missao.class)) {
						Missao obj = (Missao)objeto;
						return obj.cpOrgaoUsuario.getId().equals(AutorizacaoGIAntigo.titular().getOrgaoUsuario().getId());					
					}
					
					return false;
				}
			}
		));	
		
		return listaFiltrada;
	}
	
	private static Calendar recuperarDataEHora(Calendar dataHoraPesquisa, int hora, int minuto, int segundo) {
		Calendar dataHora = Calendar.getInstance();
		dataHora.set(Calendar.YEAR,  dataHoraPesquisa.get(Calendar.YEAR));
		dataHora.set(Calendar.MONTH,  dataHoraPesquisa.get(Calendar.MONTH));
		dataHora.set(Calendar.DAY_OF_MONTH,  dataHoraPesquisa.get(Calendar.DAY_OF_MONTH));
		dataHora.set(Calendar.HOUR_OF_DAY, hora);
		dataHora.set(Calendar.MINUTE,minuto);
		dataHora.set(Calendar.SECOND,segundo);
		return dataHora;
	}		
}
