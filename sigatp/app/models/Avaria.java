package models;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

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

import play.data.binding.As;
import play.data.validation.Required;
import play.db.jpa.GenericModel;
import play.modules.br.jus.jfrj.siga.uteis.validadores.validarAnoData.ValidarAnoData;
import uteis.PerguntaSimNao;
import br.jus.jfrj.siga.uteis.UpperCase;

import com.google.gson.Gson;

@Entity
//@Table(name = "AVARIA_2", schema="SIGAOR")
@Audited
@Table(schema = "SIGATP")
public class Avaria extends GenericModel implements Comparable<Avaria> {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence_generator") @SequenceGenerator(name = "hibernate_sequence_generator", sequenceName="SIGATP.hibernate_sequence") 
	public Long id;
	
	@ManyToOne
	@NotNull
	public Veiculo veiculo;
	
	//TODO nao se registra a data em que aconteceu a avaria?

	@Required
	@As(lang={"*"}, value={"dd/MM/yyyy"})
	@ValidarAnoData(descricaoCampo="Data de Registro")
	public Calendar dataDeRegistro;
	
	//@As(lang={"*"}, value={"dd/MM/yyyy"})
	@As(lang={"*"}, value={"dd/MM/yyyy HH:mm"})
	@ValidarAnoData(descricaoCampo="Data de Solucao")
	public Calendar dataDeSolucao;
	
	@Required
	@UpperCase
	public String descricao;
	
	public Avaria() {
		this.id = new Long(0);
		this.veiculo = null; 
		this.dataDeRegistro = Calendar.getInstance();
		this.dataDeSolucao = null;
		this.descricao = "";
		this.podeCircular = PerguntaSimNao.NAO;
	}
	
	public Avaria(Veiculo veiculo, Calendar dataDeRegistro, Calendar dataDeSolucao,
			String descricao, PerguntaSimNao podeCircular) {
		super();
		this.veiculo = veiculo;
		this.dataDeRegistro = dataDeRegistro;
		this.dataDeSolucao = dataDeSolucao;
		this.descricao = descricao;
		this.podeCircular = podeCircular;
		//this.dataHoraInicio = dataHoraInicio;
		//this.dataHoraFim = dataHoraFim;
	}
	
	public String toJson() {
		return new Gson().toJson(this); 
	}
	
	public static List<Avaria> buscarTodasPorVeiculo(Veiculo veiculo) {
		List<Avaria> avarias = Avaria.find("veiculo", veiculo).fetch();
		Collections.sort(avarias);
		return avarias;
	}

	public static List<Avaria> buscarPendentesPorVeiculo(Veiculo veiculo) {
		List<Avaria> avarias = Avaria.find("veiculo = ? AND dataDeSolucao is null ", veiculo).fetch();
		Collections.sort(avarias);
		return avarias;
	}
	
	@Override
	public int compareTo(Avaria o) {
		return this.veiculo.compareTo(o.veiculo);
	}

	/*@As(lang={"*"}, value={"dd/MM/yyyy HH:mm"})
	public Calendar dataHoraInicio;
	
	@As(lang={"*"}, value={"dd/MM/yyyy HH:mm"})
	public Calendar dataHoraFim;*/

	@Required
	@Enumerated(EnumType.STRING)
	public PerguntaSimNao podeCircular;

	public static List<Avaria> listarTodos() {
		List<Avaria> avarias = Avaria.findAll();
		Collections.sort(avarias);
		return avarias;
	}
}
