package br.gov.jfrj.siga.tp.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import play.data.validation.Required;
import play.db.jpa.GenericModel;

@Entity
// @Table(name = "COR_2", schema="SIGAOR")
@Audited
@Table(schema = "SIGATP")
public class Cor extends GenericModel {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence_generator")
	@SequenceGenerator(name = "hibernate_sequence_generator", sequenceName = "SIGATP.hibernate_sequence")
	private long id;

	@Required
	private String nome;

	public Cor() {
		this.nome = "";
	}

	public long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
}