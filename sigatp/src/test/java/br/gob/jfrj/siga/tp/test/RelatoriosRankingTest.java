package br.gob.jfrj.siga.tp.test;
import java.util.Calendar;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import br.gov.jfrj.siga.tp.model.Missao;
import br.gov.jfrj.siga.tp.model.RelatorioRanking;
import br.gov.jfrj.siga.tp.model.RequisicaoTransporte;
import controllers.RelatoriosRanking;

public class RelatoriosRankingTest {

	private RelatorioRanking iniciarClasse() {
		RelatorioRanking relatorio = new RelatorioRanking();
		relatorio.setQuantidadeDadosRetorno(2);

		relatorio.setDataInicio(Calendar.getInstance());
		relatorio.getDataInicio().set(2014, 9, 1); // 01/10/2014

		relatorio.setDataFim(Calendar.getInstance());
		relatorio.getDataFim().set(2014, 10, 11); // 11/11/2014

		return relatorio;
	}

	@Test
	public void testRetornarCondutoresQueAtenderamMaisRequisicoes() {
		List<br.gov.jfrj.siga.tp.model.RelatorioRanking.RankingCondutorRequisicao> rankingCondutor = null;

		try {
			RelatorioRanking relatorio = iniciarClasse();
			String linha = "";
			rankingCondutor = RelatoriosRanking.retornarCondutoresQueAtenderamMaisRequisicoes(relatorio);

			for (br.gov.jfrj.siga.tp.model.RelatorioRanking.RankingCondutorRequisicao item : rankingCondutor) {
				linha = "Condutor : " + item.getCondutor().getNome() + " ";
				linha += "Missoes : ";

				for (Missao missao : item.getMissoes()) {
					linha += missao.getSequence() + " ";
				}

				linha += "Requisicoes : ";

				for (RequisicaoTransporte requisicao : item.getRequisicoes()) {
					linha += requisicao.buscarSequence() + " ";
				}

				System.out.println(linha);
			}

		} catch (Exception e) {
			System.out.println(e.getMessage().toString());
		}

		Assert.assertTrue(rankingCondutor != null);
	}

	@Test
	public void testRetornarVeiculosQueAtenderamMaisRequisicoes() {
		List<br.gov.jfrj.siga.tp.model.RelatorioRanking.RankingVeiculoRequisicao> rankingVeiculo = null;

		try {
			RelatorioRanking relatorio = iniciarClasse();
			String linha = "";
			rankingVeiculo = RelatoriosRanking.retornarVeiculosQueAtenderamMaisRequisicoes(relatorio);

			for (br.gov.jfrj.siga.tp.model.RelatorioRanking.RankingVeiculoRequisicao item : rankingVeiculo) {
				linha = "Veiculo : " + item.getVeiculo().getPlaca() + " ";
				linha += "Requisicoes : ";

				for (RequisicaoTransporte requisicao : item.getRequisicoes()) {
					linha += requisicao.buscarSequence() + " ";
				}

				System.out.println(linha);
			}

		} catch (Exception e) {
			System.out.println(e.getMessage().toString());
		}

		Assert.assertTrue(rankingVeiculo != null);
	}

	@Test
	public void testRetornarFinalidadesQueAtenderamMaisRequisicoes() {
		List<br.gov.jfrj.siga.tp.model.RelatorioRanking.RankingFinalidadeRequisicao> rankingFinalidade = null;

		try {
			RelatorioRanking relatorio = iniciarClasse();
			String linha = "";
			rankingFinalidade = RelatoriosRanking.retornarFinalidadesComMaisRequisicoes(relatorio);

			for (br.gov.jfrj.siga.tp.model.RelatorioRanking.RankingFinalidadeRequisicao item : rankingFinalidade) {
				linha = "Finalidade : " + item.getFinalidade().getDescricao() + " ";
				linha += "Total : " + item.getTotalFinalidade() + " ";
				System.out.println(linha);
			}

		} catch (Exception e) {
			System.out.println(e.getMessage().toString());
		}

		Assert.assertTrue(rankingFinalidade != null);
	}

	@Test
	public void testRetornarTiposDePassageiroQueAtenderamMaisRequisicoes() {
		List<br.gov.jfrj.siga.tp.model.RelatorioRanking.RankingTipoPassageiroRequisicao> rankingTipoDePassageiro = null;

		try {
			RelatorioRanking relatorio = iniciarClasse();
			String linha = "";
			rankingTipoDePassageiro = RelatoriosRanking.retornarTipoPassageiroComMaisRequisicoes(relatorio);

			for (br.gov.jfrj.siga.tp.model.RelatorioRanking.RankingTipoPassageiroRequisicao item : rankingTipoDePassageiro) {
				linha = "Tipo de passageiro : " + item.getTipoPassageiro() + " ";
				linha += "Total : " + item.getTotalTipoPassageiros() + " ";
				System.out.println(linha);
			}

		} catch (Exception e) {
			System.out.println(e.getMessage().toString());
		}

		Assert.assertTrue(rankingTipoDePassageiro != null);
	}
}