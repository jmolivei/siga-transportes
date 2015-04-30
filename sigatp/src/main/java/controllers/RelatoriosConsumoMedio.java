package controllers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;

import models.Abastecimento;
import models.EstadoMissao;
import models.Missao;
import models.RelatorioConsumoMedio;
import models.Veiculo;
import play.data.validation.Validation;
import play.db.jpa.JPA;
import play.mvc.Controller;
import play.mvc.With;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;

@With(AutorizacaoGI.class)
public class RelatoriosConsumoMedio extends Controller {
	public static void consultar() throws Exception {
		RelatorioConsumoMedio relatorioConsumoMedio = new RelatorioConsumoMedio();
		montarCombos();
		render(relatorioConsumoMedio); 
	}
	
	public static void gerarRelatorio(RelatorioConsumoMedio relatorioConsumoMedio) throws ParseException, Exception {
		if(Validation.hasErrors()){
			montarCombos();
			renderTemplate("@Consultar");
		}
		else {
			String msgErro = "";
			
			if (relatorioConsumoMedio.abastecimentoInicial == null) {
				msgErro += "Abastecimento Inicial, ";
			}
			if (relatorioConsumoMedio.abastecimentoFinal == null) {
				 msgErro += "Abastecimento Final, ";
			}
			
			if (msgErro != "") {
				boolean plural = StringUtils.countMatches(msgErro, ",") > 1 ? true : false; 
				msgErro = msgErro.substring(0, msgErro.length()-2);
				msgErro += " deve" + (plural ? "m" : "") + " ser preenchido" + (plural ? "s" : "");
				Validation.addError("relatorio", msgErro);
			}
			
			if (!Validation.hasErrors()) {
				RelatorioConsumoMedio relatoriocm = calcularConsumoMedio(relatorioConsumoMedio);
				render (relatoriocm);
			}
			else {
				montarCombos();
				render("@Consultar", relatorioConsumoMedio);
			}
		} 
	}
	
	@SuppressWarnings("unchecked")
	private static RelatorioConsumoMedio calcularConsumoMedio(RelatorioConsumoMedio relatorio) throws Exception {
		List<Object[]> lista;
		Set<Missao> setMissao = new HashSet<Missao>();
		Missao missao = null;
		CpOrgaoUsuario cpOrgaoUsuario = AutorizacaoGI.titular().getOrgaoUsuario();
		RelatorioConsumoMedio resultado = new RelatorioConsumoMedio();

		Calendar dataInicial = Calendar.getInstance(); 
		relatorio.abastecimentoInicial = Abastecimento.findById(relatorio.abastecimentoInicial.id);
		dataInicial.setTime(relatorio.abastecimentoInicial.dataHora.getTime());
		
		Calendar dataFinal = Calendar.getInstance(); 
		relatorio.abastecimentoFinal = Abastecimento.findById(relatorio.abastecimentoFinal.id);
		dataFinal.setTime(relatorio.abastecimentoFinal.dataHora.getTime());
		
		String qrl = "SELECT m.id, m.consumoEmLitros, m.odometroSaidaEmKm, m.odometroRetornoEmKm " +
					 "FROM  Missao m " + 
					 "WHERE m.veiculo.id = ? " +
					 "AND   m.dataHora BETWEEN ? AND ? " + 
					 "AND   m.cpOrgaoUsuario.idOrgaoUsu = ? " + 
					 "AND   m.estadoMissao = ? "; 

		Query qry = JPA.em().createQuery(qrl);
		qry.setParameter(1, relatorio.veiculo.id);
		qry.setParameter(2, dataInicial);
		qry.setParameter(3, dataFinal);
		qry.setParameter(4, cpOrgaoUsuario.getIdOrgaoUsu());
		qry.setParameter(5, EstadoMissao.FINALIZADA);
		
		lista = (List<Object[]>) qry.getResultList();
		
		double kmInicial = relatorio.abastecimentoInicial.odometroEmKm;
		double kmFinal = relatorio.abastecimentoFinal.odometroEmKm;
		double quantidadeEmLitros = relatorio.abastecimentoFinal.quantidadeEmLitros;

		for (int i = 0; i < lista.size(); i++) {
			if ((Double.parseDouble(lista.get(i)[2].toString()) >= kmInicial) || 
				(Double.parseDouble(lista.get(i)[3].toString()) <= kmFinal)) {
				missao = new Missao();
				missao.id = Long.parseLong(lista.get(i)[0].toString());
				setMissao.add((Missao) Missao.findById(missao.id));
			}
		}
		
		resultado.abastecimentoInicial = new Abastecimento();
		resultado.abastecimentoInicial.dataHora = dataInicial;

		resultado.abastecimentoFinal = new Abastecimento();
		resultado.abastecimentoFinal.dataHora = dataFinal;
		
		resultado.veiculo = Veiculo.findById(relatorio.veiculo.id);
		resultado.missoes = new ArrayList<Missao>(setMissao);
		resultado.kmPercorridos =  Double.parseDouble(String.format("%.2f", kmFinal - kmInicial).replace(",","."));
		resultado.consumoMedio = Double.parseDouble(String.format("%.2f", quantidadeEmLitros <= 0 ? 0 : (kmFinal - kmInicial) / quantidadeEmLitros).replace(",","."));
		setMissao.clear();
		return resultado;
	}

