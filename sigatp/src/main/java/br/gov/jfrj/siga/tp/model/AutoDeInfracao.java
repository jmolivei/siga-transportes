package br.gov.jfrj.siga.tp.model;

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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import br.com.caelum.vraptor.Convert;
import br.gov.jfrj.siga.model.ActiveRecord;
import br.gov.jfrj.siga.tp.util.PerguntaSimNao;
import br.gov.jfrj.siga.tp.vraptor.ConvertableEntity;
import br.gov.jfrj.siga.tp.vraptor.converter.DoubleConverter;
import br.jus.jfrj.siga.uteis.UpperCase;

@Entity
@Audited
@Table(schema = "SIGATP")
public class AutoDeInfracao extends TpModel implements ConvertableEntity, Comparable<AutoDeInfracao> {

	public static final ActiveRecord<AutoDeInfracao> AR = new ActiveRecord<>(AutoDeInfracao.class);

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence_generator")
	@SequenceGenerator(name = "hibernate_sequence_generator", sequenceName = "SIGATP.hibernate_sequence")
	public Long id;

	@NotNull
	public Calendar dataHora;

	@ManyToOne
	@NotNull
	public Veiculo veiculo;

	@NotNull
	public String codigoDaAutuacao;

	@NotNull
	public String codigoDaPenalidade;

	@NotNull
	@UpperCase
	public String descricao;

	@NotNull
	@Enumerated(EnumType.STRING)
	public Gravidade gravidade;

	@NotNull
	@UpperCase
	public String enquadramento;

	@NotNull
	@UpperCase
	public String local;

	@NotNull
	@Enumerated(EnumType.STRING)
	public PerguntaSimNao foiRecebido;

	@NotNull
	@Convert(DoubleConverter.class)
	public double valor;

	public double valorComDesconto;

	@NotNull
	public int quantidadeDePontos;

	@NotNull
	public Calendar dataDeVencimento;

	public Calendar dataDePagamento;

	// @Required
	// public Requisicao requisicao;

	@ManyToOne
	@NotNull
	public Condutor condutor;

	public Calendar dataLimiteApresentacao;

	@UpperCase
	public String memorando;

	public Calendar dataDoProcesso;

	@UpperCase
	public String numeroDoProcesso;

	@Transient
	public PerguntaSimNao foiPago() {
		return dataDePagamento != null ? PerguntaSimNao.SIM : PerguntaSimNao.NAO;
	}

	public Calendar getDataHora() {
		return dataHora;
	}

	public void setDataHora(Calendar dataHora) {
		this.dataHora = dataHora;
	}

	public Veiculo getVeiculo() {
		return veiculo;
	}

	public void setVeiculo(Veiculo veiculo) {
		this.veiculo = veiculo;
	}

	public String getCodigoDaAutuacao() {
		return codigoDaAutuacao;
	}

	public void setCodigoDaAutuacao(String codigoDaAutuacao) {
		this.codigoDaAutuacao = codigoDaAutuacao;
	}

	public String getCodigoDaPenalidade() {
		return codigoDaPenalidade;
	}

