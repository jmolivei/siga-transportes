package br.gov.jfrj.siga.tp.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.GenericModel;
import play.modules.br.jus.jfrj.siga.uteis.validadores.upperCase.UpperCase;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;

@SuppressWarnings("serial")
@Entity
@Audited
@Table(schema = "SIGATP")
public class Penalidade extends GenericModel {
	private static final long _ID_DA_PENALIDADE_OUTRA = -1;

	@Id
	@GeneratedValue
	public Long id;

	@Required
	@Unique(message="Campo 'C&oacute;digo da Infra&ccedil;&atilde;o' j&aacute; existente")
	@UpperCase
	public String codigoInfracao;
	
	@Required
	@UpperCase
	public String descricaoInfracao;
	
	@Required
	@UpperCase	
	public String artigoCTB;
	
	@Required
	public double valor;
	
	@Required
	@Enumerated(EnumType.STRING)
	public Infrator infrator;
	
	@Required
	@Enumerated(EnumType.STRING)
	public Gravidade classificacao;
		
	
	public Penalidade() {
		this.id = new Long(0);
		this.infrator = Infrator.CONDUTOR;
		this.classificacao = Gravidade.LEVE;
	}

	public static List<Penalidade> listarTodos() {
		return Penalidade.findAll();
	}
	
	public static List<Penalidade> listarTodos(CpOrgaoUsuario orgaoUsuario) {
		return Penalidade.find("cpOrgaoOrigem = ? and id <> ?", orgaoUsuario, _ID_DA_PENALIDADE_OUTRA).fetch();
	}

	public static Penalidade buscar(Long idBuscar) {
		Penalidade retorno = null;
		try {
			retorno = Penalidade.find("id = ?", idBuscar).first();
		} catch (Exception e) {
			return null;
		}
		return retorno;
	}

}
