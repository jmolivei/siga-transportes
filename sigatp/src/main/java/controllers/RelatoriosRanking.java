package controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import play.data.validation.Validation;
import play.db.jpa.JPA;
import play.mvc.Controller;
import play.mvc.With;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.tp.model.Condutor;
import br.gov.jfrj.siga.tp.model.FinalidadeRequisicao;
import br.gov.jfrj.siga.tp.model.Missao;
import br.gov.jfrj.siga.tp.model.RelatorioRanking;
import br.gov.jfrj.siga.tp.model.RequisicaoTransporte;
import br.gov.jfrj.siga.tp.model.TipoDePassageiro;
import br.gov.jfrj.siga.tp.model.Veiculo;
import br.gov.jfrj.siga.tp.model.RelatorioRanking.RankingCondutorRequisicao;
import br.gov.jfrj.siga.tp.model.RelatorioRanking.RankingFinalidadeRequisicao;
import br.gov.jfrj.siga.tp.model.RelatorioRanking.RankingTipoPassageiroRequisicao;
import br.gov.jfrj.siga.tp.model.RelatorioRanking.RankingVeiculoRequisicao;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@With(AutorizacaoGIAntigo.class)
public class RelatoriosRanking extends Controller {
	private static String[] gerarVetorNumeros() {
		String vetor[] = new String[20];
		for (int i = 1; i <= 20; i++) {
			vetor[i - 1] = String.valueOf(i);
		}
		return vetor;
	}

	public static void consultar() {
		String numeros[] = gerarVetorNumeros();
		String valorDefault = "1";
		RelatorioRanking relatorioRanking = new RelatorioRanking();
		renderArgs.put("optValores", numeros);
		renderArgs.put("valorDefault", valorDefault);
		render(relatorioRanking);
	}

