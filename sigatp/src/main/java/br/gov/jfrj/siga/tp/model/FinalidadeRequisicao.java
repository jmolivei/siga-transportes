package br.gov.jfrj.siga.tp.model;

import java.util.List;

import javax.persistence.Column;
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

import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.GenericModel;
import play.i18n.Messages;
import play.modules.br.jus.jfrj.siga.uteis.validadores.upperCase.UpperCase;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import controllers.AutorizacaoGI;

@SuppressWarnings("serial")
@Entity
@Audited
@Table(name="FinalidadeRequisicao", schema = "SIGATP")
public class FinalidadeRequisicao extends GenericModel {
	
	private static final long _ID_DA_FINALIDADE_OUTRA = -1;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence_generator") 
	@SequenceGenerator(name = "hibernate_sequence_generator", sequenceName="SIGATP.hibernate_sequence") 
	public Long id;
	
	@Required
	@Unique(message="finalidadeRequisicao.descricao.unique")
	@Column(unique=true)
	@UpperCase
 	public String descricao;
	
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@JoinColumn(name = "ID_ORGAO_ORI")
	public CpOrgaoUsuario cpOrgaoOrigem;
 	
 	public FinalidadeRequisicao() {
		this.id = new Long(0);
	}
 	
 	public static FinalidadeRequisicao buscar(String descricaoBuscar) {
 		FinalidadeRequisicao retorno = null;
 		try {
 			retorno = FinalidadeRequisicao.find("descricao = ?", descricaoBuscar).first();
		} catch (Exception e) {
			return null;
		}
 		return retorno;
 	}
 	
 	public static FinalidadeRequisicao buscar(Long idBuscar) {
 		FinalidadeRequisicao retorno = null;
 		try {
 			retorno = FinalidadeRequisicao.find("id = ?", idBuscar).first();
		} catch (Exception e) {
			return null;
		}
 		return retorno;
 	}
 	
	public static List<FinalidadeRequisicao> listarTodos(CpOrgaoUsuario orgaoUsuario) {
		return FinalidadeRequisicao.find("cpOrgaoOrigem = ? and id <> ?", orgaoUsuario, _ID_DA_FINALIDADE_OUTRA).fetch();
	}

	public static List<FinalidadeRequisicao> listarTodos() {
		
		return FinalidadeRequisicao.findAll();
	}

	public void checarProprietario(CpOrgaoUsuario orgaoUsuario) throws Exception {
		if ((!this.cpOrgaoOrigem.equivale(orgaoUsuario)) || (this.id.equals(_ID_DA_FINALIDADE_OUTRA))) {
			try {
				throw new Exception(Messages.get("finalidadeRequisicao.checarProprietario.exception"));
			} catch (Exception e) {
				AutorizacaoGI.tratarExcecoes(e);
			}
		}
	}
	
	public boolean ehOutra() {
		if(this.id.equals(_ID_DA_FINALIDADE_OUTRA)) {
			return true;
		}
		return false;
	}
	

}
