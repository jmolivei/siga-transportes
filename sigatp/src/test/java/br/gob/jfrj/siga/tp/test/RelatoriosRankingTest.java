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
		relatorio.quantidadeDadosRetorno = 2;

		relatorio.dataInicio = Calendar.getInstance();
		relatorio.dataInicio.set(2014, 9, 1); // 01/10/2014

		relatorio.dataFim = Calendar.getInstance();
		relatorio.dataFim.set(2014, 10, 11); // 11/11/2014

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
				linha = "Condutor : " + item.condutor.getNome() + " ";
				linha += "Missoes : ";

				for (Missao missao : item.missoes) {
					linha += missao.getSequence() + " ";
				}

				linha += "Requisicoes : ";

				for (RequisicaoTransporte requisicao : item.requisicoes) {
					linha += requisicao.getSequence() + " ";
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
				linha = "Veiculo : " + item.veiculo.getPlaca() + " ";
				linha += "Requisicoes : ";

				for (RequisicaoTransporte requisicao : item.requisicoes) {
					linha += requisicao.getSequence() + " ";
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
				linha = "Finalidade : " + item.finalidade.getDescricao() + " ";
				linha += "Total : " + item.totalFinalidade + " ";
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
				linha = "Tipo de passageiro : " + item.tipoPassageiro + " ";
				linha += "Total : " + item.totalTipoPassageiros + " ";
				System.out.println(linha);
			}

		} catch (Exception e) {
			System.out.println(e.getMessage().toString());
		}

		Assert.assertTrue(rankingTipoDePassageiro != null);
	}
}