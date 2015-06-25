package controllers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;

import play.data.validation.Validation;
import play.db.jpa.JPA;
import play.mvc.Controller;
import play.mvc.With;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.tp.model.Abastecimento;
import br.gov.jfrj.siga.tp.model.EstadoMissao;
import br.gov.jfrj.siga.tp.model.Missao;
import br.gov.jfrj.siga.tp.model.RelatorioConsumoMedio;
import br.gov.jfrj.siga.tp.model.Veiculo;

@With(AutorizacaoGIAntigo.class)
public class RelatoriosConsumoMedio extends Controller {
	public static void consultar() throws Exception {
		RelatorioConsumoMedio relatorioConsumoMedio = new RelatorioConsumoMedio();
		montarCombos();
		render(relatorioConsumoMedio);
	}

	public static void gerarRelatorio(RelatorioConsumoMedio relatorioConsumoMedio) throws ParseException, Exception {
		if (Validation.hasErrors()) {
			montarCombos();
			renderTemplate("@Consultar");
		} else {
			String msgErro = "";

			if (relatorioConsumoMedio.getAbastecimentoInicial() == null) {
				msgErro += "Abastecimento Inicial, ";
			}
			if (relatorioConsumoMedio.getAbastecimentoFinal() == null) {
				msgErro += "Abastecimento Final, ";
			}

			if (msgErro != "") {
				boolean plural = StringUtils.countMatches(msgErro, ",") > 1 ? true : false;
				msgErro = msgErro.substring(0, msgErro.length() - 2);
				msgErro += " deve" + (plural ? "m" : "") + " ser preenchido" + (plural ? "s" : "");
				Validation.addError("relatorio", msgErro);
			}

			if (!Validation.hasErrors()) {
				RelatorioConsumoMedio relatoriocm = calcularConsumoMedio(relatorioConsumoMedio);
				render(relatoriocm);
			} else {
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
		CpOrgaoUsuario cpOrgaoUsuario = AutorizacaoGIAntigo.titular().getOrgaoUsuario();
		RelatorioConsumoMedio resultado = new RelatorioConsumoMedio();

		Calendar dataInicial = Calendar.getInstance();
		relatorio.setAbastecimentoInicial(Abastecimento.AR.findById(relatorio.getAbastecimentoInicial().getId()));
		dataInicial.setTime(relatorio.getAbastecimentoInicial().getDataHora().getTime());

		Calendar dataFinal = Calendar.getInstance();
		relatorio.setAbastecimentoFinal(Abastecimento.AR.findById(relatorio.getAbastecimentoFinal().getId()));
		dataFinal.setTime(relatorio.getAbastecimentoFinal().getDataHora().getTime());

		String qrl = "SELECT m.id, m.consumoEmLitros, m.odometroSaidaEmKm, m.odometroRetornoEmKm " + "FROM  Missao m " + "WHERE m.veiculo.getId() = ? " + "AND   m.dataHora BETWEEN ? AND ? "
				+ "AND   m.cpOrgaoUsuario.idOrgaoUsu = ? " + "AND   m.estadoMissao = ? ";

		Query qry = JPA.em().createQuery(qrl);
		qry.setParameter(1, relatorio.getVeiculo().getId());
		qry.setParameter(2, dataInicial);
		qry.setParameter(3, dataFinal);
		qry.setParameter(4, cpOrgaoUsuario.getIdOrgaoUsu());
		qry.setParameter(5, EstadoMissao.FINALIZADA);

		lista = (List<Object[]>) qry.getResultList();

		double kmInicial = relatorio.getAbastecimentoInicial().getOdometroEmKm();
		double kmFinal = relatorio.getAbastecimentoFinal().getOdometroEmKm();
		double quantidadeEmLitros = relatorio.getAbastecimentoFinal().getQuantidadeEmLitros();

		for (int i = 0; i < lista.size(); i++) {
			if ((Double.parseDouble(lista.get(i)[2].toString()) >= kmInicial) || (Double.parseDouble(lista.get(i)[3].toString()) <= kmFinal)) {
				missao = new Missao();
				missao.setId(Long.parseLong(lista.get(i)[0].toString()));
				setMissao.add((Missao) Missao.AR.findById(missao.getId()));
			}
		}

		resultado.setAbastecimentoInicial(new Abastecimento());
		resultado.getAbastecimentoInicial().setDataHora(dataInicial);

		resultado.setAbastecimentoFinal(new Abastecimento());
		resultado.getAbastecimentoFinal().setDataHora(dataFinal);

		resultado.setVeiculo(Veiculo.AR.findById(relatorio.getVeiculo().getId()));
		resultado.setMissoes(new ArrayList<Missao>(setMissao));
		resultado.setKmPercorridos(Double.parseDouble(String.format("%.2f", kmFinal - kmInicial).replace(",", ".")));
		resultado.setConsumoMedio(Double.parseDouble(String.format("%.2f", quantidadeEmLitros <= 0 ? 0 : (kmFinal - kmInicial) / quantidadeEmLitros).replace(",", ".")));
		setMissao.clear();
		return resultado;
	}

	private static void montarCombos() throws Exception {
		List<Veiculo> veiculos = Veiculo.listarTodos(AutorizacaoGIAntigo.titular().getOrgaoUsuario());
		List<Abastecimento> abastecimentosIniciais = new ArrayList<Abastecimento>();
		List<Abastecimento> abastecimentosFinais = new ArrayList<Abastecimento>();

		abastecimentosIniciais = montarCombosAbastecimento(veiculos.get(0).getId());

		if (abastecimentosIniciais.size() > 0) {
			for (Abastecimento abastecimento : abastecimentosIniciais) {
				if (abastecimentosIniciais.size() > 1) {
					if (abastecimento.getDataHora().after(abastecimentosIniciais.get(1))) {
						abastecimentosFinais.add(abastecimento);
					}
				}
			}
		}

		renderArgs.put("veiculos", veiculos);
		renderArgs.put("abastecimentosIniciais", abastecimentosIniciais);
		if (abastecimentosFinais.size() > 0) {
			renderArgs.put("abastecimentosFinais", abastecimentosFinais);
		}
	}

	public static List<Abastecimento> montarCombosAbastecimento(Long idVeiculo) throws Exception {
		Veiculo veiculo = Veiculo.AR.findById(idVeiculo);
		return Abastecimento.buscarTodosPorVeiculo(veiculo);
	}

	public static void carregarComboAbastecimentoInicial(Long idVeiculo) throws Exception {
		StringBuffer htmlSelectAbastecimentoInicial = new StringBuffer();
		StringBuffer htmlSelectAbastecimentoFinal = new StringBuffer();
		List<Abastecimento> lstAbastecimento = montarCombosAbastecimento(idVeiculo);

		if (lstAbastecimento.size() > 0) {
			if (lstAbastecimento.size() == 1) {
				htmlSelectAbastecimentoInicial.append("<option value='" + lstAbastecimento.get(0).getId() + "'");
				htmlSelectAbastecimentoInicial.append(">" + lstAbastecimento.get(0).getDadosParaExibicao());
				htmlSelectAbastecimentoInicial.append("</option>");
			} else {
				for (Abastecimento abastecimento : lstAbastecimento) {

					htmlSelectAbastecimentoInicial.append("<option value='" + abastecimento.getId() + "'");

					if (lstAbastecimento.indexOf(abastecimento) == 1) {
						htmlSelectAbastecimentoInicial.append(" selected='selected'");
					}

					htmlSelectAbastecimentoInicial.append(">" + abastecimento.getDadosParaExibicao());
					htmlSelectAbastecimentoInicial.append("</option>");

					if (abastecimento.getDataHora().after(lstAbastecimento.get(1).getDataHora())) {
						htmlSelectAbastecimentoFinal.append("<option value='" + abastecimento.getId() + "'");

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