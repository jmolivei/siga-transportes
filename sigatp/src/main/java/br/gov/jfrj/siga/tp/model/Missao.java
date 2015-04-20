package br.gov.jfrj.siga.tp.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import play.data.binding.As;
import play.data.validation.Required;
import play.i18n.Messages;
import play.modules.br.jus.jfrj.siga.uteis.validadores.sequence.SequenceMethods;
import play.modules.br.jus.jfrj.siga.uteis.validadores.upperCase.UpperCase;
import play.modules.br.jus.jfrj.siga.uteis.validadores.validarAnoData.ValidarAnoData;
import br.gov.jfrj.siga.cp.CpComplexo;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.model.ActiveRecord;
import br.gov.jfrj.siga.tp.util.PerguntaSimNao;
import br.gov.jfrj.siga.tp.util.Reflexao;
import br.gov.jfrj.siga.tp.vraptor.ConvertableEntity;
import br.jus.jfrj.siga.uteis.Sequence;
import br.jus.jfrj.siga.uteis.SiglaDocumentoType;

@SuppressWarnings("serial")
@Entity
// @Table(name = "VEICULO_2", schema="SIGAOR")
@Audited
@Table(schema = "SIGATP")
public class Missao extends TpModel implements ConvertableEntity, Comparable<Missao>, SequenceMethods {

	public static ActiveRecord<Missao> AR = new ActiveRecord<>(Missao.class);

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence_generator")
	@SequenceGenerator(name = "hibernate_sequence_generator", sequenceName = "SIGATP.hibernate_sequence")
	private Long id;

	@Sequence(propertieOrgao = "cpOrgaoUsuario", siglaDocumento = SiglaDocumentoType.MTP)
	@Column(updatable = false)
	private Long numero;

	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@JoinColumn(name = "ID_PESSOA")
	public DpPessoa responsavel;

	@As(lang = { "*" }, value = { "dd/MM/yyyy HH:mm" })
	public Calendar dataHora;

	@Transient
	public double distanciaPercorridaEmKm;

	@Transient
	@As(lang = { "*" }, value = { "HH:mm" })
	@ValidarAnoData(descricaoCampo = "Data/Hora")
	public Calendar tempoBruto;

	public double consumoEmLitros;

	@Required
	@NotNull
	@As(lang = { "*" }, value = { "dd/MM/yyyy HH:mm" })
	@ValidarAnoData(descricaoCampo = "Data/Hora")
	public Calendar dataHoraSaida;

	public double odometroSaidaEmKm;

	@Enumerated(EnumType.STRING)
	public PerguntaSimNao estepe;

	@Enumerated(EnumType.STRING)
	public PerguntaSimNao avariasAparentesSaida;

	@Enumerated(EnumType.STRING)
	public PerguntaSimNao limpeza;

	@Enumerated(EnumType.STRING)
	public NivelDeCombustivel nivelCombustivelSaida;

	@Enumerated(EnumType.STRING)
	public PerguntaSimNao triangulos;

	@Enumerated(EnumType.STRING)
	public PerguntaSimNao extintor;

	@Enumerated(EnumType.STRING)
	public PerguntaSimNao ferramentas;

	@Enumerated(EnumType.STRING)
	public PerguntaSimNao licenca;

	@Enumerated(EnumType.STRING)
	public PerguntaSimNao cartaoSeguro;

	@Enumerated(EnumType.STRING)
	public PerguntaSimNao cartaoAbastecimento;

	@Enumerated(EnumType.STRING)
	public PerguntaSimNao cartaoSaida;

	@As(lang = { "*" }, value = { "dd/MM/yyyy HH:mm" })
	@ValidarAnoData(descricaoCampo = "Data/Hora Retorno")
	public Calendar dataHoraRetorno;

	public double odometroRetornoEmKm;

	@Enumerated(EnumType.STRING)
	public PerguntaSimNao avariasAparentesRetorno;

	@Enumerated(EnumType.STRING)
	public NivelDeCombustivel nivelCombustivelRetorno;

	@UpperCase
	public String ocorrencias;

	@UpperCase
	public String itinerarioCompleto;

	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@JoinColumn(name = "ID_ORGAO_USU")
	public CpOrgaoUsuario cpOrgaoUsuario;

	@Required(message = "missao.requisicoesTransporte.required")
	@NotNull
	@ManyToMany
	@JoinTable(name = "missao_requisTransporte", joinColumns = @JoinColumn(name = "missao_Id"), inverseJoinColumns = @JoinColumn(name = "requisicaoTransporte_Id"))
	// @JoinTable(name = "missao_requisTransporte", joinColumns = @JoinColumn(name = "missao_Id"))
	public List<RequisicaoTransporte> requisicoesTransporte;

