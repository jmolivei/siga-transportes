package models;

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
import play.i18n.Messages;
import play.modules.br.jus.jfrj.siga.uteis.validadores.validarAnoData.ValidarAnoData;
import binders.DoubleBinder;
import br.gov.jfrj.siga.dp.DpLotacao;

@Entity
//@Table(name = "LOTACAO_VEICULO_2", schema="SIGAOR")
@Audited
@Table(schema = "SIGATP")
public class LotacaoVeiculo extends GenericModel {
	public LotacaoVeiculo() {
		
	}
	
	public LotacaoVeiculo(Long id, Veiculo veiculo, DpLotacao lotacao,
			Calendar dataHoraInicio, Calendar dataHoraFim, double odometroEmKm) {
		super();
		this.id = id;
		this.veiculo = veiculo;
		this.lotacao = lotacao;
		this.dataHoraInicio = dataHoraInicio;
		this.dataHoraFim = dataHoraFim;
		this.odometroEmKm = odometroEmKm;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence_generator") @SequenceGenerator(name = "hibernate_sequence_generator", sequenceName="SIGATP.hibernate_sequence") 
	public Long id;
	
	@ManyToOne
	@NotNull
	public Veiculo veiculo;
	
 	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@NotNull
	@JoinColumn(name = "ID_LOTA_SOLICITANTE")
	public DpLotacao lotacao;
	
	@Required
	@NotNull
	@ValidarAnoData(descricaoCampo="Data/Hora Inicio")
	public Calendar dataHoraInicio;
	
	@As(lang={"*"}, value={"dd/MM/yyyy HH:mm"})
	@ValidarAnoData(descricaoCampo="Data/Hora Fim")
	public Calendar dataHoraFim;
	
	@As(binder=DoubleBinder.class)
	public Double odometroEmKm;
	
	/**
	 * Inclui a nova lotação do veículo e preenche a data fim da lotação anterior 
	 * 
	 * @param veiculo
	 */
	public static String atualizarDataFimLotacaoAnterior(Veiculo veiculo) throws Exception {
     	try { 
    		List<LotacaoVeiculo> lotacoesVeiculo = LotacaoVeiculo.find("veiculo = ? and dataHoraFim is null order by dataHoraInicio DESC", veiculo).fetch();
    		if(lotacoesVeiculo.size() == 1) {
    			lotacoesVeiculo.get(0).dataHoraFim = Calendar.getInstance();
    			lotacoesVeiculo.get(0).save();
    		} else {
    			if(lotacoesVeiculo.size() > 1) {
    			   throw new Exception(Messages.get("lotacaoVeiculo.lotacoesVeiculo.MaiorQueUm.exception"));
    			}
    		}
		} catch (Exception e) {
			throw new Exception(Messages.get("lotacaoVeiculo.lotacoesVeiculo.exception", e.getMessage()));
		}

		return "ok";
	}

	public static List<LotacaoVeiculo> buscarTodosPorVeiculo(Veiculo veiculo) {
		return LotacaoVeiculo.find("veiculo = ? order by dataHoraInicio DESC", veiculo).fetch();
	}
}
