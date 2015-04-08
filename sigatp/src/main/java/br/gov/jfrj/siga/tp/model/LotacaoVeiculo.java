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
import play.modules.br.jus.jfrj.siga.uteis.validadores.validarAnoData.ValidarAnoData;
import br.com.caelum.vraptor.Convert;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.model.ActiveRecord;
import br.gov.jfrj.siga.tp.binder.DoubleConverter;
import br.gov.jfrj.siga.tp.util.TpMessages;

@Entity
// @Table(name = "LOTACAO_VEICULO_2", schema="SIGAOR")
@Audited
@Table(schema = "SIGATP")
public class LotacaoVeiculo extends TpModel {

	private static final long serialVersionUID = 1912137163976035054L;
	public static ActiveRecord<LotacaoVeiculo> AR = new ActiveRecord<>(LotacaoVeiculo.class);

	public LotacaoVeiculo() {
	}

	public LotacaoVeiculo(Long id, Veiculo veiculo, DpLotacao lotacao, Calendar dataHoraInicio, Calendar dataHoraFim, double odometroEmKm) {
		super();
		this.id = id;
		this.veiculo = veiculo;
		this.lotacao = lotacao;
		this.dataHoraInicio = dataHoraInicio;
		this.dataHoraFim = dataHoraFim;
		this.odometroEmKm = odometroEmKm;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence_generator")
	@SequenceGenerator(name = "hibernate_sequence_generator", sequenceName = "SIGATP.hibernate_sequence")
	private Long id;

	@ManyToOne
	@NotNull
	private Veiculo veiculo;

	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@NotNull
	@JoinColumn(name = "ID_LOTA_SOLICITANTE")
	private DpLotacao lotacao;

	@Required
	@NotNull
	@ValidarAnoData(descricaoCampo = "Data/Hora Inicio")
	private Calendar dataHoraInicio;

	@As(lang = { "*" }, value = { "dd/MM/yyyy HH:mm" })
	@ValidarAnoData(descricaoCampo = "Data/Hora Fim")
	private Calendar dataHoraFim;

	@Convert(DoubleConverter.class)
	private Double odometroEmKm;

	/**
	 * Inclui a nova lotação do veículo e preenche a data fim da lotação anterior
	 * 
	 * @param veiculo
	 */
	public static String atualizarDataFimLotacaoAnterior(Veiculo veiculo) throws Exception {
		try {
			List<LotacaoVeiculo> lotacoesVeiculo = LotacaoVeiculo.AR.find("id = ? and dataHoraFim is null order by dataHoraInicio DESC", veiculo.getId()).fetch();
			if (lotacoesVeiculo.size() == 1) {
				lotacoesVeiculo.get(0).dataHoraFim = Calendar.getInstance();
				lotacoesVeiculo.get(0).save();
			} else {
				if (lotacoesVeiculo.size() > 1) {
					throw new Exception(TpMessages.getMessage("lotacaoVeiculo.lotacoesVeiculo.MaiorQueUm.exception"));
				}
			}
		} catch (Exception e) {
			throw new Exception(TpMessages.getMessage("lotacaoVeiculo.lotacoesVeiculo.exception", e.getMessage()));
		}

		return "ok";
	}

	public static List<LotacaoVeiculo> buscarTodosPorVeiculo(Veiculo veiculo) {
		return LotacaoVeiculo.AR.find("veiculo = ? order by dataHoraInicio DESC", veiculo).fetch();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Veiculo getVeiculo() {
		return veiculo;
	}

	public void setVeiculo(Veiculo veiculo) {
		this.veiculo = veiculo;
	}

	public DpLotacao getLotacao() {
		return lotacao;
	}

	public void setLotacao(DpLotacao lotacao) {
		this.lotacao = lotacao;
	}

	public Calendar getDataHoraInicio() {
		return dataHoraInicio;
	}

	public void setDataHoraInicio(Calendar dataHoraInicio) {
		this.dataHoraInicio = dataHoraInicio;
	}

	public Calendar getDataHoraFim() {
		return dataHoraFim;
	}

	public void setDataHoraFim(Calendar dataHoraFim) {
		this.dataHoraFim = dataHoraFim;
	}

	public Double getOdometroEmKm() {
		return odometroEmKm;
	}

	public void setOdometroEmKm(Double odometroEmKm) {
		this.odometroEmKm = odometroEmKm;
	}
}
