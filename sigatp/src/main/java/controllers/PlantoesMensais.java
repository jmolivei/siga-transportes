package controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import play.data.validation.Validation;
import play.db.jpa.JPA;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Scope.RenderArgs;
import play.mvc.With;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdmin;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissao;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissaoComplexo;
import br.gov.jfrj.siga.tp.model.Afastamento;
import br.gov.jfrj.siga.tp.model.Condutor;
import br.gov.jfrj.siga.tp.model.DiaDaSemana;
import br.gov.jfrj.siga.tp.model.Mes;
import br.gov.jfrj.siga.tp.model.Plantao;

@With(AutorizacaoGIAntigo.class)
public class PlantoesMensais extends Controller {
	
	@SuppressWarnings("unused")
	private static final String _TEMPLATE_INCLUIR_INICIO = "PlantoesMensais/incluirInicio.html";
	private static final String _TEMPLATE_INCLUIR = "PlantoesMensais/incluir.html";
	private static final String _TEMPLATE_EDITAR = "PlantoesMensais/editar.html";
	private static final String _TEMPLATE_LISTAR = "PlantoesMensais/listar.html";
	private static final String _HORARIO_INICIO_PLANTAO_24H = "07:00";


	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void imprimir(String referencia) throws ParseException {
		List<Plantao> plantoes = Plantao.buscarTodosPorReferencia(referencia);
		ordenarPelaDataHoraInicioDoPlantao(plantoes);
		
		DiaDaSemana diaDaSemana = DiaDaSemana.getDiaDaSemana(plantoes.get(0).dataHoraInicio);
		
		String dadosParaTitulo = plantoes.get(0).referencia;
		
		render(plantoes, diaDaSemana, dadosParaTitulo);
	}
	
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void editar(String referencia) {
		List<Plantao> plantoes = Plantao.buscarTodosPorReferencia(referencia);
		Collections.sort(plantoes);
		Mes mes = extrairMesDaReferencia(referencia);
		int ano = extrairAnoDaReferencia(referencia);
		String hora = extrairHoraDaReferencia(referencia);
		montarDadosParaForm(plantoes, referencia, mes, ano, hora);
		render();
	}
	
	private static String extrairHoraDaReferencia(String referencia) {
		String[] dados = referencia.split("[()]");
		String retorno = dados[1];
		return retorno;
	}

	private static int extrairAnoDaReferencia(String referencia) {
		String[] dados = referencia.split(" ");
		String retorno = dados[2];
		return Integer.parseInt(retorno);
	}