	public void setCodigoDaPenalidade(String codigoDaPenalidade) {
		this.codigoDaPenalidade = codigoDaPenalidade;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Gravidade getGravidade() {
		return gravidade;
	}

	public void setGravidade(Gravidade gravidade) {
		this.gravidade = gravidade;
	}

	public String getEnquadramento() {
		return enquadramento;
	}

	public void setEnquadramento(String enquadramento) {
		this.enquadramento = enquadramento;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public PerguntaSimNao getFoiRecebido() {
		return foiRecebido;
	}

	public void setFoiRecebido(PerguntaSimNao foiRecebido) {
		this.foiRecebido = foiRecebido;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

	public double getValorComDesconto() {
		return valorComDesconto;
	}

	public void setValorComDesconto(double valorComDesconto) {
		this.valorComDesconto = valorComDesconto;
	}

	public int getQuantidadeDePontos() {
		return quantidadeDePontos;
	}

	public void setQuantidadeDePontos(int quantidadeDePontos) {
		this.quantidadeDePontos = quantidadeDePontos;
	}

	public Calendar getDataDeVencimento() {
		return dataDeVencimento;
	}

	public void setDataDeVencimento(Calendar dataDeVencimento) {
		this.dataDeVencimento = dataDeVencimento;
	}

	public Calendar getDataDePagamento() {
		return dataDePagamento;
	}

	public void setDataDePagamento(Calendar dataDePagamento) {
		this.dataDePagamento = dataDePagamento;
	}

	public Condutor getCondutor() {
		return condutor;
	}

	public void setCondutor(Condutor condutor) {
		this.condutor = condutor;
	}

	public Calendar getDataLimiteApresentacao() {
		return dataLimiteApresentacao;
	}

	public void setDataLimiteApresentacao(Calendar dataLimiteApresentacao) {
		this.dataLimiteApresentacao = dataLimiteApresentacao;
	}

	public String getMemorando() {
		return memorando;
	}

	public void setMemorando(String memorando) {
		this.memorando = memorando;
	}

	public Calendar getDataDoProcesso() {
		return dataDoProcesso;
	}

	public void setDataDoProcesso(Calendar dataDoProcesso) {
		this.dataDoProcesso = dataDoProcesso;
	}

	public String getNumeroDoProcesso() {
		return numeroDoProcesso;
	}

	public void setNumeroDoProcesso(String numeroDoProcesso) {
		this.numeroDoProcesso = numeroDoProcesso;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	public AutoDeInfracao() {
		this.id = new Long(0);
		this.gravidade = Gravidade.LEVE;
		this.foiRecebido = PerguntaSimNao.NAO;
	}

	public AutoDeInfracao(Long id, Calendar dataHora, Veiculo veiculo, String codigoDaAutuacao, String codigoDaPenalidade, String descricao, Gravidade gravidade, String enquadramento, String local,
			PerguntaSimNao foiRecebido, double valor, int quantidadeDePontos, double valorComDesconto, Calendar dataDeVencimento, Calendar dataDePagamento, Condutor condutor,
			Calendar dataLimiteApresentacao, String memorando, Calendar dataDoProcesso, String numeroDoProcesso) {
		super();
		this.id = id;
		this.dataHora = dataHora;
		this.veiculo = veiculo;
		this.codigoDaAutuacao = codigoDaAutuacao;
		this.codigoDaPenalidade = codigoDaPenalidade;
		this.descricao = descricao;
		this.gravidade = gravidade;
		this.enquadramento = enquadramento;
		this.local = local;
		this.foiRecebido = foiRecebido;
		this.valor = valor;
		this.quantidadeDePontos = quantidadeDePontos;
		this.valorComDesconto = valorComDesconto;
		this.dataDeVencimento = dataDeVencimento;
		this.dataDePagamento = dataDePagamento;
		this.condutor = condutor;
		this.dataLimiteApresentacao = dataLimiteApresentacao;
		this.memorando = memorando;
		this.dataDoProcesso = dataDoProcesso;
		this.numeroDoProcesso = numeroDoProcesso;
	}

	public static List<AutoDeInfracao> buscarAutosDeInfracaoPorVeiculo(Veiculo veiculo) {
		List<AutoDeInfracao> autosDeInfracao = AutoDeInfracao.AR.find("veiculo", veiculo).fetch();
		Collections.sort(autosDeInfracao, Collections.reverseOrder());
		return autosDeInfracao;
	}

	public static List<AutoDeInfracao> buscarAutosDeInfracaoPorCondutor(Condutor condutor) {
		List<AutoDeInfracao> autosDeInfracao = AutoDeInfracao.AR.find("condutor", condutor).fetch();
		Collections.sort(autosDeInfracao, Collections.reverseOrder());
		return autosDeInfracao;
	}

	@Override
	public int compareTo(AutoDeInfracao o) {
		return this.dataHora.compareTo(o.dataHora);
	}

	public boolean dataPosteriorDataCorrente(Calendar dataDePagamento) {
		return dataDePagamento.after(Calendar.getInstance());
	}

	public static List<AutoDeInfracao> listarOrdenado() {
		List<AutoDeInfracao> autosDeInfracao = AutoDeInfracao.AR.findAll();
		Collections.sort(autosDeInfracao, Collections.reverseOrder());
		return autosDeInfracao;
	}

}