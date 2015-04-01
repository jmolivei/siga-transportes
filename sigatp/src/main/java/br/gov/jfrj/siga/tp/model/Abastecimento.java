package br.gov.jfrj.siga.tp.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;







import play.data.binding.As;
import play.data.validation.Min;
import play.data.validation.Required;
import play.db.jpa.GenericModel;
import play.db.jpa.JPA;
import play.modules.br.jus.jfrj.siga.uteis.validadores.validarAnoData.ValidarAnoData;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.tp.binder.DoubleBinder;
import br.gov.jfrj.siga.tp.binder.PriceBinder;

@SuppressWarnings("serial")
@Entity
@Audited
@Table(schema = "SIGATP")
public class Abastecimento extends GenericModel implements Comparable<Abastecimento> {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence_generator") 
	@SequenceGenerator(name = "hibernate_sequence_generator", sequenceName="SIGATP.hibernate_sequence") 
	public Long id;
	
	@Required
	@ValidarAnoData(descricaoCampo="Data/Hora")
	@As(lang={"*"}, value={"dd/MM/yyyy HH:mm"})
	public Calendar dataHora;
	
	@Required
	@ManyToOne
	@NotNull
	public Fornecedor fornecedor;
	
	@Required
	@Enumerated(EnumType.STRING)
	@NotNull
	public TipoDeCombustivel tipoDeCombustivel;
	
	@Required
	@Min(value=1, message="abastecimento.quantidadeEmLitros.min")
	@As(binder=DoubleBinder.class)
	public double quantidadeEmLitros;
	
	@Required
	@As(binder=PriceBinder.class)
	public double precoPorLitro;
	
	@Required
	@As(binder=DoubleBinder.class)
	public double valorTotalDaNotaFiscal;
	
	@Required
	public String numeroDaNotaFiscal;
	
	@Required
	@ManyToOne
	@NotNull
	public Veiculo veiculo;
	
	@Required
	@ManyToOne
	@NotNull
	public Condutor condutor;	
	
	@Enumerated(EnumType.STRING)
	public NivelDeCombustivel nivelDeCombustivel;
	
	@Required
	@As(binder=DoubleBinder.class)
	public double odometroEmKm;
	
	@Required
	@As(binder=DoubleBinder.class)
	public double distanciaPercorridaEmKm;
	
	@Required
	@As(binder=DoubleBinder.class)
	public double consumoMedioEmKmPorLitro;
	
 	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@JoinColumn(name = "ID_SOLICITANTE")
	public DpPessoa solicitante;
 	
 	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@JoinColumn(name = "ID_TITULAR")
	public DpPessoa titular; 	
	
 	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@JoinColumn(name = "ID_ORGAO_USU")
	public CpOrgaoUsuario orgao; 	
	
	public static List<Abastecimento> listarTodos() {
		List<Abastecimento> abastecimentos = Abastecimento.findAll();
		Collections.sort(abastecimentos, Collections.reverseOrder());
		return abastecimentos;
	}

	public static List<Abastecimento> buscarTodosPorVeiculo(Veiculo veiculo){
		List<Abastecimento> abastecimentos = Abastecimento.find("veiculo", veiculo).fetch();
		Collections.sort(abastecimentos, Collections.reverseOrder());
		return abastecimentos;
	}
		
	public static List<Abastecimento> buscarTodosPorCondutor(Condutor condutor){
		List<Abastecimento> abastecimentos = Abastecimento.find("condutor", condutor).fetch();
		Collections.sort(abastecimentos, Collections.reverseOrder());
		return abastecimentos;
	}

	public static List<Abastecimento> buscarTodosPorTipoDeCombustivel(TipoDeCombustivel tipo){
		List<Abastecimento> abastecimentos = Abastecimento.find("tipoDeCombustivel", tipo).fetch();
		Collections.sort(abastecimentos, Collections.reverseOrder());
		return abastecimentos;
	}

	public Abastecimento(){
		this.id = new Long(0);
		this.tipoDeCombustivel = TipoDeCombustivel.GASOLINA;
		this.nivelDeCombustivel = NivelDeCombustivel.I;
	}