	private static Mes extrairMesDaReferencia(String referencia) {
		String[] dados = referencia.split(" ");
		String mesPorExtenso = dados[0];
		Mes mes = Mes.getMes(mesPorExtenso);
		return mes;
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void excluir(String referencia) {
		List<Plantao> plantoesAExcluir = Plantao.buscarTodosPorReferencia(referencia);
		
		// verificar se este plantao mensal eh o atual ou passado; se sim, nao excluir
		if(!podeExcluirPlantaoMensal(plantoesAExcluir)) {
			Validation.addError("referencias", "plantoesMensais.podeExcluirPlantaoMensal.validation");
			montarDadosParaListar();
			renderTemplate(PlantoesMensais._TEMPLATE_LISTAR);
		}
		
		for (Iterator<Plantao> iterator = plantoesAExcluir.iterator(); iterator
				.hasNext();) {
			Plantao plantao = (Plantao) iterator.next();
			plantao.delete();
		}
		
		listar();
	}
	
	private static boolean podeExcluirPlantaoMensal(List<Plantao> plantoesAExcluir) {
		Plantao teste = plantoesAExcluir.get(0);
		Calendar dataParaTestar = teste.dataHoraInicio;
		
		Calendar ultimoDiaDoMes = Calendar.getInstance();
		ultimoDiaDoMes.set(Calendar.DATE, ultimoDiaDoMes.getMaximum(Calendar.DAY_OF_MONTH));
		
		if(ultimoDiaDoMes.compareTo(dataParaTestar) < 0) {
			return true;
		}
		
		return false;
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void listar() {
		montarDadosParaListar();
		render();
	}

	private static void montarDadosParaListar() {		
		List<String> referencias = Plantao.getReferencias(AutorizacaoGIAntigo.titular().getOrgaoUsuario().getIdOrgaoUsu());
		RenderArgs.current().put("referencias", referencias);
	}
	
	private static String[] gerarDatasPlantaoMensal(Integer mes, Integer ano, String hora) {
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
		
		Calendar data = Calendar.getInstance();
		data.set(Calendar.MONTH, mes);
		data.set(Calendar.YEAR, ano);

		data.set(Calendar.DATE, data.getMinimum(Calendar.DAY_OF_MONTH));
		int ultimoDiaDoMesQueEuQuero = data.getActualMaximum(Calendar.DAY_OF_MONTH);
		String retorno[] = new String[ultimoDiaDoMesQueEuQuero + 1];
		
		for(int i=0; i <= ultimoDiaDoMesQueEuQuero; i++) {
			retorno[i] = formato.format(data.getTime()) + " " + hora;
			data.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		return retorno;
	}
	
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void incluirInicio() {
		montarDadosParaIncluirInicio();
		render();
	}

	private static void montarDadosParaIncluirInicio() {
		Mes optMes = Mes.JANEIRO;
		
		Calendar dataParaTirarMes = Calendar.getInstance();
		dataParaTirarMes.add(Calendar.MONTH, 1);
		
		int anoCorrente = Calendar.getInstance().get(Calendar.YEAR);
		Integer optAno[] = {anoCorrente, anoCorrente + 1};
		
		Mes mesDefault = Mes.getMes(dataParaTirarMes.get(Calendar.MONTH));
		int anoDefault = dataParaTirarMes.get(Calendar.YEAR);
		
		String optHora[] = criarOpcoesDeHora();
		String horaDefault = PlantoesMensais._HORARIO_INICIO_PLANTAO_24H;
	
		RenderArgs.current().put("optHora", optHora);
		RenderArgs.current().put("horaDefault", horaDefault);
		RenderArgs.current().put("optMes", optMes);
		RenderArgs.current().put("mesDefault", mesDefault);
		RenderArgs.current().put("optAno", optAno);
		RenderArgs.current().put("anoDefault", anoDefault);
	}
	
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void incluir(Mes mes, int ano, String hora) {
		String dadosParaTitulo = gerarDadosParaTituloEReferencia(mes, ano, hora);
		
		if(Plantao.plantaoMensalJaExiste(dadosParaTitulo)) {
			Validation.addError("hora", "plantoesMensais.plantaoMensalJaExiste.validation");
			montarDadosParaIncluirInicio();
			renderTemplate("PlantoesMensais/incluirInicio.html");
		}
		
		String diasParaPlantoes[] = gerarDatasPlantaoMensal(mes.getCodigo(), ano, hora);
		List<Plantao> plantoes = new ArrayList<Plantao>();
		for(int cont = 0; cont < diasParaPlantoes.length - 1; cont++) {
			Plantao plantao = new Plantao();
			
			plantao.dataHoraInicio = converterParaCalendar(diasParaPlantoes[cont]);
			plantao.dataHoraFim = converterParaCalendar(diasParaPlantoes[cont + 1]);
			
			plantoes.add(plantao);
		}
		
		montarDadosParaForm(plantoes, dadosParaTitulo, mes, ano, hora);
		render();
	}

	private static String gerarDadosParaTituloEReferencia(Mes mes, int ano,
			String hora) {
		return mes.getNomeExibicao() + " / " + ano + " (" + hora + ")";
	}
	
	private static Calendar converterParaCalendar(String dataEmTexto) {
		Calendar retorno = Calendar.getInstance();
		SimpleDateFormat formatoDataEHora = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		
		try {
			retorno.setTime(formatoDataEHora.parse(dataEmTexto));
		} catch (ParseException e) {
			throw new RuntimeException(Messages.get("plantoesMensais.converterParaCalendar.exception"));
		}
		return retorno;
	}

	private static void montarDadosParaForm(List<Plantao> plantoes,
			String dadosParaTitulo, Mes mes, int ano, String hora) {
		List<Condutor> condutores;
		try {
			condutores = Condutor.listarTodos(AutorizacaoGIAntigo.titular().getOrgaoUsuario());
		} catch (Exception e) {
			throw new RuntimeException(Messages.get("plantoesMensais.montarDadosParaForm.exception"));
		}
		
		RenderArgs.current().put("plantoes", plantoes);
		RenderArgs.current().put("condutores", condutores);
		RenderArgs.current().put("dadosParaTitulo", dadosParaTitulo);
		RenderArgs.current().put("mes", mes);
		RenderArgs.current().put("ano", ano);
		RenderArgs.current().put("hora", hora);
	}

	private static String[] criarOpcoesDeHora() {
		String retorno[] = new String[24];
		
		retorno[0] = "00:00";
		retorno[1] = "01:00";
		retorno[2] = "02:00";
		retorno[3] = "03:00";
		retorno[4] = "04:00";
		retorno[5] = "05:00";
		retorno[6] = "06:00";
		retorno[7] = "07:00";
		retorno[8] = "08:00";
		retorno[9] = "09:00";
		retorno[10] = "10:00";
		retorno[11] = "11:00";
		retorno[12] = "12:00";
		retorno[13] = "13:00";
		retorno[14] = "14:00";
		retorno[15] = "15:00";
		retorno[16] = "16:00";
		retorno[17] = "17:00";
		retorno[18] = "18:00";
		retorno[19] = "19:00";
		retorno[20] = "20:00";
		retorno[21] = "21:00";
		retorno[22] = "22:00";
		retorno[23] = "23:00";
		
		return retorno;
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void salvar(List<Plantao> plantoes, String dadosParaTitulo, Mes mes, int ano, String hora) throws Exception{
		ordenarPelaDataHoraInicioDoPlantao(plantoes);
		
		boolean incluir = (plantoes.get(0).id == 0);
		
		SimpleDateFormat formatoDataEHora = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		SimpleDateFormat formatoSomenteData = new SimpleDateFormat("dd/MM/yyyy");
		List<Plantao> plantoesComErro = new ArrayList<Plantao>();
		
		for (Iterator<Plantao> iterator = plantoes.iterator(); iterator.hasNext();) {
			Plantao plantaoDaLista = (Plantao) iterator.next();
			Plantao plantao = incluir ? plantaoDaLista : montarPlantaoParaSalvar(plantaoDaLista);
			
			if(incluir) {
				plantao.referencia = dadosParaTitulo;
			}
			plantao.condutor = Condutor.AR.findById(plantao.condutor.getId());
			List<Afastamento> afastamentos = Afastamento.buscarPorCondutores(plantao.condutor, plantao.dataHoraInicio, plantao.dataHoraFim);
			if(afastamentos != null && !afastamentos.isEmpty()) {
				Validation.addError("plantao", "plantoesMensais.afastamentos.validation", plantao.condutor.getDadosParaExibicao(), formatoDataEHora.format(plantao.dataHoraInicio.getTime()), formatoDataEHora.format(plantao.dataHoraFim.getTime()));
				plantoesComErro.add(plantao);
			} else {
				try {
					plantao.save();
				} catch (Exception e) {
					Validation.addError("plantao:" + e.getMessage(), "Houve um erro n&atilde;o identificado ao salvar o plant&atilde;o do dia " + formatoSomenteData.format(plantao.dataHoraInicio.getTime()) + ". Verifique se o plant&atilde;o j&aacute; foi cadastrado anteriormente para a mesma data.");
					plantoesComErro.add(plantao);
				}
			}
		}
		
		if(!plantoesComErro.isEmpty()) {
			JPA.setRollbackOnly();
			if(incluir) {
				zerarIdsInvalidosDaListaDePlantao(plantoes);
			}

			montarDadosParaForm(new ArrayList<Plantao>(plantoes), dadosParaTitulo, mes, ano, hora);
			if(incluir) {
				renderTemplate(_TEMPLATE_INCLUIR);
			} else {
				renderTemplate(_TEMPLATE_EDITAR);
			}
		} 
		
		listar();
		
	}

	private static Plantao montarPlantaoParaSalvar(Plantao plantao) throws Exception {
		Plantao retorno = Plantao.AR.findById(plantao.id);
		retorno.condutor = Condutor.AR.findById(plantao.condutor.getId());
		return retorno;
	}

	private static void ordenarPelaDataHoraInicioDoPlantao(List<Plantao> plantoes) {
		// nao vem ordenado conforme o HTML manda
		Collections.sort(plantoes);
	}

	private static void zerarIdsInvalidosDaListaDePlantao(List<Plantao> plantoes) {
		// apos o rollback, os Ids atribuidos aos plantoes 
		// que nao deram erro sao invalidos e precisam ser zerados
		for (Iterator<Plantao> iterator = plantoes.iterator(); iterator.hasNext();) {
			Plantao plantao = (Plantao) iterator.next();
			plantao.id = 0L;
		}
	}
	
}
