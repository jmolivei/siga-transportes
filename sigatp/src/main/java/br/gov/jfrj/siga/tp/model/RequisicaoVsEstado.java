package br.gov.jfrj.siga.tp.model;

public class RequisicaoVsEstado  {
	
	public Long idRequisicaoTransporte;
	public EstadoRequisicao estado;
	
	public static EstadoRequisicao encontrarEstadoNaLista(RequisicaoVsEstado[] vetor, Long idRequisicao) {
		for (int i = 0; i < vetor.length; i++) {
			RequisicaoVsEstado re = vetor[i];
			if(re.idRequisicaoTransporte.equals(idRequisicao))
				return re.estado;
		}
		return null;
	}

}