	@Required
	@ManyToOne
	public Veiculo veiculo;

	@Required
	@NotNull
	@ManyToOne
	public Condutor condutor;

	@Enumerated(EnumType.STRING)
	public EstadoMissao estadoMissao;

	@UpperCase
	public String justificativa;

	@Enumerated(EnumType.STRING)
	public PerguntaSimNao inicioRapido;

	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@JoinColumn(name = "ID_COMPLEXO")
	public CpComplexo cpComplexo;

	public Missao() {
		this.id = new Long(0);
		this.numero = new Long(0);
		this.dataHora = Calendar.getInstance();
		this.avariasAparentesRetorno = PerguntaSimNao.NAO;
		this.avariasAparentesSaida = PerguntaSimNao.NAO;
		this.cartaoAbastecimento = PerguntaSimNao.SIM;
		this.cartaoSaida = PerguntaSimNao.SIM;
		this.cartaoSeguro = PerguntaSimNao.SIM;
		this.estepe = PerguntaSimNao.SIM;
		this.extintor = PerguntaSimNao.SIM;
		this.ferramentas = PerguntaSimNao.SIM;
		this.licenca = PerguntaSimNao.SIM;
		this.limpeza = PerguntaSimNao.SIM;
		this.triangulos = PerguntaSimNao.SIM;
		this.nivelCombustivelRetorno = NivelDeCombustivel.A;
		this.nivelCombustivelSaida = NivelDeCombustivel.A;
		this.estadoMissao = EstadoMissao.PROGRAMADA;
		this.inicioRapido = PerguntaSimNao.NAO;
	}

	public String getDadosParaExibicao() {
		return this.numero.toString();
	}

	@Override
	public int compareTo(Missao o) {
		return (this.numero).compareTo(o.numero);
	}

	@Override
	public String getSequence() {
		if (this.numero != 0) {
			return cpOrgaoUsuario.getAcronimoOrgaoUsu().replace("-", "").toString() + "-" + Reflexao.recuperaAnotacaoField(this).substring(1) + "-"
					+ String.format("%04d", this.dataHora.get(Calendar.YEAR)) + "/" + String.format("%05d", numero) + "-" + Reflexao.recuperaAnotacaoField(this).substring(0, 1);

		} else {
			return "";
		}
	}

	public void setSequence(Object cpOrgaoUsuarioObject) {
		CpOrgaoUsuario cpOrgaoUsuario = (CpOrgaoUsuario) cpOrgaoUsuarioObject;
		int year = Calendar.getInstance().get(Calendar.YEAR);
		String qrl = "SELECT m FROM Missao m where m.numero = ";
		qrl = qrl + "(SELECT MAX(t.numero) FROM Missao t";
		qrl = qrl + " where cpOrgaoUsuario.id = " + cpOrgaoUsuario.getId();
		qrl = qrl + " and YEAR(dataHora) = " + year;
		qrl = qrl + " and m.cpOrgaoUsuario.id = t.cpOrgaoUsuario.id";
		qrl = qrl + " and YEAR(m.dataHora) = YEAR(t.dataHora)";
		qrl = qrl + ") order by m.numero desc";
		Query qry = AR.em().createQuery(qrl);
		try {
			Object obj = qry.getResultList().get(0);
			this.numero = ((Missao) obj).numero + 1;
		} catch (IndexOutOfBoundsException ex) {
			this.numero = new Long(1);
		}

	}

	public static List<Missao> buscarEmAndamento() {
		return Missao.AR.find("trunc(dataHoraSaida) = trunc(sysdate)").fetch();
	}

	public static Missao buscar(String sequence) throws Exception {
		String[] partesDoCodigo = null;
		Missao missao = new Missao();
		try {
			partesDoCodigo = sequence.split("[-/]");

		} catch (Exception e) {
			throw new Exception(Messages.get("missao.buscar.sequence.exception", sequence));
		}

		CpOrgaoUsuario cpOrgaoUsuario = TpDao.find(CpOrgaoUsuario.class, "acronimoOrgaoUsu", partesDoCodigo[0]).first();
		Integer ano = new Integer(Integer.parseInt(partesDoCodigo[2]));
		Long numero = new Long(Integer.parseInt(partesDoCodigo[3]));
		String siglaDocumento = partesDoCodigo[4] + partesDoCodigo[1];
		if (!Reflexao.recuperaAnotacaoField(missao).equals(siglaDocumento)) {
			throw new Exception(Messages.get("missao.buscar.siglaDocumento.exception", sequence));
		}
		List<Missao> missoes = Missao.AR.find("cpOrgaoUsuario = ? and numero = ? and YEAR(dataHora) = ?", cpOrgaoUsuario, numero, ano).fetch();
		if (missoes.size() > 1)
			throw new Exception(Messages.get("missao.buscar.codigoDuplicado.exception"));
		if (missoes.size() == 0)
			throw new Exception(Messages.get("missao.buscar.codigoInvalido.exception"));
		return missoes.get(0);
	}

