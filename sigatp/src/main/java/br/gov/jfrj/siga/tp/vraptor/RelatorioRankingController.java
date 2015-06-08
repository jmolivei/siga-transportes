package br.gov.jfrj.siga.tp.vraptor;

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

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

import play.db.jpa.JPA;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.validator.ValidationMessage;
import br.com.caelum.vraptor.view.Results;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.tp.auth.AutorizacaoGI;
import br.gov.jfrj.siga.tp.model.Condutor;
import br.gov.jfrj.siga.tp.model.FinalidadeRequisicao;
import br.gov.jfrj.siga.tp.model.Missao;
import br.gov.jfrj.siga.tp.model.RelatorioRanking;
import br.gov.jfrj.siga.tp.model.RelatorioRanking.RankingCondutorRequisicao;
import br.gov.jfrj.siga.tp.model.RelatorioRanking.RankingFinalidadeRequisicao;
import br.gov.jfrj.siga.tp.model.RelatorioRanking.RankingTipoPassageiroRequisicao;
import br.gov.jfrj.siga.tp.model.RelatorioRanking.RankingVeiculoRequisicao;
import br.gov.jfrj.siga.tp.model.RequisicaoTransporte;
import br.gov.jfrj.siga.tp.model.TipoDePassageiro;
import br.gov.jfrj.siga.tp.model.TpDao;
import br.gov.jfrj.siga.tp.model.Veiculo;
import br.gov.jfrj.siga.vraptor.SigaObjects;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@Resource
@Path("/app/relatorioRanking")
public class RelatorioRankingController extends TpController {
	private AutorizacaoGI autorizacaoGI;

	public RelatorioRankingController(HttpServletRequest request,
			Result result, Validator validator, SigaObjects so,
			EntityManager em, AutorizacaoGI autorizacaoGI) {
		super(request, result, TpDao.getInstance(), validator, so, em);

		this.autorizacaoGI = autorizacaoGI;
	}

	private static String[] gerarVetorNumeros() {
		String[] vetor = new String[20];
		for (int i = 1; i <= 20; i++) {
			vetor[i - 1] = String.valueOf(i);
		}
		return vetor;
	}

	@Path("/consultar")
	public void consultar() {
		String[] numeros = gerarVetorNumeros();
		String valorDefault = "1";
		RelatorioRanking relatorioRanking = new RelatorioRanking();
		result.include("optValores", numeros);
		result.include("valorDefault", valorDefault);

		result.include("relatorioRanking", relatorioRanking);
	}

	public void gerarRelatorios(RelatorioRanking relatorioRanking)
			throws ParseException, Exception {
		String[] numeros = gerarVetorNumeros();
		String valorDefault = "1";

		if (validator.hasErrors()) {
			result.include("optValores", numeros);
			result.include("valorDefault", valorDefault);

			validator.onErrorUse(Results.page())
					.of(RelatorioRankingController.class).consultar();

		} else {
			validaDatas(relatorioRanking);

			if (!validator.hasErrors()) {
				List<RankingCondutorRequisicao> rc = retornarCondutoresQueAtenderamMaisRequisicoes(relatorioRanking);
				List<RankingVeiculoRequisicao> rv = retornarVeiculosQueAtenderamMaisRequisicoes(relatorioRanking);
				List<RankingFinalidadeRequisicao> rf = retornarFinalidadesComMaisRequisicoes(relatorioRanking);
				List<RankingTipoPassageiroRequisicao> rtp = retornarTipoPassageiroComMaisRequisicoes(relatorioRanking);
				result.include("rc", rc);
				result.include("rv", rv);
				result.include("rf", rf);
				result.include("rtp", rtp);
				result.include("relatorioRanking", relatorioRanking);
			} else {
				result.include("optValores", numeros);
				result.include("valorDefault", valorDefault);
				result.include("relatorioRanking", relatorioRanking);
				result.redirectTo(this).consultar();
			}
		}
	}

	private void validaDatas(RelatorioRanking relatorioRanking) {
		String msgErro = "";

		if (relatorioRanking.getDataInicio() == null)
			msgErro += "Data Inicio ";

		if (relatorioRanking.getDataFim() == null)
			msgErro += msgErro.equalsIgnoreCase("") ? "Data Fim "
					: "e Data Fim ";

		msgErro += " deve(m) ser preenchido(s)";

		if (relatorioRanking.getDataFim() != null
				&& relatorioRanking.getDataInicio() != null
				&& relatorioRanking.getDataFim().getTime()
						.before(relatorioRanking.getDataInicio().getTime()))
			msgErro = "Data Inicio maior que Data Fim";

		if (!msgErro.equals("")) {
			result.include("optValores", gerarVetorNumeros());
			validator.add(new ValidationMessage(msgErro, "relatorio"));
			validator.onErrorUse(Results.page())
					.of(RelatorioRankingController.class).consultar();
		}
	}

