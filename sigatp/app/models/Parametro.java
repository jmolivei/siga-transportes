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
import javax.persistence.Transient;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import play.data.binding.As;
import play.data.validation.Required;
import play.db.jpa.GenericModel;
import play.modules.br.jus.jfrj.siga.uteis.validadores.validarAnoData.ValidarAnoData;
import br.gov.jfrj.siga.cp.CpComplexo;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;

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
 	
 	@Required
 	public String descricao;
 	
 	@As(lang={"*"}, value={"dd/MM/yyyy"})
	@ValidarAnoData(descricaoCampo="Data de Inicio")
	public Calendar dataInicio;
 	
 	@As(lang={"*"}, value={"dd/MM/yyyy"})
	@ValidarAnoData(descricaoCampo="Data do Fim")
	public Calendar dataFim;
 	
 	@Transient
 	public NivelDeParametro nivel;
 	
 	public Parametro() {
		this.id = new Long(0);
	}

	public static List<Parametro> listarTodos() {
		List<Parametro> parametros =  Parametro.findAll();
		
		configurarNivel(parametros);
		
		return parametros;
	}
	
	private static void configurarNivel(Parametro parametro) {
		if(parametro.dpPessoa != null) {
			parametro.nivel = NivelDeParametro.USUARIO;
		} else if(parametro.dpLotacao != null) {
			parametro.nivel = NivelDeParametro.LOTACAO;
		} else if(parametro.cpComplexo != null) {
			parametro.nivel = NivelDeParametro.COMPLEXO;
		} else if(parametro.cpOrgaoUsuario != null) {
			parametro.nivel = NivelDeParametro.ORGAO;
		} else {
			parametro.nivel = NivelDeParametro.GERAL;
		}
	}
	
	private static void configurarNivel(List<Parametro> parametros) {
		for (Parametro parametro : parametros) {
			configurarNivel(parametro);
		}
	}
	
	public static Parametro buscar(Long idBuscar) {
		Parametro retorno = null;
		try {
			retorno = Parametro.findById(idBuscar);
			configurarNivel(retorno);
		} catch (Exception e) {
			return null;
		}
		return retorno;
	}

	public static String buscarValorEmVigor(String nome, DpPessoa usuario, CpComplexo complexoPadrao) {
		String retorno = null;
		Calendar hoje = Calendar.getInstance();
		String queryComData = "nomeParametro = ? "
				+ "and dataInicio < ? "
				+ "and (dataFim is null or dataFim > ?) "
				+ "and (dpPessoa is null or dpPessoa = ?) "
				+ "and (dpLotacao is null or dpLotacao = ?) "
				+ "and (cpComplexo is null or cpComplexo = ?) "
				+ "and (cpOrgaoUsuario is null or cpOrgaoUsuario = ?)";
		
		List<Parametro> parametros = Parametro.find(queryComData, 
													nome, 
													hoje, 
													hoje, 
													usuario, 
													usuario.getLotacao(), 
													complexoPadrao, 
													usuario.getOrgaoUsuario()).fetch();
		configurarNivel(parametros);
		
		if((parametros != null) && !(parametros.isEmpty())) {
			if(parametros.size() == 1) {
				retorno = parametros.get(0).valorParametro;
			} else {
				NivelDeParametro nivel = NivelDeParametro.GERAL;
				for (Parametro parametro : parametros) {
					boolean teste = (parametro.dpPessoa != null);
					if(teste) {
						if(parametro.dpPessoa.equivale(usuario)) {
							retorno = parametro.valorParametro;
							break;
						}
					} else {
						if(parametro.nivel.compareTo(nivel) >= 0) {
							retorno = parametro.valorParametro;
							nivel = parametro.nivel;
						}
					}
				}
			}
		}
		return retorno;
	}
	
	public static String buscarConfigSistemaEmVigor(String nome) {
		String retorno = null;
		Calendar hoje = Calendar.getInstance();
		String queryComData = "nomeParametro = ? "
				+ "and dataInicio < ? "
				+ "and (dataFim is null or dataFim > ?) "
				+ "and dpPessoa is null "
				+ "and dpLotacao is null "
				+ "and cpComplexo is null "
				+ "and cpOrgaoUsuario is null";
		
		Parametro parametro = Parametro.find(queryComData, 
													nome, 
													hoje, 
													hoje).first();
		if(parametro != null) {
			retorno = parametro.valorParametro;
		}
		return retorno;
	}
	
	
}
