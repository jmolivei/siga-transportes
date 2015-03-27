package br.gov.jfrj.siga.tp.model;

import java.util.Calendar;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import play.data.binding.As;
import play.data.validation.Required;
import play.db.jpa.GenericModel;
import play.modules.br.jus.jfrj.siga.uteis.validadores.validarAnoData.ValidarAnoData;
import br.gov.jfrj.siga.tp.binder.DoubleBinder;
import br.gov.jfrj.siga.tp.util.PerguntaSimNao;
import br.jus.jfrj.siga.uteis.UpperCase;

@Entity
@Audited
@Table(schema = "SIGATP")
public class RelatorioDiario extends GenericModel {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence_generator") @SequenceGenerator(name = "hibernate_sequence_generator", sequenceName="SIGATP.hibernate_sequence") 
	public Long id;
	
	@Required
	@As(lang={"*"}, value={"dd/MM/yyyy"})
	@ValidarAnoData(descricaoCampo="Data")
	public Calendar data;
	
	@Required
	@ManyToOne
	@NotNull
	public Veiculo veiculo;	
	
	@Required
	@As(binder=DoubleBinder.class)
	public double odometroEmKm;
	
	@Required
	@Enumerated(EnumType.STRING)
	public NivelDeCombustivel nivelDeCombustivel;
	
	@Enumerated(EnumType.STRING)
	public PerguntaSimNao equipamentoObrigatorio;
	
	@Enumerated(EnumType.STRING)
	public PerguntaSimNao cartoes;
	
	@UpperCase
	public String observacao;
	
	public static List<RelatorioDiario> buscarTodosPorVeiculo(Veiculo veiculo){
		return RelatorioDiario.find("veiculo", veiculo).fetch();
	}
	
	public RelatorioDiario(){
		this.id = new Long(0);
		this.nivelDeCombustivel = NivelDeCombustivel.I;
		this.equipamentoObrigatorio = PerguntaSimNao.SIM;
		this.cartoes = PerguntaSimNao.SIM;
	}
	
	public RelatorioDiario(Long id, Calendar data,
			NivelDeCombustivel nivelDeCombustivel,
			double odometroEmKm, PerguntaSimNao equipamentoObrigatorio,  PerguntaSimNao cartoes,
			String observacao) {
		super();
		this.id = id;
		this.data = data;
		this.nivelDeCombustivel = nivelDeCombustivel;
		this.odometroEmKm = odometroEmKm;
		this.equipamentoObrigatorio = equipamentoObrigatorio;
		this.cartoes = cartoes;
		this.observacao = observacao;
	}

	

}