	public Abastecimento(Long id, Calendar dataHora, Fornecedor fornecedor,
			TipoDeCombustivel tipoDeCombustivel, double quantidadeEmLitros,
			double precoPorLitro, double valorTotalDaNotaFiscal,
			String numeroDaNotaFiscal, Veiculo veiculo, Condutor condutor, 
			NivelDeCombustivel nivelDeCombustivel, double odometroEmKm,
			double distanciaPercorridaEmKm, double consumoMedioEmKmPorLitro) {
		super();
		this.id = id;
		this.dataHora = dataHora;
		this.fornecedor = fornecedor;
		this.tipoDeCombustivel = tipoDeCombustivel;
		this.quantidadeEmLitros = quantidadeEmLitros;
		this.precoPorLitro = precoPorLitro;
		this.valorTotalDaNotaFiscal = valorTotalDaNotaFiscal;
		this.numeroDaNotaFiscal = numeroDaNotaFiscal;
		this.veiculo = veiculo;
		this.condutor = condutor;
		this.nivelDeCombustivel = nivelDeCombustivel;
		this.odometroEmKm = odometroEmKm;
		this.distanciaPercorridaEmKm = distanciaPercorridaEmKm;
		this.consumoMedioEmKmPorLitro = consumoMedioEmKmPorLitro;
	}

	@Override
	public int compareTo(Abastecimento o) {
        return this.dataHora.compareTo(o.dataHora);
	}

	public static List<Abastecimento> listarAbastecimentosDoCondutor(Condutor condutor) {
		List<Abastecimento> abastecimentos = Abastecimento.find("titular.idPessoaIni = ?", condutor.getDpPessoa().getIdInicial()).fetch();
		Collections.sort(abastecimentos, Collections.reverseOrder());
		return abastecimentos;
	}
	
	public static List<Abastecimento> listarParaAdminGabinete(DpPessoa admin) {
		List<Abastecimento> retorno;
		String query = "select a from Abastecimento a "
				+ "where a.titular.idPessoa IS NOT NULL and a.titular.idPessoa in "
				+ "("
				+ "select t.idPessoa from DpPessoa t "
				+ "where (t.idPessoaIni = " + admin.getIdInicial() + " or "
				+ "t.lotacao.idLotacaoIni = " + admin.getLotacao().getIdInicial()
				+ ") and t.dataFimPessoa IS NULL)";
		
		Query qry = JPA.em().createQuery(query);
		try {
			retorno = (List<Abastecimento>) qry.getResultList();
		} catch(NoResultException ex) {
			retorno =null;
		}
		Collections.sort(retorno, Collections.reverseOrder());
		return retorno;
	}

	public static List<Abastecimento> listarParaAgente(DpPessoa agente) {
		List<Abastecimento> retorno;
		String query = "select a from Abastecimento a "
					+ "where orgao.id = " + agente.getOrgaoUsuario().getId() + " "
					+ "and a.titular.idPessoa IS NULL or a.titular.idPessoa in "
					+ "("
					+ "select t.idPessoa from DpPessoa t "
					+ "where (t.idPessoaIni = " + agente.getIdInicial() + " or "
					+ "t.lotacao.idLotacaoIni = " + agente.getLotacao().getIdInicial()
					+ ") and t.dataFimPessoa IS NULL)";
				 
		Query qry = JPA.em().createQuery(query);
		try {
			retorno = (List<Abastecimento>) qry.getResultList();
		} catch(NoResultException ex) {
			retorno =null;
		}
		Collections.sort(retorno, Collections.reverseOrder());
		return retorno;
	}

	public static List<Abastecimento> listarTodos(DpPessoa admin) {
		List<Abastecimento> retorno;
		String query = "select a from Abastecimento a "
					+ "where orgao.id = " + admin.getOrgaoUsuario().getId();
				 
		Query qry = JPA.em().createQuery(query);
		try {
			retorno = (List<Abastecimento>) qry.getResultList();
		} catch(NoResultException ex) {
			retorno =null;
		}
		Collections.sort(retorno, Collections.reverseOrder());
		return retorno;
	}

	public String getDadosParaExibicao() {
		SimpleDateFormat dataFormatada = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		return dataFormatada.format(this.dataHora.getTime()) + " - " + this.fornecedor.razaoSocial;
	}
}