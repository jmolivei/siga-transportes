package models;

import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import play.data.validation.CheckWith;
import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.GenericModel;
import play.modules.br.jus.jfrj.siga.uteis.validadores.upperCase.UpperCase;
import play.modules.br.jus.jfrj.siga.uteis.validadores.email.EmailCheck;

@SuppressWarnings("serial")
@Entity
@Audited
@Table(schema = "SIGATP")
public class Fornecedor extends GenericModel implements Comparable<Fornecedor>{
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence_generator") @SequenceGenerator(name = "hibernate_sequence_generator", sequenceName="SIGATP.hibernate_sequence") 
	public long id;
	
	@Enumerated(EnumType.STRING)
	public RamoDeAtividade ramoDeAtividade;
	
	@Required
	@Unique(message="fornecedor.cnpj.unique")
	public String cnpj;
	
	@Required
	@UpperCase
	public String razaoSocial;
	
	@UpperCase
	public String nomeContato;
	
	public String telefone;
	
	public String fax;
	
	@CheckWith(EmailCheck.class)
	@Unique
	public String eMail;
	
	@UpperCase
	public String logradouro;

	@UpperCase
	public String complemento;
	
	public String cep;
	
	@UpperCase
	public String bairro;
	
	@UpperCase
	public String localidade;
	
	@UpperCase
	public String uf;
	
	public String enderecoVirtual;
	
	
	public String getRazaoSocialECNPJ() {
		return this.razaoSocial + " - " + this.cnpj;
	}

	
	public Fornecedor(){
		this.id = new Long(0);
		this.ramoDeAtividade = RamoDeAtividade.COMBUSTIVEL;
	}

	
	public Fornecedor(long id, RamoDeAtividade ramoDeAtividade, String cnpj,
			String razaoSocial, String nomeContato, String telefone,
			String fax, String eMail, String logradouro, String complemento,
			String cep, String bairro, String cidade, String localidade, String uf,
			String enderecoVirtual) {
		super();
		this.id = id;
		this.ramoDeAtividade = ramoDeAtividade;
		this.cnpj = cnpj;
		this.razaoSocial = razaoSocial;
		this.nomeContato = nomeContato;
		this.telefone = telefone;
		this.fax = fax;
		this.eMail = eMail;
		this.logradouro = logradouro;
		this.complemento = complemento;
		this.cep = cep;
		this.bairro = bairro;
		this.localidade = localidade;
		this.uf = uf;
		this.enderecoVirtual = enderecoVirtual;
	}

	@Override
	public int compareTo(Fornecedor o) {
		return this.razaoSocial.compareTo(o.razaoSocial);
	}

	public static List<Fornecedor> buscarTodosPorUF(String uf){
		List<Fornecedor> fornecedores = Fornecedor.find("uf", uf).fetch();
		Collections.sort(fornecedores);
		return fornecedores;
	}

	public static List<Fornecedor> buscarTodosPorCep(String cep){
		List<Fornecedor> fornecedores = Fornecedor.find("cep", cep).fetch();
		Collections.sort(fornecedores);
		return fornecedores;
	}

	public static List<Fornecedor> listarTodos() {
		List<Fornecedor> fornecedores = Fornecedor.findAll();
		Collections.sort(fornecedores);
		return fornecedores;
	}
}