	public static void gerarRelatorios(RelatorioRanking relatorioRanking) throws ParseException {
		String numeros[] = gerarVetorNumeros();
		String valorDefault = "1";

		if (Validation.hasErrors()) {
			renderArgs.put("optValores", numeros);
			renderArgs.put("valorDefault", valorDefault);
			renderTemplate("@Consultar");
		} else {
			String msgErro = "";

			if (relatorioRanking.dataInicio == null) {
				msgErro += "Data Inicio, ";
			}
			if (relatorioRanking.dataFim == null) {
				msgErro += "Data Fim, ";
			}

			if (msgErro != "") {
				msgErro = msgErro.substring(0, msgErro.length() - 2);
				msgErro += " devem ser preenchidos";
				Validation.addError("relatorio", msgErro);
			}

			if (!Validation.hasErrors()) {
				List<RankingCondutorRequisicao> rc = retornarCondutoresQueAtenderamMaisRequisicoes(relatorioRanking);
				List<RankingVeiculoRequisicao> rv = retornarVeiculosQueAtenderamMaisRequisicoes(relatorioRanking);
				List<RankingFinalidadeRequisicao> rf = retornarFinalidadesComMaisRequisicoes(relatorioRanking);
				List<RankingTipoPassageiroRequisicao> rtp = retornarTipoPassageiroComMaisRequisicoes(relatorioRanking);
				render(rc, rv, rf, rtp, relatorioRanking);
			} else {
				renderArgs.put("optValores", numeros);
				renderArgs.put("valorDefault", valorDefault);
				render("@Consultar", relatorioRanking);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static List<RankingCondutorRequisicao> retornarCondutoresQueAtenderamMaisRequisicoes(RelatorioRanking relatorio) throws ParseException {
		List<Object[]> lista;
		List<RankingCondutorRequisicao> listaRankingCondutor = new ArrayList<RankingCondutorRequisicao>();
		Set<Missao> setMissao = new HashSet<Missao>();
		Set<RequisicaoTransporte> setRequisicao = new HashSet<RequisicaoTransporte>();
		Condutor condutor = null;
		Missao missao = null;
		RequisicaoTransporte requisicao = null;
		CpOrgaoUsuario cpOrgaoUsuario = AutorizacaoGIAntigo.titular().getOrgaoUsuario();

		try {
			relatorio.dataInicio.setTime(formatarDataHora(relatorio.dataInicio, "00:00:00"));
			relatorio.dataFim.setTime(formatarDataHora(relatorio.dataFim, "23:59:59"));

			String qrl = "SELECT c.id, m.id, r.id " + "FROM Condutor c, Missao m " + "INNER JOIN m.requisicoesTransporte r " + "WHERE c.id = m.condutor.id " + "and   dataHoraRetorno BETWEEN ? AND ? "
					+ "AND   r.cpOrgaoUsuario.idOrgaoUsu = ? " + "ORDER BY c.id, m.id, r.id";

			Query qry = JPA.em().createQuery(qrl);
			qry.setParameter(1, relatorio.dataInicio);
			qry.setParameter(2, relatorio.dataFim);
			qry.setParameter(3, cpOrgaoUsuario.getIdOrgaoUsu());

			lista = (List<Object[]>) qry.getResultList();
			Long idProximoCondutor = 0L;
			RankingCondutorRequisicao itemRc = null;
			Boolean salvar = false;

			for (int i = 0; i < lista.size(); i++) {
				condutor = new Condutor();
				condutor.setId(Long.parseLong(lista.get(i)[0].toString()));

				missao = new Missao();
				missao.setId(Long.parseLong(lista.get(i)[1].toString()));
				setMissao.add((Missao) Missao.AR.findById(missao.getId()));

				requisicao = new RequisicaoTransporte();
				requisicao.id = Long.parseLong(lista.get(i)[2].toString());
				setRequisicao.add((RequisicaoTransporte) RequisicaoTransporte.AR.findById(requisicao.id));

				if (i < lista.size() - 1) {
					idProximoCondutor = Long.parseLong(lista.get(i + 1)[0].toString());

					if (!condutor.getId().equals(idProximoCondutor)) {
						salvar = true;
					}
				} else {
					salvar = true;
				}

				if (salvar) {
					itemRc = new RelatorioRanking().new RankingCondutorRequisicao();
					itemRc.condutor = Condutor.AR.findById(condutor.getId());
					itemRc.missoes = new ArrayList<Missao>(setMissao);
					itemRc.requisicoes = new ArrayList<RequisicaoTransporte>(setRequisicao);
					listaRankingCondutor.add(itemRc);
					setMissao.clear();
					setRequisicao.clear();
					salvar = false;
				}
			}

			Collections.sort(listaRankingCondutor);

		} catch (Exception ex) {
			listaRankingCondutor = null;
		}

		return listaRankingCondutor;
	}

	@SuppressWarnings("unchecked")
	public static List<RankingVeiculoRequisicao> retornarVeiculosQueAtenderamMaisRequisicoes(RelatorioRanking relatorio) throws ParseException {
		List<Object[]> lista;
		List<RankingVeiculoRequisicao> listaRankingVeiculo = new ArrayList<RankingVeiculoRequisicao>();
		Set<RequisicaoTransporte> setRequisicao = new HashSet<RequisicaoTransporte>();
		Veiculo veiculo = null;
		RequisicaoTransporte requisicao = null;
		CpOrgaoUsuario cpOrgaoUsuario = AutorizacaoGIAntigo.titular().getOrgaoUsuario();

		try {
			relatorio.dataInicio.setTime(formatarDataHora(relatorio.dataInicio, "00:00:00"));
			relatorio.dataFim.setTime(formatarDataHora(relatorio.dataFim, "23:59:59"));

			String qrl = "SELECT v.id, r.id " + "FROM Veiculo v, Missao m " + "INNER JOIN m.requisicoesTransporte r " + "WHERE v.id = m.veiculo.id " + "and   dataHoraRetorno BETWEEN ? AND ? "
					+ "AND   r.cpOrgaoUsuario.idOrgaoUsu = ? " + "ORDER BY v.id, r.id";

			Query qry = JPA.em().createQuery(qrl);
			qry.setParameter(1, relatorio.dataInicio);
			qry.setParameter(2, relatorio.dataFim);
			qry.setParameter(3, cpOrgaoUsuario.getIdOrgaoUsu());
			lista = (List<Object[]>) qry.getResultList();

			Long idProximoVeiculo = 0L;
			RankingVeiculoRequisicao itemRv = null;
			Boolean salvar = false;

			for (int i = 0; i < lista.size(); i++) {
				veiculo = new Veiculo();
				veiculo.setId(Long.parseLong(lista.get(i)[0].toString()));

				requisicao = new RequisicaoTransporte();
				requisicao.id = Long.parseLong(lista.get(i)[1].toString());
				setRequisicao.add((RequisicaoTransporte) RequisicaoTransporte.AR.findById(requisicao.id));

				if (i < lista.size() - 1) {
					idProximoVeiculo = Long.parseLong(lista.get(i + 1)[0].toString());

					if (!veiculo.getId().equals(idProximoVeiculo)) {
						salvar = true;
					}
				} else {
					salvar = true;
				}

				if (salvar) {
					itemRv = new RelatorioRanking().new RankingVeiculoRequisicao();
					itemRv.veiculo = Veiculo.AR.findById(veiculo.getId());
					itemRv.requisicoes = new ArrayList<RequisicaoTransporte>(setRequisicao);
					listaRankingVeiculo.add(itemRv);
					setRequisicao.clear();
					salvar = false;
				}
			}

			Collections.sort(listaRankingVeiculo);

		} catch (Exception ex) {
			listaRankingVeiculo = null;
		}

		return listaRankingVeiculo;
	}

	@SuppressWarnings("unchecked")
	public static List<RankingFinalidadeRequisicao> retornarFinalidadesComMaisRequisicoes(RelatorioRanking relatorio) throws ParseException {
		List<Object[]> lista;
		List<RankingFinalidadeRequisicao> listaRankingFinalidade = new ArrayList<RankingFinalidadeRequisicao>();
		FinalidadeRequisicao finalidade = null;
		int totalFinalidade = 0;
		CpOrgaoUsuario cpOrgaoUsuario = AutorizacaoGIAntigo.titular().getOrgaoUsuario();

		try {
			relatorio.dataInicio.setTime(formatarDataHora(relatorio.dataInicio, "00:00:00"));
			relatorio.dataFim.setTime(formatarDataHora(relatorio.dataFim, "23:59:59"));

			String qrl = "SELECT f.id, count(f.id) as total_finalidade " + "FROM  FinalidadeRequisicao f, RequisicaoTransporte r " + "WHERE r.tipoFinalidade.id = f.id "
					+ "and   r.dataHora BETWEEN ? AND ? " + "AND   r.cpOrgaoUsuario.idOrgaoUsu = ? " + "GROUP BY f.id " + "ORDER BY total_finalidade DESC";

			Query qry = JPA.em().createQuery(qrl);
			qry.setParameter(1, relatorio.dataInicio);
			qry.setParameter(2, relatorio.dataFim);
			qry.setParameter(3, cpOrgaoUsuario.getIdOrgaoUsu());

			lista = (List<Object[]>) qry.getResultList();
			RankingFinalidadeRequisicao itemRf = null;

			for (int i = 0; i < lista.size(); i++) {
				finalidade = new FinalidadeRequisicao();
				finalidade.setId(Long.parseLong(lista.get(i)[0].toString()));
				totalFinalidade = Integer.parseInt(lista.get(i)[1].toString());

				itemRf = new RelatorioRanking().new RankingFinalidadeRequisicao();
				itemRf.finalidade = FinalidadeRequisicao.AR.findById(finalidade.getId());
				itemRf.totalFinalidade = totalFinalidade;
				listaRankingFinalidade.add(itemRf);
			}

		} catch (Exception ex) {
			listaRankingFinalidade = null;
		}

		return listaRankingFinalidade;
	}

	public static List<RankingTipoPassageiroRequisicao> retornarTipoPassageiroComMaisRequisicoes(RelatorioRanking relatorio) throws ParseException {
		List<RequisicaoTransporte> lista;
		List<RankingTipoPassageiroRequisicao> listaRankingTipoPassageiro = new ArrayList<RankingTipoPassageiroRequisicao>();
		List<TipoDePassageiro> listaTipoDePassageiro = Arrays.asList(TipoDePassageiro.values());
		CpOrgaoUsuario cpOrgaoUsuario = AutorizacaoGIAntigo.titular().getOrgaoUsuario();

		try {
			relatorio.dataInicio.setTime(formatarDataHora(relatorio.dataInicio, "00:00:00"));
			relatorio.dataFim.setTime(formatarDataHora(relatorio.dataFim, "23:59:59"));
			lista = RequisicaoTransporte.AR.find("dataHora BETWEEN ? AND ? " + "AND cpOrgaoUsuario.idOrgaoUsu = ? ", relatorio.dataInicio, relatorio.dataFim, cpOrgaoUsuario.getIdOrgaoUsu()).fetch();
			RankingTipoPassageiroRequisicao itemRp = null;

			for (int i = 0; i < listaTipoDePassageiro.size(); i++) {
				final TipoDePassageiro tipoPassageiro = listaTipoDePassageiro.get(i);

				if (lista.size() > 0) {
					List<RequisicaoTransporte> requisicoesFiltradas = Lists.newArrayList(Iterables.filter(lista, new Predicate<RequisicaoTransporte>() {
						public boolean apply(RequisicaoTransporte requisicao) {
							return requisicao.tiposDePassageiro.contains(tipoPassageiro);
						}
					}));

					itemRp = new RelatorioRanking().new RankingTipoPassageiroRequisicao();
					itemRp.tipoPassageiro = tipoPassageiro.getDescricao();
					itemRp.totalTipoPassageiros = requisicoesFiltradas.size();
					listaRankingTipoPassageiro.add(itemRp);
				}
			}

			Collections.sort(listaRankingTipoPassageiro);

		} catch (Exception ex) {
			listaRankingTipoPassageiro = null;
		}

		return listaRankingTipoPassageiro;
	}

	private static Date formatarDataHora(Calendar data, String hora) throws ParseException {
		String strDataPesquisa = "";
		SimpleDateFormat formatar = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		strDataPesquisa = String.format("%02d", data.get(Calendar.DAY_OF_MONTH)) + "/" + String.format("%02d", data.get(Calendar.MONTH) + 1) + "/" + String.format("%04d", data.get(Calendar.YEAR));
		return formatar.parse(strDataPesquisa + " " + hora);
	}
}