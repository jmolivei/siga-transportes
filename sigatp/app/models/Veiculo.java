package models;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Query;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import play.data.binding.As;
import play.data.validation.CheckWith;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.GenericModel;
import play.db.jpa.JPA;
import play.modules.br.jus.jfrj.siga.uteis.validadores.validarAnoData.ValidarAnoData;
import uteis.PerguntaSimNao;
import uteis.Situacao;
import validadores.ChassiCheck;
import validadores.RenavamCheck;
import binders.DoubleBinder;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.jus.jfrj.siga.uteis.UpperCase;

@Entity
//@Table(name = "VEICULO_2", schema="SIGAOR")
@Audited
@Table(schema = "SIGATP")
public class Veiculo extends GenericModel implements Comparable<Veiculo> {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence_generator") @SequenceGenerator(name = "hibernate_sequence_generator", sequenceName="SIGATP.hibernate_sequence") 
	public Long id;
	
	@Required
	@MaxSize(value=8,message="veiculo.placa.maxSize.")
	@Unique(message="veiculo.placa.unique")
	@UpperCase
	public String placa;
	
	@ManyToOne
	public Grupo grupo;
	
 	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@JoinColumn(name = "ID_ORGAO_USU")
	public CpOrgaoUsuario cpOrgaoUsuario;  

	@Enumerated(EnumType.STRING)
	public Situacao situacao;
	
	@Required
	@UpperCase
	@MaxSize(value=11,message="veiculo.patrimonio.maxSize")
	public String patrimonio;
	
	@OneToMany(orphanRemoval=true,mappedBy="veiculo") @OrderBy("dataHoraInicio DESC") 
	public List<LotacaoVeiculo> lotacoes;
	
	@Transient
	public DpLotacao lotacaoAtual;
	
	@Transient
	@As(binder=DoubleBinder.class)
	public Double odometroEmKmAtual;
	
	@Enumerated(EnumType.STRING)
	public PerguntaSimNao usoComum;
	
	@MinSize(value=4,message="veiculo.anoFabricacao.minSize")
	@MaxSize(value=4,message="veiculo.anoFabricacao.maxSize")
	public int anoFabricacao;
	
	@MinSize(value=4,message="veiculo.anoModelo.minSize")
	@MaxSize(value=4,message="veiculo.anoModelo.maxSize")
	public int anoModelo;
	
	@Required
	@UpperCase
	public String marca;
	
	@Required
	@UpperCase
	public String modelo;
	
	@Enumerated(EnumType.STRING)
	public TipoDeCombustivel tipoDeCombustivel;

	@ManyToOne
	public Cor cor;
	
	@UpperCase
	public String motor;
	
	@UpperCase
	public String potencia;
	
	@UpperCase
	public String direcao;
	
	@UpperCase
	public String transmissao;
	
	@UpperCase
	public String tipoDeBlindagem;	
	
	public String tanque;
	
	public String pneuMedida;
	
	public String pneuPressaoDianteira;	
	
	public String pneuPressaoTraseira;
	
	@Required
	@CheckWith(RenavamCheck.class)
	public String renavam;	
	
	@Required
	@CheckWith(ChassiCheck.class)
	@UpperCase
	public String chassi;
	
	@Enumerated(EnumType.STRING)
	public PerguntaSimNao licenciamentoAnual;
	
	@Enumerated(EnumType.STRING)
	public PerguntaSimNao dpvat;
	
	@Enumerated(EnumType.STRING)
	public CategoriaCNH categoriaCNH;
	
	public boolean temAIRBAG;
	
	public boolean temGPS;
	
	public boolean temPILOTOAUTOMATICO;
	
	public boolean temCONTROLEDETRACAO;
	
	public boolean temSENSORDEMARCHARE;
	
	public boolean temABS;
	
	public boolean temCDPLAYER;
	
	public boolean temBANCOSEMCOURO;
	
	public boolean temRODADELIGALEVE;
	
	public boolean temCAMERADEMARCHARE;
	
	public boolean temEBD;
	
	public boolean temDVDPLAYER;
	
	public boolean temTELALCDPAPOIOCABECA;
	
	public boolean temFREIOADISCONASQUATRORODAS;
	
	public boolean temARCONDICIONADO;
	
	public boolean temOUTROS;
	
	@UpperCase
	public String outros;
	
	@As(lang={"*"}, value={"dd/MM/yyyy"})
	@ValidarAnoData(descricaoCampo="Data de Aquisicao")
	public Calendar dataAquisicao;	
	
	@As(binder=DoubleBinder.class)
	public Double valorAquisicao;
	
	@As(lang={"*"}, value={"dd/MM/yyyy"})
	@ValidarAnoData(descricaoCampo="Data de Garantia")
	public Calendar dataGarantia;
	