	@SuppressWarnings("unchecked")
	public List<RankingCondutorRequisicao> retornarCondutoresQueAtenderamMaisRequisicoes(
			RelatorioRanking relatorio) throws ParseException, Exception {
		List<Object[]> lista;
		List<RankingCondutorRequisicao> listaRankingCondutor = new ArrayList<RankingCondutorRequisicao>();
		Set<Missao> setMissao = new HashSet<Missao>();
		Set<RequisicaoTransporte> setRequisicao = new HashSet<RequisicaoTransporte>();
		Condutor condutor = null;
		Missao missao = null;
		RequisicaoTransporte requisicao = null;
		CpOrgaoUsuario cpOrgaoUsuario = getTitular().getOrgaoUsuario();

		relatorio.getDataInicio().setTime(
				formatarDataHora(relatorio.getDataInicio(), "00:00:00"));
		relatorio.getDataFim().setTime(
				formatarDataHora(relatorio.getDataFim(), "23:59:59"));

		String qrl = "SELECT c.id, m.id, r.id " + "FROM Condutor c, Missao m "
				+ "INNER JOIN m.requisicoesTransporte r "
				+ "WHERE c.id = m.condutor.id "
				+ "and   dataHoraRetorno BETWEEN ? AND ? "
				+ "AND   r.cpOrgaoUsuario.idOrgaoUsu = ? "
				+ "ORDER BY c.id, m.id, r.id";

		Query qry = JPA.em().createQuery(qrl);
		qry.setParameter(1, relatorio.getDataInicio());
		qry.setParameter(2, relatorio.getDataFim());
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
			requisicao.setId(Long.parseLong(lista.get(i)[2].toString()));
			setRequisicao.add((RequisicaoTransporte) RequisicaoTransporte.AR
					.findById(requisicao.getId()));

			if (i < lista.size() - 1) {
				idProximoCondutor = Long.parseLong(lista.get(i + 1)[0]
						.toString());

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
				itemRc.requisicoes = new ArrayList<RequisicaoTransporte>(
						setRequisicao);
				listaRankingCondutor.add(itemRc);
				setMissao.clear();
				setRequisicao.clear();
				salvar = false;
			}
		}

		Collections.sort(listaRankingCondutor);

