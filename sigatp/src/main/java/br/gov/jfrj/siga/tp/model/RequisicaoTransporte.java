package br.gov.jfrj.siga.tp.model;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Query;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.validator.constraints.NotEmpty;

import controllers.ServicosVeiculo;
import br.com.caelum.vraptor.validator.I18nMessage;
import br.gov.jfrj.siga.cp.CpComplexo;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.model.ActiveRecord;
import br.gov.jfrj.siga.tp.util.FormataCaminhoDoContextoUrl;
import br.gov.jfrj.siga.tp.util.Reflexao;
import br.gov.jfrj.siga.tp.util.SigaTpException;
import br.gov.jfrj.siga.tp.validation.annotation.Data;
import br.gov.jfrj.siga.tp.vraptor.ConvertableEntity;
import br.gov.jfrj.siga.vraptor.handler.Resources;
import br.jus.jfrj.siga.uteis.Sequence;
import br.jus.jfrj.siga.uteis.SiglaDocumentoType;

@SuppressWarnings("serial")
@Entity
@Audited
@Table(name = "REQUISICAOTRANSPORTE", schema = "SIGATP")
public class RequisicaoTransporte extends TpModel implements Comparable<RequisicaoTransporte>, ConvertableEntity {
	private static final String IMG_LINKNOVAJANELAICON = "/sigatp/public/images/linknovajanelaicon.png";

	public static final ActiveRecord<RequisicaoTransporte> AR = new ActiveRecord<>(RequisicaoTransporte.class);

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence_generator")
	@SequenceGenerator(name = "hibernate_sequence_generator", sequenceName = "SIGATP.hibernate_sequence")
	private Long id;


	@Sequence(propertieOrgao = "cpOrgaoUsuario", siglaDocumento = SiglaDocumentoType.RTP)
	@Column(updatable = false)
	private Long numero;

	@Data(descricaoCampo = "dataHora")
	private Calendar dataHora;

	@NotNull
	@Data(descricaoCampo = "dataHoraSaidaPrevista")
	private Calendar dataHoraSaidaPrevista;

	@Data(descricaoCampo = "dataHoraRetornoPrevisto")
	private Calendar dataHoraRetornoPrevisto;

	@NotNull
	@Enumerated(EnumType.STRING)
	private TipoRequisicao tipoRequisicao;