	private static void montarCombos() throws Exception {
		List<Veiculo> veiculos = Veiculo.listarTodos(AutorizacaoGI.titular().getOrgaoUsuario());
		List<Abastecimento> abastecimentosIniciais = new ArrayList<Abastecimento>();
		List<Abastecimento> abastecimentosFinais = new ArrayList<Abastecimento>();
		
		abastecimentosIniciais = montarCombosAbastecimento(veiculos.get(0).id);
		
		if (abastecimentosIniciais.size() > 0) {
			for (Abastecimento abastecimento : abastecimentosIniciais) {
				if (abastecimentosIniciais.size() > 1) {
					if (abastecimento.dataHora.after(abastecimentosIniciais.get(1))) {
						abastecimentosFinais.add(abastecimento);
					}
				}
			}
		}
		
		renderArgs.put("veiculos",veiculos);
		renderArgs.put("abastecimentosIniciais",abastecimentosIniciais);
		if (abastecimentosFinais.size() > 0) {
			renderArgs.put("abastecimentosFinais",abastecimentosFinais);
		}
	}

	public static List<Abastecimento> montarCombosAbastecimento(Long idVeiculo) {
		Veiculo veiculo = Veiculo.findById(idVeiculo);
		return Abastecimento.buscarTodosPorVeiculo(veiculo);
	}
	
	public static void carregarComboAbastecimentoInicial(Long idVeiculo) {
		StringBuffer htmlSelectAbastecimentoInicial = new StringBuffer();
		StringBuffer htmlSelectAbastecimentoFinal = new StringBuffer();
		List<Abastecimento> lstAbastecimento = montarCombosAbastecimento(idVeiculo);

		if (lstAbastecimento.size() > 0) {
			if (lstAbastecimento.size()==1) {
				htmlSelectAbastecimentoInicial.append("<option value='"+ lstAbastecimento.get(0).id + "'");
				htmlSelectAbastecimentoInicial.append(">" + lstAbastecimento.get(0).getDadosParaExibicao());
				htmlSelectAbastecimentoInicial.append("</option>");
			}
			else {
				for (Abastecimento abastecimento : lstAbastecimento) {

					htmlSelectAbastecimentoInicial.append("<option value='"+ abastecimento.id + "'");
					
					if (lstAbastecimento.indexOf(abastecimento) == 1) {
						htmlSelectAbastecimentoInicial.append(" selected='selected'");
					}
					
					htmlSelectAbastecimentoInicial.append(">" + abastecimento.getDadosParaExibicao());
					htmlSelectAbastecimentoInicial.append("</option>");

					if (abastecimento.dataHora.after(lstAbastecimento.get(1).dataHora)) {
						htmlSelectAbastecimentoFinal.append("<option value='"+ abastecimento.id + "'");

						if (lstAbastecimento.indexOf(abastecimento) == 2) {
							htmlSelectAbastecimentoFinal.append(" selected='selected'");
						}

						htmlSelectAbastecimentoFinal.append(">" + abastecimento.getDadosParaExibicao());
						htmlSelectAbastecimentoFinal.append("</option>");
					}
				}
			}
		}

		renderText(htmlSelectAbastecimentoInicial + "@" + htmlSelectAbastecimentoFinal);
	}
}