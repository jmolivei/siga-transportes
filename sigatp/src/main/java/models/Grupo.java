	package models;

import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import play.data.validation.Required;
import play.db.jpa.GenericModel;
import play.modules.br.jus.jfrj.siga.uteis.validadores.upperCase.UpperCase;

@Entity
@Audited
@Table(name="GRUPOVEICULO", schema = "SIGATP")
public class Grupo extends GenericModel implements Comparable<Grupo> {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence_generator") @SequenceGenerator(name = "hibernate_sequence_generator", sequenceName="SIGATP.hibernate_sequence") 
	public long id;
	
	@Required
	@UpperCase
	public String nome;
	
	@Required
	@UpperCase	
	public String finalidade;
	
	@Required
	@UpperCase	
	public String caracteristicas;
	
	@Required
	@UpperCase	
	public String letra;

	public Grupo() {
		this.nome="";
		this.finalidade="";
		this.caracteristicas="";
	}
	
	public String getDadosParaExibicao() {
		return this.letra.toUpperCase() + "-" + this.nome.toUpperCase();
	}
	
	public int compareTo(Grupo g) {
		return this.letra.compareTo(g.letra);
	}
	
	public static List<Grupo> listarTodos() throws Exception {
		List<Grupo> grupos = Grupo.findAll();
		Collections.sort(grupos);
		return grupos;
	}
}