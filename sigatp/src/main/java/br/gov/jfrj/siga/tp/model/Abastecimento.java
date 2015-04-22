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
import play.db.jpa.JPA;
import play.modules.br.jus.jfrj.siga.uteis.validadores.validarAnoData.ValidarAnoData;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.model.ActiveRecord;

@SuppressWarnings("serial")
@Entity
@Audited
@Table(schema = "SIGATP")
public class Abastecimento extends TpModel implements Comparable<Abastecimento> {
	
	public static final ActiveRecord<Abastecimento> AR = new ActiveRecord<>(Abastecimento.class);
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence_generator") 
	@SequenceGenerator(name = "hibernate_sequence_generator", sequenceName="SIGATP.hibernate_sequence") 
	private Long id;
	
	@Required
	@ValidarAnoData(descricaoCampo="Data/Hora")
	@As(lang={"*"}, value={"dd/MM/yyyy HH:mm"})
	private Calendar dataHora;
	
	@Required
	@ManyToOne
	@NotNull
	private Fornecedor fornecedor;
	
	@Required
	@Enumerated(EnumType.STRING)
	@NotNull
	private TipoDeCombustivel tipoDeCombustivel;
	
	@Required
	@Min(value=1, message="abastecimento.quantidadeEmLitros.min")
	private double quantidadeEmLitros;
	
	@Required
//	@As(binder=PriceBinder.class)
	private double precoPorLitro;
	
	@Required
	private double valorTotalDaNotaFiscal;
	
	@Required
	private String numeroDaNotaFiscal;
	
	@Required
	@ManyToOne
	@NotNull
	private Veiculo veiculo;
	
	@Required
	@ManyToOne
	@NotNull
	private Condutor condutor;	
	
	@Enumerated(EnumType.STRING)
	private NivelDeCombustivel nivelDeCombustivel;
	
	@Required
	private double odometroEmKm;
	
	@Required
	private double distanciaPercorridaEmKm;
	
	@Required
	private double consumoMedioEmKmPorLitro;
	
 	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@JoinColumn(name = "ID_SOLICITANTE")
	private DpPessoa solicitante;
 	
 	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@JoinColumn(name = "ID_TITULAR")
	private DpPessoa titular; 	
	
 	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@JoinColumn(name = "ID_ORGAO_USU")
	private CpOrgaoUsuario orgao;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Calendar getDataHora() {
		return dataHora;
	}

	public void setDataHora(Calendar dataHora) {
		this.dataHora = dataHora;
	}

	public Fornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public TipoDeCombustivel getTipoDeCombustivel() {
		return tipoDeCombustivel;
	}

	public void setTipoDeCombustivel(TipoDeCombustivel tipoDeCombustivel) {
		this.tipoDeCombustivel = tipoDeCombustivel;
	}

	public double getQuantidadeEmLitros() {
		return quantidadeEmLitros;
	}

	public void setQuantidadeEmLitros(double quantidadeEmLitros) {
		this.quantidadeEmLitros = quantidadeEmLitros;
	}

	public double getPrecoPorLitro() {
		return precoPorLitro;
	}

	public void setPrecoPorLitro(double precoPorLitro) {
		this.precoPorLitro = precoPorLitro;
	}

	public double getValorTotalDaNotaFiscal() {
		return valorTotalDaNotaFiscal;
	}

	public void setValorTotalDaNotaFiscal(double valorTotalDaNotaFiscal) {
		this.valorTotalDaNotaFiscal = valorTotalDaNotaFiscal;
	}

	public String getNumeroDaNotaFiscal() {
		return numeroDaNotaFiscal;
	}

	public void setNumeroDaNotaFiscal(String numeroDaNotaFiscal) {
		this.numeroDaNotaFiscal = numeroDaNotaFiscal;
	}

	public Veiculo getVeiculo() {
		return veiculo;
	}

	public void setVeiculo(Veiculo veiculo) {
		this.veiculo = veiculo;
	}

	public Condutor getCondutor() {
		return condutor;
	}

	public void setCondutor(Condutor condutor) {
		this.condutor = condutor;
	}

	public NivelDeCombustivel getNivelDeCombustivel() {
		return nivelDeCombustivel;
	}

	public void setNivelDeCombustivel(NivelDeCombustivel nivelDeCombustivel) {
		this.nivelDeCombustivel = nivelDeCombustivel;
	}

	public double getOdometroEmKm() {
		return odometroEmKm;
	}

	public void setOdometroEmKm(double odometroEmKm) {
		this.odometroEmKm = odometroEmKm;
	}

	public double getDistanciaPercorridaEmKm() {
		return distanciaPercorridaEmKm;
	}

	public void setDistanciaPercorridaEmKm(double distanciaPercorridaEmKm) {
		this.distanciaPercorridaEmKm = distanciaPercorridaEmKm;
	}

	public double getConsumoMedioEmKmPorLitro() {
		return consumoMedioEmKmPorLitro;
	}

	public void setConsumoMedioEmKmPorLitro(double consumoMedioEmKmPorLitro) {
		this.consumoMedioEmKmPorLitro = consumoMedioEmKmPorLitro;
	}

	public DpPessoa getSolicitante() {
		return solicitante;
	}

	public void setSolicitante(DpPessoa solicitante) {
		this.solicitante = solicitante;
	}

	public DpPessoa getTitular() {
		return titular;
	}

	public void setTitular(DpPessoa titular) {
		this.titular = titular;
	}

	public CpOrgaoUsuario getOrgao() {
		return orgao;
	}

	public void setOrgao(CpOrgaoUsuario orgao) {
		this.orgao = orgao;
	}

	public static List<Abastecimento> listarTodos() {
		List<Abastecimento> abastecimentos = Abastecimento.AR.findAll();
		Collections.sort(abastecimentos, Collections.reverseOrder());
		return abastecimentos;
	}

	public static List<Abastecimento> buscarTodosPorVeiculo(Veiculo veiculo){
		List<Abastecimento> abastecimentos = Abastecimento.AR.find("veiculo", veiculo).fetch();
		Collections.sort(abastecimentos, Collections.reverseOrder());
		return abastecimentos;
	}
		
	public static List<Abastecimento> buscarTodosPorCondutor(Condutor condutor){
		List<Abastecimento> abastecimentos = Abastecimento.AR.find("condutor", condutor).fetch();
		Collections.sort(abastecimentos, Collections.reverseOrder());
		return abastecimentos;
	}

	public static List<Abastecimento> buscarTodosPorTipoDeCombustivel(TipoDeCombustivel tipo){
		List<Abastecimento> abastecimentos = Abastecimento.AR.find("tipoDeCombustivel", tipo).fetch();
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
		List<Abastecimento> abastecimentos = Abastecimento.AR.find("titular.idPessoaIni = ?", condutor.getDpPessoa().getIdInicial()).fetch();
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
		return dataFormatada.format(this.dataHora.getTime()) + " - " + this.fornecedor.getRazaoSocial();
	}
}