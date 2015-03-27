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

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import br.gov.jfrj.siga.cp.CpComplexo;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import play.data.binding.As;
import play.data.validation.*;
import play.db.jpa.GenericModel;
import play.modules.br.jus.jfrj.siga.uteis.validadores.validarAnoData.ValidarAnoData;

@SuppressWarnings("serial")
@Entity
@Audited
@Table(schema = "SIGATP")
public class Parametro extends GenericModel {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence_generator") 
	@SequenceGenerator(name = "hibernate_sequence_generator", sequenceName="SIGATP.hibernate_sequence") 
	public Long id;
	
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@JoinColumn(name = "ID_ORGAO_USU")
	public CpOrgaoUsuario cpOrgaoUsuario;
	
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@JoinColumn(name = "ID_LOTACAO")
	public DpLotacao dpLotacao;
	
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@JoinColumn(name = "ID_PESSOA")
	public DpPessoa dpPessoa; 
	
 	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@JoinColumn(name = "ID_COMPLEXO")
	public CpComplexo cpComplexo;
 	
 	@Required
 	public String nomeParametro;
 	
 	@Required
 	public String valorParametro;
 	
 	@As(lang={"*"}, value={"dd/MM/yyyy"})
	@ValidarAnoData(descricaoCampo="Data de Inicio")
	public Calendar dataInicio;
 	
 	@As(lang={"*"}, value={"dd/MM/yyyy"})
	@ValidarAnoData(descricaoCampo="Data do Fim")
	public Calendar dataFim;
 	
	public Parametro() {
		this.id = new Long(0);
	}

	public static List<Parametro> listarTodos() {
		return Parametro.findAll();
	}
	

	public static Parametro buscar(Long idBuscar) {
		Parametro retorno = null;
		try {
			retorno = Parametro.find("id = ?", idBuscar).first();
		} catch (Exception e) {
			return null;
		}
		return retorno;
	}
}
