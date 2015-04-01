package br.gov.jfrj.siga.tp.model;

import java.util.Calendar;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import play.data.binding.As;
import play.data.validation.Required;
import play.db.jpa.GenericModel;
import play.db.jpa.JPA;
import play.modules.br.jus.jfrj.siga.uteis.validadores.upperCase.UpperCase;
import play.modules.br.jus.jfrj.siga.uteis.validadores.validarAnoData.ValidarAnoData;


@SuppressWarnings("serial")
@Entity
@Audited
@Table(schema = "SIGATP")
public class Afastamento extends GenericModel  {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence_generator") @SequenceGenerator(name = "hibernate_sequence_generator", sequenceName="SIGATP.hibernate_sequence") 
	public long id;
		
	
	@Required
	@ManyToOne
	@NotNull
	public Condutor condutor;
	
	
	@Required
	@UpperCase
	@NotNull
	public String descricao;
	
	
	@Required
	@As(lang={"*"}, value={"dd/MM/yyyy HH:mm"})
	@NotNull
	@ValidarAnoData(descricaoCampo="Data/Hora Inicio")
	public Calendar dataHoraInicio;
	
	
	@Required
	@As(lang={"*"}, value={"dd/MM/yyyy HH:mm"})
	@NotNull
	@ValidarAnoData(descricaoCampo="Data/Hora Fim")
	public Calendar dataHoraFim;
	
	
	public static List<Afastamento> buscarTodosPorCondutor(Condutor condutor){
		return Afastamento.find("condutor", condutor).fetch();
	}
	
	
	public Afastamento(){
		
	}


	public Afastamento(long id, Condutor condutor, String descricao,
			Calendar dataHoraInicio, Calendar dataHoraFim) {
		super();
		this.id = id;
		this.condutor = condutor;
		this.descricao = descricao;
		this.dataHoraInicio = dataHoraInicio;
		this.dataHoraFim = dataHoraFim;
	}
	
	@SuppressWarnings("unchecked")
	private static List<Afastamento> retornarLista(String qrl) throws NoResultException {
		List<Afastamento> afastamentos;
		Query qry = JPA.em().createQuery(qrl);
		try {
			afastamentos = (List<Afastamento>) qry.getResultList();
		} catch(NoResultException ex) {
			afastamentos = null;
		}
		return afastamentos; 
	}
	
	public static List<Afastamento> buscarPorCondutores(Long IdCondutor, String dataHoraInicio){
		String dataFormatadaOracle = "to_date('" + dataHoraInicio + "', 'DD/MM/YYYY')";
		String filtroCondutor = "";

		if (IdCondutor != null) {
			filtroCondutor = "condutor.id = " + IdCondutor + " AND ";  
		}
		
		String qrl = 	"SELECT a FROM Afastamento a " +
		                " WHERE " + filtroCondutor +
						" trunc(dataHoraInicio) <= trunc(" + dataFormatadaOracle + ")" +  	
						" AND (dataHoraFim IS NULL OR trunc(dataHoraFim) >= trunc(" + dataFormatadaOracle + "))";

		return retornarLista(qrl); 
	}
	
	public static List<Afastamento> buscarPorCondutores(Long IdCondutor, String dataHoraInicio, String dataHoraFim){
		String dataFormatadaOracleInicio = "to_date('" + dataHoraInicio + "', 'DD/MM/YYYY')";
		String dataFormatadaOracleFim = "to_date('" + dataHoraFim + "', 'DD/MM/YYYY')";
		String filtroCondutor = "";

		if (IdCondutor != null) {
			filtroCondutor = "condutor.id = " + IdCondutor + " AND ";  
		}
		
		String qrl = 	"SELECT a FROM Afastamento a " +
		                " WHERE " + filtroCondutor +
						" ((trunc(dataHoraInicio) <= trunc(" + dataFormatadaOracleInicio + ")" +  	
						" AND (dataHoraFim IS NULL OR trunc(dataHoraFim) >= trunc(" + dataFormatadaOracleInicio + ")))" +
						" OR (trunc(dataHoraInicio) <= trunc(" + dataFormatadaOracleFim + ")" +  	
						" AND (dataHoraFim IS NULL OR trunc(dataHoraFim) >= trunc(" + dataFormatadaOracleFim + "))))";

		return retornarLista(qrl); 
	}

	public static List<Afastamento> buscarPorCondutores(Condutor condutor, Calendar dataHoraInicio, Calendar dataHoraFim){
		List<Afastamento> retorno = Afastamento.find(
				"condutor.id = ? "
				+ "and "
					+ "((dataHoraInicio <= ? and (dataHoraFim = null or dataHoraFim >= ?)) "
				+ "or "
					+ "(dataHoraInicio <= ? and (dataHoraFim = null or dataHoraFim >= ?)))", 
					
					condutor.getId(), 
					dataHoraInicio, dataHoraInicio, 
					dataHoraFim, dataHoraFim)
				.fetch();
		
		return retorno;
	}

	
	public boolean ordemDeDatasCorreta(){
		return this.dataHoraInicio.before(this.dataHoraFim);
	}	
}