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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import play.data.binding.As;
import play.data.validation.Required;
import play.db.jpa.GenericModel;
import play.modules.br.jus.jfrj.siga.uteis.validadores.validarAnoData.ValidarAnoData;
import uteis.PerguntaSimNao;
import binders.PriceBinder;
import br.jus.jfrj.siga.uteis.UpperCase;

@Entity
@Audited
@Table(schema = "SIGATP")
public class AutoDeInfracao extends GenericModel implements Comparable<AutoDeInfracao> {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence_generator") @SequenceGenerator(name = "hibernate_sequence_generator", sequenceName="SIGATP.hibernate_sequence") 
	public Long id;
	
	@Required
	@As(lang={"*"}, value = {"dd/MM/yyyy HH:mm"})
	@ValidarAnoData(descricaoCampo="Data/Hora")
	public Calendar dataHora;
	
	@Required
	@ManyToOne
	@NotNull
	public Veiculo veiculo;	
	
	public TipoDeNotificacao tipoDeNotificacao;
	
	@Required
	@ManyToOne
	@NotNull
	@JoinColumn(name = "PENALIDADE_ID")
	public Penalidade penalidade;	
	
	@Required
	@UpperCase
	public String local;
	
	@Required
	@Enumerated(EnumType.STRING)
	public PerguntaSimNao foiRecebido;
	
	@Required
	@As(binder=PriceBinder.class)
	public double valor;
	
	@As(binder=PriceBinder.class)
	public double valorComDesconto;
	
	@Required
	@As(lang={"*"}, value = {"dd/MM/yyyy"})
	@ValidarAnoData(intervalo=2, descricaoCampo="Data de Vencimento")
	public Calendar dataDeVencimento;
	
	@As(lang={"*"}, value = {"dd/MM/yyyy"})
	@ValidarAnoData(intervalo=5, descricaoCampo="Data de Pagamento")
	public Calendar dataDePagamento;
	
	@Required
	@ManyToOne
	@NotNull
	public Condutor condutor;
	
	@As(lang={"*"}, value = {"dd/MM/yyyy"})
	@ValidarAnoData(descricaoCampo="Data Limite Apresentacao")
	public Calendar dataLimiteApresentacao;
	
	@UpperCase
	public String memorando;
	
	@As(lang={"*"}, value = {"dd/MM/yyyy"})
	@ValidarAnoData(descricaoCampo="Data do Processo")
	public Calendar dataDoProcesso;
	
	@UpperCase
	public String numeroDoProcesso;
	
	@Transient
	public PerguntaSimNao foiPago(){
		return dataDePagamento != null ? PerguntaSimNao.SIM : PerguntaSimNao.NAO;
	}
	
	
	public AutoDeInfracao(){
		this.id = new Long(0);
		this.foiRecebido = PerguntaSimNao.NAO;
	}
	
		
	public AutoDeInfracao(Long id, Calendar dataHora, Veiculo veiculo,
			String local, PerguntaSimNao foiRecebido, double valor, double valorComDesconto,
			Calendar dataDeVencimento, Calendar dataDePagamento, Condutor condutor,
			Calendar dataLimiteApresentacao, String memorando,
			Calendar dataDoProcesso, String numeroDoProcesso, Penalidade penalidade) {
		super();
		this.id = id;
		this.dataHora = dataHora;
		this.veiculo = veiculo;
		this.local = local;
		this.foiRecebido = foiRecebido;
		this.valor = valor;
		this.valorComDesconto = valorComDesconto;
		this.dataDeVencimento = dataDeVencimento;
		this.dataDePagamento = dataDePagamento;
		this.condutor = condutor;
		this.dataLimiteApresentacao = dataLimiteApresentacao;
		this.memorando = memorando;
		this.dataDoProcesso = dataDoProcesso;
		this.numeroDoProcesso = numeroDoProcesso;
		this.penalidade = penalidade;
	}


	public static List<AutoDeInfracao> buscarAutosDeInfracaoPorVeiculo(Veiculo veiculo) {
		List<AutoDeInfracao> autosDeInfracao = AutoDeInfracao.find("veiculo", veiculo).fetch();
  		Collections.sort(autosDeInfracao, Collections.reverseOrder());
		return autosDeInfracao;
	}

	public static List<AutoDeInfracao> buscarAutosDeInfracaoPorCondutor(Condutor condutor) {
		List<AutoDeInfracao> autosDeInfracao = AutoDeInfracao.find("condutor", condutor).fetch();
  		Collections.sort(autosDeInfracao, Collections.reverseOrder());
		return autosDeInfracao;
	}
	
	@Override
	public int compareTo(AutoDeInfracao o) {
        return this.dataHora.compareTo(o.dataHora);
	}		

	public boolean dataPosteriorDataCorrente(Calendar dataDePagamento){
		return dataDePagamento.after(Calendar.getInstance());
	}


	public static List<AutoDeInfracao> listarOrdenado() {
		List<AutoDeInfracao> autosDeInfracao = AutoDeInfracao.findAll();
  		Collections.sort(autosDeInfracao, Collections.reverseOrder());
		return autosDeInfracao;
	}
}