	public static List<Missao> buscarPorCondutor(Long IdCondutor, Calendar dataHoraInicio) {
		return Missao.AR.find("condutor.id = ? AND dataHoraSaida <= ? AND (dataHoraRetorno is null OR dataHoraRetorno >= ?) AND estadoMissao NOT IN (?,?) order by dataHoraSaida", IdCondutor,
				dataHoraInicio, dataHoraInicio, EstadoMissao.CANCELADA, EstadoMissao.FINALIZADA).fetch();
	}

	public static List<Missao> buscarMissoesEmAbertoPorCondutor(Condutor condutor) {
		return Missao.AR.find("condutor.id = ? AND estadoMissao NOT IN (?,?) order by dataHoraSaida", condutor.getId(), EstadoMissao.CANCELADA, EstadoMissao.FINALIZADA).fetch();
	}

	public static List<Missao> buscarTodasAsMissoesPorCondutor(Condutor condutor) {
		if (condutor == null) {
			return new ArrayList<Missao>();
		}
		;
		return Missao.AR.find("condutor.id = ? order by dataHoraSaida desc", condutor.getId()).fetch();
	}

	public static List<Missao> buscarPorVeiculos(Long IdVeiculo, String dataHoraInicio) {

		return filtrarMissoes("veiculo", IdVeiculo, dataHoraInicio);

	}

	public static List<Missao> buscarPorCondutores(Long IdCondutor, String dataHoraInicio) {

		return filtrarMissoes("condutor", IdCondutor, dataHoraInicio);
	}

	@SuppressWarnings("unchecked")
	private static List<Missao> filtrarMissoes(String entidade, Long idEntidade, String dataHoraInicio) {
		String filtroEntidade = "";
		if (idEntidade != null) {
			filtroEntidade = entidade + ".id = " + idEntidade + " AND ";
		}

		String dataFormatadaOracle = "to_date('" + dataHoraInicio + "', 'DD/MM/YYYY')";
		List<Missao> missoes;

		String qrl = "SELECT m FROM Missao m WHERE " + filtroEntidade
				+
				// "  estadoMissao NOT IN ('" + EstadoMissao.CANCELADA + "','" + EstadoMissao.FINALIZADA + "')" +
				"  estadoMissao NOT IN ('" + EstadoMissao.CANCELADA + "')" + " AND trunc(dataHoraSaida) <= trunc(" + dataFormatadaOracle + ")"
				+ " AND (dataHoraRetorno IS NULL OR trunc(dataHoraRetorno) >= trunc(" + dataFormatadaOracle + "))";

		Query qry = AR.em().createQuery(qrl);
		try {
			missoes = (List<Missao>) qry.getResultList();
		} catch (NoResultException ex) {
			missoes = null;
		}
		return missoes;
	}

	@SuppressWarnings("unchecked")
	public static List<Missao> retornarMissoes(String entidade, Long idEntidade, Long usuarioId, Calendar dataHoraInicio, Calendar dataHoraFim) {
		String filtroEntidade = "";
		if (idEntidade != null) {
			filtroEntidade = entidade + " = " + idEntidade;
		}

		String qrl = "SELECT m FROM Missao m";
		qrl += " WHERE " + filtroEntidade;
		SimpleDateFormat formatar = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String dataInicioFormatadaOracle = "to_date('" + formatar.format(dataHoraInicio.getTime()) + "', 'DD/MM/YYYY HH24:MI')";
		String dataFimFormatadaOracle = dataHoraFim == null ? "" : "to_date('" + formatar.format(dataHoraFim.getTime()) + "', 'DD/MM/YYYY HH24:MI')";

		if (!dataFimFormatadaOracle.isEmpty()) {
			qrl += " AND dataHoraSaida BETWEEN " + dataInicioFormatadaOracle + " AND " + dataFimFormatadaOracle;
		} else {
			qrl += " AND trunc(dataHoraSaida) <= trunc(" + dataInicioFormatadaOracle + ")";
		}

		qrl += " AND cpOrgaoUsuario.id = " + usuarioId;

		Query qry = AR.em().createQuery(qrl);
		return (List<Missao>) qry.getResultList();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}