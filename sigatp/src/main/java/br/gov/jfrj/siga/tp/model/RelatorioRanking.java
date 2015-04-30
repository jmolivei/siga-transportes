package br.gov.jfrj.siga.tp.model;

import java.util.Calendar;
import java.util.List;

import play.data.binding.As;
import play.data.validation.Required;

public class RelatorioRanking {
	@Required
	@As(lang={"*"}, value={"dd/MM/yyyy"})
	public Calendar dataInicio;
	
	@Required
	@As(lang={"*"}, value={"dd/MM/yyyy"})
	public Calendar dataFim;
	
	@Required
	public int quantidadeDadosRetorno;

	public int getQuantidadeDadosRetorno() {
		return quantidadeDadosRetorno;
	}

	public class RankingCondutorRequisicao implements Comparable<RankingCondutorRequisicao> {
		public Condutor condutor;
		public List<Missao> missoes;
		public List<RequisicaoTransporte> requisicoes;

		@Override
		public int compareTo(RankingCondutorRequisicao o) {
			if (!this.condutor.equals(o.condutor)) {
				if (this.missoes.size() == o.missoes.size()) {
					return (this.requisicoes.size() > o.requisicoes.size()) ? -1 : 1;
				} else {
					return (this.missoes.size() > o.missoes.size()) ? -1 : 1;
				}
			}
			return 0;
		}
	}

	public class RankingVeiculoRequisicao implements Comparable<RankingVeiculoRequisicao> {
		public Veiculo veiculo;
		public List<RequisicaoTransporte> requisicoes;

		@Override
		public int compareTo(RankingVeiculoRequisicao o) {
			if (!this.veiculo.equals(o.veiculo)) {
				return (this.requisicoes.size() > o.requisicoes.size()) ? -1 : 1;
			}
			return 0;
		}
	}

	public class RankingFinalidadeRequisicao  {
		public FinalidadeRequisicao finalidade;
		public int totalFinalidade;
	}

	public class RankingTipoPassageiroRequisicao implements Comparable<RankingTipoPassageiroRequisicao> {
		public String tipoPassageiro;
		public int totalTipoPassageiros;
		
		@Override
		public int compareTo(RankingTipoPassageiroRequisicao o)  {
			if (!this.tipoPassageiro.equals(o.tipoPassageiro)) {
				return (this.totalTipoPassageiros > o.totalTipoPassageiros) ? -1 : 1;
			}
			return 0;
		}
	}
}