	@ManyToOne
	public Fornecedor fornecedor;
	
	public String numeroCartaoAbastecimento;
	
	@As(lang={"*"}, value={"dd/MM/yyyy"})
	@ValidarAnoData(descricaoCampo="Validade do Cartao de Abastecimento")
	public Calendar validadeCartaoAbastecimento;
	
	public String numeroCartaoSeguro;
	
	@As(lang={"*"}, value={"dd/MM/yyyy"})
	@ValidarAnoData(intervalo=10, descricaoCampo="Validade do Cartao de Seguro")
	public Calendar validadeCartaoSeguro;
	
	@As(lang={"*"}, value={"dd/MM/yyyy HH:mm"}) 
	@ValidarAnoData(descricaoCampo="Data de Alienacao")
	public Calendar dataAlienacao;
	
	@UpperCase
	public String termoAlienacao;
	
	@UpperCase
	public String processoAlienacao;
	
	@OneToMany(mappedBy="veiculo",cascade=CascadeType.ALL)
	public List<AutoDeInfracao> autosDeInfracao;
	
	@OneToMany(mappedBy="veiculo",orphanRemoval=true)
	public List<Avaria> avarias;
	
	@OneToMany(mappedBy="veiculo",orphanRemoval=true)
	public List<Abastecimento> abastecimentos;
	
	@OneToMany(mappedBy="veiculo",orphanRemoval=true)
	public List<RelatorioDiario> relatoriosdiarios;
	
	public Veiculo() {
		this.id = new Long(0);
		this.grupo = null;
		this.placa = null;
		this.situacao = Situacao.Ativo;
		this.patrimonio = "";
		this.lotacoes = null;
		this.usoComum = PerguntaSimNao.NAO;
		this.anoFabricacao = 2013;
		this.anoModelo = 2013;
		this.marca = "";
		this.modelo = "";
		this.tipoDeCombustivel = TipoDeCombustivel.GASOLINA;
		this.cor = null;
		this.motor = "";
		this.potencia = "";
		this.direcao = "";
		this.transmissao = "";
		this.tipoDeBlindagem = "";
		this.tanque = "";
		this.pneuMedida = "";
		this.pneuPressaoDianteira = "";
		this.pneuPressaoTraseira = "";
		this.renavam = "";
		this.chassi = "";
		this.licenciamentoAnual = PerguntaSimNao.NAO;
		this.dpvat = PerguntaSimNao.NAO;
		this.categoriaCNH = CategoriaCNH.D;
		this.temAIRBAG = false;
		this.temGPS = false;
		this.temPILOTOAUTOMATICO = false;
		this.temCONTROLEDETRACAO = false;
		this.temSENSORDEMARCHARE = false;
		this.temABS = false;
		this.temCDPLAYER = false;
		this.temBANCOSEMCOURO = false;
		this.temRODADELIGALEVE = false;
		this.temCAMERADEMARCHARE = false;
		this.temEBD = false;
		this.temDVDPLAYER = false;
		this.temTELALCDPAPOIOCABECA = false;
		this.temFREIOADISCONASQUATRORODAS = false;
		this.temARCONDICIONADO = false;
		this.temOUTROS = false;
		this.outros = "";
		this.dataAquisicao = null;
		this.valorAquisicao = 0.00;
		this.dataGarantia = null;
		this.numeroCartaoAbastecimento = "";
		this.validadeCartaoAbastecimento = null;
		this.numeroCartaoSeguro = "";
		this.validadeCartaoSeguro = null;
		this.dataAlienacao = null;
		this.termoAlienacao = "";
		this.processoAlienacao = "";
	}
	
	public String getDadosParaExibicao() {
		return this.marca + " " + this.modelo + " - " + this.placa;
	}
	
/*	@Override
	public int compareTo(Veiculo o) {
		return (this.situacao + this.placa).compareTo(o.situacao + o.placa);
	}
*/	
	@Override
	public int compareTo(Veiculo o) {
		return (this.situacao + this.marca + this.modelo).compareTo(o.situacao + o.marca + o.modelo);
	}

	public Double getUltimoOdometroDeLotacao() {
		Double retorno = (double) 0;
		
		if(lotacoes != null && !lotacoes.isEmpty() && lotacoes.get(0).odometroEmKm != null) {
			retorno = lotacoes.get(0).odometroEmKm;
		}
		
		return retorno;
	}
	
	public DpLotacao getDpLotacaoVigente() {
		DpLotacao retorno = null;
		
		if(lotacoes != null && !lotacoes.isEmpty()) {
			retorno = lotacoes.get(0).lotacao;
		}
		
		return retorno;
	}
	
