package br.gov.jfrj.siga.tp.model;

import java.util.Calendar;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import play.data.binding.As;
import play.data.validation.Required;
import play.db.jpa.GenericModel;
import play.modules.br.jus.jfrj.siga.uteis.validadores.validarAnoData.ValidarAnoData;
import br.gov.jfrj.siga.dp.DpPessoa;

@Entity
@Audited
@Table(schema = "SIGATP")
public class ControleGabinete extends GenericModel {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence_generator") @SequenceGenerator(name = "hibernate_sequence_generator", sequenceName="SIGATP.hibernate_sequence") 
	public Long id;
	
	@As(lang={"*"}, value={"dd/MM/yyyy HH:mm"})
	@ValidarAnoData(descricaoCampo="Data/Hora")
	public Calendar dataHora;
	
	@Required
	@As(lang={"*"}, value={"dd/MM/yyyy HH:mm"})
	@ValidarAnoData(descricaoCampo="Data/Hora Saida")
	public Calendar dataHoraSaida;
	
	@Required
	@As(lang={"*"}, value={"dd/MM/yyyy HH:mm"})
	@ValidarAnoData(descricaoCampo="Data/Hora Retorno")
	public Calendar dataHoraRetorno;
	
	@Required
	public String itinerario;
	
	@Required
	@ManyToOne
	@NotNull
	public Veiculo veiculo;
	
	@Required
	@ManyToOne
	@NotNull
	public Condutor condutor;	
	
	@Required
	public double odometroEmKmSaida;
	
	@Required
	public double odometroEmKmRetorno;
	
	@Required
	public String naturezaDoServico;
	
	public String ocorrencias;
	
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@JoinColumn(name = "ID_TITULAR")
	public DpPessoa titular;
	
	@NotNull
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@JoinColumn(name = "ID_SOLICITANTE")
	public DpPessoa solicitante;
	
	public static List<ControleGabinete> buscarTodosPorVeiculo(Veiculo veiculo){
		return ControleGabinete.find("veiculo", veiculo).fetch();
	}
	
	public static double buscarUltimoOdometroPorVeiculo(Veiculo veiculo, ControleGabinete controleGabinete){
		double retorno = 0;
		try {
			retorno = ((ControleGabinete) ControleGabinete.find("veiculo = ? and id <> ? order by id desc", veiculo, controleGabinete.id).fetch().get(0)).odometroEmKmRetorno;
		} catch (Exception e) {
		}
		return retorno;
	}
		
	public ControleGabinete(){
		this.id = new Long(0);
	}

	public static List<ControleGabinete> listarTodos() {
		return ControleGabinete.findAll();
	}

	public static List<ControleGabinete> listarPorCondutor(Condutor condutor) {
		return ControleGabinete.find("condutor", condutor).fetch();
	}
}
