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
import br.gov.jfrj.siga.model.ActiveRecord;

@SuppressWarnings("serial")
@Entity
@Audited
@Table(schema = "SIGATP")
public class Penalidade extends TpModel {
	private static final long _ID_DA_PENALIDADE_OUTRA = -1;

	public static final ActiveRecord<Penalidade> AR = new ActiveRecord<>(Penalidade.class);

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
		return Penalidade.AR.findAll();
	}

	public static List<Penalidade> listarTodos(CpOrgaoUsuario orgaoUsuario) {
		return Penalidade.AR.find("cpOrgaoOrigem = ? and id <> ?", orgaoUsuario, _ID_DA_PENALIDADE_OUTRA).fetch();
	}

	public static Penalidade buscar(Long idBuscar) {
		Penalidade retorno = null;
		try {
			retorno = Penalidade.AR.find("id = ?", idBuscar).first();
		} catch (Exception e) {
			return null;
		}
		return retorno;
	}

	@Override
	public Long getId() {
		// TODO Auto-generated method stub
		return null;
	}

}