	public static List<Veiculo> listarDisponiveis(String dataSaida, Long idMissao, Long idOrgao) {
		List<Veiculo> veiculos;
		String dataFormatadaOracle = "to_date('" + dataSaida + "', 'DD/MM/YYYY HH24:mi')";
		String qrl = 	"SELECT v FROM Veiculo v where " +
						" v.situacao = '" + Situacao.Ativo.toString() + "' " +
						" AND v.cpOrgaoUsuario.id in  " + 
						"(SELECT cp.id FROM CpOrgaoUsuario cp" +
						" WHERE  cp.id = " + idOrgao + ")" +
						" AND v.id not in " + 
						"(SELECT s.veiculo.id FROM ServicoVeiculo s" +
						" WHERE  s.veiculo.id = v.id" +
						" AND   s.dataHoraInicio <= " + dataFormatadaOracle +
						" AND    (s.dataHoraFim = NULL " + 
						" OR    s.dataHoraFim >= " + dataFormatadaOracle + "))" +
						" AND   v.id not in" + 
						"(SELECT m.veiculo.id FROM Missao m" +
						" WHERE  m.veiculo.id = v.id" +
						" AND    m.id != " + idMissao +
						" AND    m.estadoMissao != '" + EstadoMissao.CANCELADA + "'" +
						" AND    m.estadoMissao != '" + EstadoMissao.PROGRAMADA + "'" +
						" AND    m.estadoMissao != '" + EstadoMissao.FINALIZADA + "'" +
						" AND   ((m.dataHoraSaida <=  " + dataFormatadaOracle +
						" AND    m.dataHoraRetorno = NULL)" + 
						" OR    (m.dataHoraSaida <=  " + dataFormatadaOracle +
						" AND    m.dataHoraRetorno >= "  + dataFormatadaOracle + ")))" +
						" AND v.id not in " + 
						"(SELECT a.veiculo.id FROM Avaria a" +
						" WHERE a.podeCircular = '" + PerguntaSimNao.NAO + "'" +
						" AND a.dataDeRegistro <= " + dataFormatadaOracle +
						" AND (a.dataDeSolucao = NULL " + 
						" OR a.dataDeSolucao >= " + dataFormatadaOracle + "))" +
						" ORDER BY v.marca, v.modelo";
		
		Query qry = JPA.em().createQuery(qrl);
		try {
			veiculos = (List<Veiculo>) qry.getResultList();
		} catch(NoResultException ex) {
			veiculos =null;
		}
		
		return veiculos;
	}

	public static Boolean estaDisponivel(Missao m) throws Exception {
		SimpleDateFormat formatar = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String dataHoraSaidaStr = formatar.format(m.dataHoraSaida.getTime());
		List<Veiculo> veiculos = listarDisponiveis(dataHoraSaidaStr, m.id,m.cpOrgaoUsuario.getId());
		for (Veiculo veiculo : veiculos) {
			if (veiculo.id.equals(m.veiculo.id)) {
				return true;
			}
		} 
		return false;
	}
	
	public static List<Veiculo> listarTodos(CpOrgaoUsuario orgaoUsuario) throws Exception {
		List<Veiculo> veiculos = Veiculo.find("cpOrgaoUsuario", orgaoUsuario).fetch();
		Collections.sort(veiculos);
		return veiculos;
	}
	
	public static List<Veiculo> listarFiltradoPor(CpOrgaoUsuario orgaoUsuario,
			DpLotacao lotacao) throws Exception  {

		List<Veiculo> veiculos;
		
		String qrl = 	"SELECT v FROM Veiculo v WHERE " +	
						"  v.cpOrgaoUsuario.id = " + orgaoUsuario.getId() + 
						" AND v.id in (SELECT L.veiculo.id FROM LotacaoVeiculo L " + 
						" where L.veiculo.id = v.id " +   	
						" AND L.lotacao.idLotacaoIni = " + lotacao.getIdLotacaoIni() + 
						" AND L.dataHoraFim IS NULL)" +
						" ORDER BY v.marca, v.modelo";

		Query qry = JPA.em().createQuery(qrl);
		try {
			veiculos = (List<Veiculo>) qry.getResultList();
		} catch(NoResultException ex) {
			veiculos = null;
		}
		return veiculos;
	}

	public void configurarLotacaoAtual() {
		this.lotacaoAtual = this.getDpLotacaoVigente();
	}

	public void configurarOdometroParaMudancaDeLotacao() {
		this.odometroEmKmAtual = this.getUltimoOdometroDeLotacao();
	}

	public DpLotacao getUltimaLotacao() {
		
		return this.getDpLotacaoVigente();
	}
}
