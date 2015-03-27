package br.gov.jfrj.siga.tp.model;

import java.util.Calendar;

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
import org.hibernate.envers.RelationTargetAuditMode;



import play.data.binding.As;
import play.data.validation.Required;
import play.db.jpa.GenericModel;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.jus.jfrj.siga.uteis.UpperCase;

@SuppressWarnings("serial")
@Entity
@Audited
@Table(schema = "SIGATP")
public class Andamento extends GenericModel implements Comparable<Andamento> {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence_generator") @SequenceGenerator(name = "hibernate_sequence_generator", sequenceName="SIGATP.hibernate_sequence") 
	public Long id;
	
	@UpperCase
	public String descricao;
	
	@NotNull
	@As(lang={"*"}, value={"dd/MM/yyyy"})
	public Calendar dataAndamento;
	
 	@As(lang={"*"}, value={"dd/MM/yyyy"})
 	public Calendar dataNotificacaoWorkFlow;
	
	@Required
	@NotNull
	@Enumerated(EnumType.STRING)
	public EstadoRequisicao estadoRequisicao;
	
	@NotNull
	@ManyToOne
	public RequisicaoTransporte requisicaoTransporte;
	
	@ManyToOne
	public Missao missao;
	
	@NotNull
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	public DpPessoa responsavel;
	
	@Override
	public int compareTo(Andamento o) {
		int retorno = this.dataAndamento.compareTo(o.dataAndamento);
		
		return retorno;
	}
}