		return listaRankingCondutor;
	}

	@SuppressWarnings("unchecked")
	public List<RankingVeiculoRequisicao> retornarVeiculosQueAtenderamMaisRequisicoes(
			RelatorioRanking relatorio) throws ParseException, Exception {
		List<Object[]> lista;
		List<RankingVeiculoRequisicao> listaRankingVeiculo = new ArrayList<RankingVeiculoRequisicao>();
		Set<RequisicaoTransporte> setRequisicao = new HashSet<RequisicaoTransporte>();
		Veiculo veiculo = null;
		RequisicaoTransporte requisicao = null;
		CpOrgaoUsuario cpOrgaoUsuario = getTitular().getOrgaoUsuario();

			relatorio.getDataInicio().setTime(
					formatarDataHora(relatorio.getDataInicio(), "00:00:00"));
			relatorio.getDataFim().setTime(
					formatarDataHora(relatorio.getDataFim(), "23:59:59"));

			String qrl = "SELECT v.id, r.id " + "FROM Veiculo v, Missao m "
					+ "INNER JOIN m.requisicoesTransporte r "
					+ "WHERE v.id = m.veiculo.id "
					+ "and   dataHoraRetorno BETWEEN ? AND ? "
					+ "AND   r.cpOrgaoUsuario.idOrgaoUsu = ? "
					+ "ORDER BY v.id, r.id";

			Query qry = JPA.em().createQuery(qrl);
			qry.setParameter(1, relatorio.getDataInicio());
			qry.setParameter(2, relatorio.getDataFim());
			qry.setParameter(3, cpOrgaoUsuario.getIdOrgaoUsu());
			lista = (List<Object[]>) qry.getResultList();

			Long idProximoVeiculo = 0L;
			RankingVeiculoRequisicao itemRv = null;
			Boolean salvar = false;

			for (int i = 0; i < lista.size(); i++) {
				veiculo = new Veiculo();
				veiculo.setId(Long.parseLong(lista.get(i)[0].toString()));

				requisicao = new RequisicaoTransporte();
				requisicao.setId(Long.parseLong(lista.get(i)[1].toString()));
				setRequisicao
						.add((RequisicaoTransporte) RequisicaoTransporte.AR
								.findById(requisicao.getId()));

				if (i < lista.size() - 1) {
					idProximoVeiculo = Long.parseLong(lista.get(i + 1)[0]
							.toString());

					if (!veiculo.getId().equals(idProximoVeiculo)) {
						salvar = true;
					}
				} else {
					salvar = true;
				}

				if (salvar) {
					itemRv = new RelatorioRanking().new RankingVeiculoRequisicao();
					itemRv.veiculo = Veiculo.AR.findById(veiculo.getId());
					itemRv.requisicoes = new ArrayList<RequisicaoTransporte>(
							setRequisicao);
					listaRankingVeiculo.add(itemRv);
					setRequisicao.clear();
					salvar = false;
				}
			}

			Collections.sort(listaRankingVeiculo);

			listaRankingVeiculo = null;

		return listaRankingVeiculo;
	}

	@SuppressWarnings("unchecked")
	public List<RankingFinalidadeRequisicao> retornarFinalidadesComMaisRequisicoes(
			RelatorioRanking relatorio) throws ParseException, Exception {
		List<Object[]> lista;
		List<RankingFinalidadeRequisicao> listaRankingFinalidade = new ArrayList<RankingFinalidadeRequisicao>();
		FinalidadeRequisicao finalidade = null;
		int totalFinalidade = 0;
		CpOrgaoUsuario cpOrgaoUsuario = getTitular().getOrgaoUsuario();

			relatorio.getDataInicio().setTime(
					formatarDataHora(relatorio.getDataInicio(), "00:00:00"));
			relatorio.getDataFim().setTime(
					formatarDataHora(relatorio.getDataFim(), "23:59:59"));

			String qrl = "SELECT f.id, count(f.id) as total_finalidade "
					+ "FROM  FinalidadeRequisicao f, RequisicaoTransporte r "
					+ "WHERE r.tipoFinalidade.id = f.id "
					+ "and   r.dataHora BETWEEN ? AND ? "
					+ "AND   r.cpOrgaoUsuario.idOrgaoUsu = ? "
					+ "GROUP BY f.id " + "ORDER BY total_finalidade DESC";

			Query qry = JPA.em().createQuery(qrl);
			qry.setParameter(1, relatorio.getDataInicio());
			qry.setParameter(2, relatorio.getDataFim());
			qry.setParameter(3, cpOrgaoUsuario.getIdOrgaoUsu());

			lista = (List<Object[]>) qry.getResultList();
			RankingFinalidadeRequisicao itemRf = null;

			for (int i = 0; i < lista.size(); i++) {
				finalidade = new FinalidadeRequisicao();
				finalidade.setId(Long.parseLong(lista.get(i)[0].toString()));
				totalFinalidade = Integer.parseInt(lista.get(i)[1].toString());

				itemRf = new RelatorioRanking().new RankingFinalidadeRequisicao();
				itemRf.finalidade = FinalidadeRequisicao.AR.findById(finalidade
						.getId());
				itemRf.totalFinalidade = totalFinalidade;
				listaRankingFinalidade.add(itemRf);
			}

			listaRankingFinalidade = null;

		return listaRankingFinalidade;
	}

	public List<RankingTipoPassageiroRequisicao> retornarTipoPassageiroComMaisRequisicoes(
			RelatorioRanking relatorio) throws ParseException, Exception {
		List<RequisicaoTransporte> lista;
		List<RankingTipoPassageiroRequisicao> listaRankingTipoPassageiro = new ArrayList<RankingTipoPassageiroRequisicao>();
		List<TipoDePassageiro> listaTipoDePassageiro = Arrays
				.asList(TipoDePassageiro.values());
		CpOrgaoUsuario cpOrgaoUsuario = getTitular().getOrgaoUsuario();

			relatorio.getDataInicio().setTime(
					formatarDataHora(relatorio.getDataInicio(), "00:00:00"));
			relatorio.getDataFim().setTime(
					formatarDataHora(relatorio.getDataFim(), "23:59:59"));
			lista = RequisicaoTransporte.AR.find(
					"dataHora BETWEEN ? AND ? "
							+ "AND cpOrgaoUsuario.idOrgaoUsu = ? ",
					relatorio.getDataInicio(), relatorio.getDataFim(),
					cpOrgaoUsuario.getIdOrgaoUsu()).fetch();
			RankingTipoPassageiroRequisicao itemRp = null;

			for (int i = 0; i < listaTipoDePassageiro.size(); i++) {
				final TipoDePassageiro tipoPassageiro = listaTipoDePassageiro
						.get(i);

				if (lista.size() > 0) {
					List<RequisicaoTransporte> requisicoesFiltradas = Lists
							.newArrayList(Iterables.filter(lista,
									new Predicate<RequisicaoTransporte>() {
										public boolean apply(
												RequisicaoTransporte requisicao) {
											return requisicao
													.getTiposDePassageiro()
													.contains(tipoPassageiro);
										}
									}));

					itemRp = new RelatorioRanking().new RankingTipoPassageiroRequisicao();
					itemRp.tipoPassageiro = tipoPassageiro.getDescricao();
					itemRp.totalTipoPassageiros = requisicoesFiltradas.size();
					listaRankingTipoPassageiro.add(itemRp);
				}
			}

			Collections.sort(listaRankingTipoPassageiro);

			listaRankingTipoPassageiro = null;

		return listaRankingTipoPassageiro;
	}

	private static Date formatarDataHora(Calendar data, String hora)
			throws ParseException {
		String strDataPesquisa = "";
		SimpleDateFormat formatar = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		strDataPesquisa = String
				.format("%02d", data.get(Calendar.DAY_OF_MONTH))
				+ "/"
				+ String.format("%02d", data.get(Calendar.MONTH) + 1)
				+ "/"
				+ String.format("%04d", data.get(Calendar.YEAR));
		return formatar.parse(strDataPesquisa + " " + hora);
	}
}