	@ElementCollection(targetClass = TipoDePassageiro.class)
	@JoinTable(name = "requisicao_tipopassageiro", joinColumns = @JoinColumn(name = "requisicaoTransporte_Id"))
	@Column(name = "tipoPassageiro", nullable = false)
	@Enumerated(EnumType.STRING)
	private List<TipoDePassageiro> tiposDePassageiro;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "ID_FINALIDADE")
	private FinalidadeRequisicao tipoFinalidade;

	@NotNull
	@NotEmpty
	private String finalidade;

	@NotNull
	@NotEmpty
	private String passageiros;

	@NotNull
	@NotEmpty
	private String itinerarios;

	@OneToMany(orphanRemoval = true, mappedBy = "requisicaoTransporte")
	private List<Andamento> andamentos;

	@Transient
	private Andamento ultimoAndamento;

	@Transient
	private EstadoRequisicao ultimoEstado;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "missao_requisTransporte", joinColumns = @JoinColumn(name = "requisicaoTransporte_Id"), inverseJoinColumns = @JoinColumn(name = "missao_Id"))
	private List<Missao> missoes;

	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@JoinColumn(name = "ID_ORGAO_USU")
	private CpOrgaoUsuario cpOrgaoUsuario;

	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@JoinColumn(name = "ID_SOLICITANTE")
	private DpPessoa solicitante;

	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@JoinColumn(name = "ID_COMPLEXO")
	private CpComplexo cpComplexo;

	@OneToOne(fetch = FetchType.LAZY, optional = true, mappedBy = "requisicaoTransporte")
	private ServicoVeiculo servicoVeiculo;

	private Integer numeroDePassageiros;

	@NotNull
	private boolean origemExterna;

	@Transient
	private Long idSolicitante;

	public Long getNumero() {
		return numero;
	}

	public void setNumero(Long numero) {
		this.numero = numero;
	}

	public Calendar getDataHora() {
		return dataHora;
	}

	public void setDataHora(Calendar dataHora) {
		this.dataHora = dataHora;
	}

	public Calendar getDataHoraSaidaPrevista() {
		return dataHoraSaidaPrevista;
	}

	public void setDataHoraSaidaPrevista(Calendar dataHoraSaidaPrevista) {
		this.dataHoraSaidaPrevista = dataHoraSaidaPrevista;
	}

	public Calendar getDataHoraRetornoPrevisto() {
		return dataHoraRetornoPrevisto;
	}

	public void setDataHoraRetornoPrevisto(Calendar dataHoraRetornoPrevisto) {
		this.dataHoraRetornoPrevisto = dataHoraRetornoPrevisto;
	}

	public TipoRequisicao getTipoRequisicao() {
		return tipoRequisicao;
	}

	public void setTipoRequisicao(TipoRequisicao tipoRequisicao) {
		this.tipoRequisicao = tipoRequisicao;
	}

	public List<TipoDePassageiro> getTiposDePassageiro() {
		return tiposDePassageiro;
	}

	public void setTiposDePassageiro(List<TipoDePassageiro> tiposDePassageiro) {
		this.tiposDePassageiro = tiposDePassageiro;
	}

	public FinalidadeRequisicao getTipoFinalidade() {
		return tipoFinalidade;
	}

	public void setTipoFinalidade(FinalidadeRequisicao tipoFinalidade) {
		this.tipoFinalidade = tipoFinalidade;
	}

	public String getFinalidade() {
		return finalidade;
	}

	public void setFinalidade(String finalidade) {
		this.finalidade = finalidade;
	}

	public String getPassageiros() {
		return passageiros;
	}

	public void setPassageiros(String passageiros) {
		this.passageiros = passageiros;
	}

	public String getItinerarios() {
		return itinerarios;
	}

	public void setItinerarios(String itinerarios) {
		this.itinerarios = itinerarios;
	}

	public List<Andamento> getAndamentos() {
		return andamentos;
	}

	public void setAndamentos(List<Andamento> andamentos) {
		this.andamentos = andamentos;
	}

	public List<Missao> getMissoes() {
		return missoes;
	}

	public void setMissoes(List<Missao> missoes) {
		this.missoes = missoes;
	}

	public CpOrgaoUsuario getCpOrgaoUsuario() {
		return cpOrgaoUsuario;
	}

	public void setCpOrgaoUsuario(CpOrgaoUsuario cpOrgaoUsuario) {
		this.cpOrgaoUsuario = cpOrgaoUsuario;
	}

	public DpPessoa getSolicitante() {
		return solicitante;
	}

	public void setSolicitante(DpPessoa solicitante) {
		this.solicitante = solicitante;
	}

	public CpComplexo getCpComplexo() {
		return cpComplexo;
	}

	public void setCpComplexo(CpComplexo cpComplexo) {
		this.cpComplexo = cpComplexo;
	}

	public ServicoVeiculo getServicoVeiculo() {
		return servicoVeiculo;
	}

	public void setServicoVeiculo(ServicoVeiculo servicoVeiculo) {
		this.servicoVeiculo = servicoVeiculo;
	}

	public Integer getNumeroDePassageiros() {
		return numeroDePassageiros;
	}

	public void setNumeroDePassageiros(int numeroDePassageiros) {
		this.numeroDePassageiros = numeroDePassageiros;
	}

	public boolean isOrigemExterna() {
		return origemExterna;
	}

	public void setOrigemExterna(boolean origemExterna) {
		this.origemExterna = origemExterna;
	}

	public Long getIdSolicitante() {
		return idSolicitante;
	}

	public void setIdSolicitante(Long idSolicitante) {
		this.idSolicitante = idSolicitante;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setUltimoEstado(EstadoRequisicao ultimoEstado) {
		this.ultimoEstado = ultimoEstado;
	}

	public RequisicaoTransporte() {
		id = new Long(0);
		tipoRequisicao = TipoRequisicao.NORMAL;
		ultimoAndamento = new Andamento();
		this.origemExterna = false;
	}

	public String getDadosParaExibicao() {
		return this.numero.toString();
	}

	@Override
	public int compareTo(RequisicaoTransporte o) {
		return (this.dataHoraSaidaPrevista).compareTo(o.dataHoraSaidaPrevista);
	}

	public String getDescricaoCompleta() throws Exception {
		StringBuffer saida = new StringBuffer();
		saida.append(buscarSequence().toString());

		boolean temTipoPassageiro = false;
		for (Iterator<TipoDePassageiro> iterator = tiposDePassageiro.iterator(); iterator.hasNext();) {
			TipoDePassageiro tipo = (TipoDePassageiro) iterator.next();
			if (!temTipoPassageiro) {
				saida.append(" - ");
				temTipoPassageiro = true;
			} else
				saida.append("; ");

			saida.append(tipo.getDescricao());
		}

		if (servicoVeiculo != null) {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("sequence", servicoVeiculo.getSequence());
			param.put("popUp", true);

			FormataCaminhoDoContextoUrl formata = new FormataCaminhoDoContextoUrl();
			String caminhoUrl = formata.retornarCaminhoContextoUrl(Resources.urlFor(ServicosVeiculo.class, "buscarServico", param));

			saida.append(" - ");
			saida.append("Servi&ccedil;o: " + servicoVeiculo.getSequence() + " <a href=\"#\" onclick=\"javascript:window.open('" + caminhoUrl + "');\">");
			saida.append("<img src=\"" + IMG_LINKNOVAJANELAICON + "\" alt=\"Abrir em uma nova janela\" title=\"Abrir em uma nova janela\"></a>");
			saida.append(" (");
			saida.append(servicoVeiculo.getVeiculo().getDadosParaExibicao() + ")");
		} else {
			saida.append(" - ");
			saida.append(itinerarios.toString());
		}

		return saida.toString();
	}

	public String buscarSequence() {
		if (this.numero != null && this.numero != 0)
			return cpOrgaoUsuario.getAcronimoOrgaoUsu().replace("-", "").toString() + "-" + Reflexao.recuperaAnotacaoField(this).substring(1) + "-" + String.format("%04d", this.dataHora.get(Calendar.YEAR)) + "/" + String.format("%05d", numero) + "-" + Reflexao.recuperaAnotacaoField(this).substring(0, 1);
		else
			return "";
	}

	public void setSequence(Object cpOrgaoUsuarioObject) {
		CpOrgaoUsuario cpOrgaoUsuario = (CpOrgaoUsuario) cpOrgaoUsuarioObject;
		int year = Calendar.getInstance().get(Calendar.YEAR);
		String qrl = "SELECT r FROM RequisicaoTransporte r where r.numero = ";
		qrl = qrl + "(SELECT MAX(rt.numero) FROM RequisicaoTransporte rt";
		qrl = qrl + " where cpOrgaoUsuario.id = " + cpOrgaoUsuario.getId();
		qrl = qrl + " and YEAR(dataHora) = " + year;
		qrl = qrl + " and r.cpOrgaoUsuario.id = rt.cpOrgaoUsuario.id";
		qrl = qrl + " and YEAR(r.dataHora) = YEAR(rt.dataHora)";
		qrl = qrl + ")";
		Query qry = AR.em().createQuery(qrl);
		try {
			Object obj = qry.getSingleResult();
			this.numero = ((RequisicaoTransporte) obj).numero + 1;
		} catch (NoResultException ex) {
			this.numero = new Long(1);
		}
	}

	public List<Missao> getMissoesOrdenadas() {
		Collections.sort(missoes);
		Collections.reverse(missoes);
		return missoes;
	}

	public static RequisicaoTransporte buscar(String codigoRequisicao) throws Exception {
		String[] partesDoCodigo = null;
		RequisicaoTransporte requisicaoTransporte = new RequisicaoTransporte();
		try {
			partesDoCodigo = codigoRequisicao.split("[-/]");

		} catch (Exception e) {
			throw new Exception(new I18nMessage("requisicaoTransporte", "requisicaoTransporte.codigoRequisicao.exception", codigoRequisicao).getMessage());
		}

		CpOrgaoUsuario cpOrgaoUsuario = CpOrgaoUsuario.AR.find("acronimoOrgaoUsu", partesDoCodigo[0]).first();
		Integer ano = new Integer(Integer.parseInt(partesDoCodigo[2]));
		Long numero = new Long(Integer.parseInt(partesDoCodigo[3]));
		String siglaDocumento = partesDoCodigo[4] + partesDoCodigo[1];
		if (!Reflexao.recuperaAnotacaoField(requisicaoTransporte).equals(siglaDocumento))
			throw new Exception(new I18nMessage("requisicaoTransporte", "requisicaoTransporte.siglaDocumento.exception", codigoRequisicao).getMessage());

		List<RequisicaoTransporte> requisicoesTransporte = RequisicaoTransporte.AR.find("cpOrgaoUsuario = ? and numero = ? and YEAR(dataHora) = ?", cpOrgaoUsuario, numero, ano).fetch();

		if (requisicoesTransporte.size() > 1)
			throw new Exception(new I18nMessage("requisicaoTransporte", "requisicaoTransporte.codigoDuplicado.exception").getMessage());

		if (requisicoesTransporte.size() == 0)
			throw new Exception(new I18nMessage("requisicaoTransporte", "requisicaoTransporte.codigoInvalido.exception").getMessage());

		return requisicoesTransporte.get(0);
	}

	public Andamento getUltimoAndamento() {
		if (andamentos != null && andamentos.size() > 0)
			ordenarAndamentosESetarUltimo();
		else
			if (!this.recarregarAndamentos())
				setUltimoAndamento(new Andamento());
			else
				ordenarAndamentosESetarUltimo();

		return this.ultimoAndamento;
	}

	private boolean recarregarAndamentos() {
		this.andamentos = Andamento.AR.find("", this.id).fetch();
		if (this.andamentos == null || this.andamentos.isEmpty())
			return false;

		return true;
	}

	private void ordenarAndamentosESetarUltimo() {
		Collections.sort(andamentos);
		setUltimoAndamento(andamentos.get(andamentos.size() - 1));
	}

	public EstadoRequisicao getUltimoEstadoNestaMissao(Long idMissao) throws Exception {
		Missao missao = Missao.AR.findById(idMissao);
		Andamento andamento = Andamento.AR.find("missao=? order by id desc", missao).first();

		return andamento.getEstadoRequisicao();
	}

	public EstadoRequisicao getUltimoEstado() {
		Andamento andamento = getUltimoAndamento();
		if (andamento.getId() == null)
			return null;

		return andamento.getEstadoRequisicao();
	}

	private void setUltimoAndamento(Andamento andamento) {
		this.ultimoAndamento = andamento;
	}

	@SuppressWarnings("unchecked")
	public static List<RequisicaoTransporte> listar(EstadoRequisicao... estadoRequisicao) {
		List<RequisicaoTransporte> requisicoes;

		String qrl = "SELECT req from RequisicaoTransporte req," + "Andamento an1 " + "WHERE req.id = an1.requisicaoTransporte.id " + "AND (an1.requisicaoTransporte.id, an1.dataAndamento) IN ("
				+ "SELECT an1.requisicaoTransporte.id, max(an1.dataAndamento) " + "FROM Andamento an1 " + "GROUP BY an1.requisicaoTransporte.id) " + "AND (";

		for (EstadoRequisicao estado : estadoRequisicao)
			qrl += "an1.estadoRequisicao = '" + estado.getDescricao() + "' OR ";

		qrl = qrl.substring(0, qrl.length() - 3) + ")";
		qrl += "ORDER BY req.id";

		try {
			Query qry = AR.em().createQuery(qrl);
			requisicoes = (List<RequisicaoTransporte>) qry.getResultList();
		} catch (NoResultException ex) {
			requisicoes = null;
		}

		return requisicoes;
	}

	@SuppressWarnings("unchecked")
	public static List<RequisicaoTransporte> listarParaAgendamento(CpOrgaoUsuario cpOrgaoUsuario) throws Exception {
		List<RequisicaoTransporte> requisicoes;

		String qrl = "SELECT req from RequisicaoTransporte req," + "Andamento an1 " + "WHERE req.id = an1.requisicaoTransporte.id " + "AND req.cpOrgaoUsuario.idOrgaoUsu = "
				+ cpOrgaoUsuario.getIdOrgaoUsu() + " " + "AND (an1.requisicaoTransporte.id, an1.dataAndamento) IN (" + "SELECT an1.requisicaoTransporte.id, max(an1.dataAndamento) "
				+ "FROM Andamento an1 " + "GROUP BY an1.requisicaoTransporte.id) " + "AND (";

		for (EstadoRequisicao estado : Arrays.asList(EstadoRequisicao.values()))
			if (estado.podeAgendar())
				qrl += "an1.estadoRequisicao = '" + estado.getDescricao() + "' OR ";

		qrl = qrl.substring(0, qrl.length() - 3) + ")";
		qrl += "ORDER BY req.id";

		try {
			Query qry = AR.em().createQuery(qrl);
			requisicoes = (List<RequisicaoTransporte>) qry.getResultList();
		} catch (NoResultException ex) {
			requisicoes = null;
		}

		return requisicoes;
	}

	public boolean getPodeAgendar() throws Exception {
		Andamento ultAnd = getUltimoAndamento();
		boolean retorno = false;

		retorno = ultAnd.getEstadoRequisicao().podeAgendar();

		return retorno;

	}

	public Boolean cancelar(DpPessoa responsavel, String descricao) throws Exception {
		EstadoRequisicao ultimoAndamentoRequisicao = this.getUltimoAndamento().getEstadoRequisicao();
		if (ultimoAndamentoRequisicao == EstadoRequisicao.PROGRAMADA) {
			Boolean missaoAlterada = false;
			for (Missao missao : missoes) {
				for (Iterator<RequisicaoTransporte> iterator = missao.requisicoesTransporte.iterator(); iterator.hasNext();) {
					RequisicaoTransporte requisicaoTransporte = (RequisicaoTransporte) iterator.next();
					if (requisicaoTransporte.id == this.id) {
						iterator.remove();
						missaoAlterada = true;
					}
				}

				if (missao.requisicoesTransporte.size() == 0)
					missao.estadoMissao = EstadoMissao.CANCELADA;

				if (missaoAlterada)
					missao.save();
			}
		}

		if (ultimoAndamentoRequisicao == EstadoRequisicao.ABERTA || ultimoAndamentoRequisicao == EstadoRequisicao.AUTORIZADA || ultimoAndamentoRequisicao == EstadoRequisicao.REJEITADA
				|| ultimoAndamentoRequisicao == EstadoRequisicao.PROGRAMADA) {
			Andamento andamento = new Andamento();
			andamento.setResponsavel(responsavel);
			andamento.setDataAndamento(Calendar.getInstance());
			andamento.setEstadoRequisicao(EstadoRequisicao.CANCELADA);
			andamento.setDescricao(descricao);
			andamento.setRequisicaoTransporte(this);
			andamento.save();
			setUltimoAndamento(andamento);
			return true;
		}

		return false;
	}

	public boolean ordemDeDatasCorreta() {
		return this.dataHoraSaidaPrevista.before(this.dataHoraRetornoPrevisto);
	}

	public void excluir(Boolean ehRequisicaoServico) throws Exception {
		EstadoRequisicao ultimoAndamentoRequisicao = this.getUltimoAndamento().getEstadoRequisicao();

		if (ultimoAndamentoRequisicao == EstadoRequisicao.ABERTA || ultimoAndamentoRequisicao == EstadoRequisicao.REJEITADA || ultimoAndamentoRequisicao == EstadoRequisicao.AUTORIZADA) {

			if (ehRequisicaoServico) {
				if (servicoVeiculo != null)
					servicoVeiculo.delete();
				else
					throw new SigaTpException(new I18nMessage("requisicaoTransporte", "requisicaoTransporte.naoEhRequisicaoServico.exception").getMessage());
			} else
				if (servicoVeiculo != null)
					throw new SigaTpException(new I18nMessage("requisicaoTransporte", "requisicaoTransporte.ehRequisicaoServico.exception").getMessage());

			this.refresh();
			this.delete();
			return;
		}

		if (!ehRequisicaoServico)
			throw new SigaTpException(new I18nMessage("requisicaoTransporte", "requisicaoTransporte.naoPodeSerExcluida.exception").getMessage());
		else
			throw new SigaTpException(new I18nMessage("requisicaoTransporte", "requisicaoTransporte.favorCancelarServico.exception").getMessage());
	}

	public boolean contemTipoDePassageiro(TipoDePassageiro tipo) {
		if (tiposDePassageiro == null || tiposDePassageiro.isEmpty())
			return false;

		if (tiposDePassageiro.contains(tipo))
			return true;

		return false;
	}

	public boolean getPodeAlterar() throws Exception {
		Andamento ultAnd = getUltimoAndamento();
		return (ultAnd.getEstadoRequisicao() == EstadoRequisicao.ABERTA && this.origemExterna == false);
	}

	@Override
	public Long getId() {
		return id;
	}
}
