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

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import play.data.binding.As;
import play.data.validation.Required;
import play.db.jpa.JPA;
import play.i18n.Messages;
import play.modules.br.jus.jfrj.siga.uteis.validadores.sequence.SequenceMethods;
import play.modules.br.jus.jfrj.siga.uteis.validadores.validarAnoData.ValidarAnoData;
import play.mvc.Router;
import br.gov.jfrj.siga.cp.CpComplexo;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.model.ActiveRecord;
import br.gov.jfrj.siga.tp.util.FormataCaminhoDoContextoUrl;
import br.gov.jfrj.siga.tp.util.Reflexao;
import br.gov.jfrj.siga.tp.util.SigaTpException;
import br.jus.jfrj.siga.uteis.Sequence;
import br.jus.jfrj.siga.uteis.SiglaDocumentoType;

@SuppressWarnings("serial")
@Entity
@Audited
@Table(schema = "SIGATP")
public class RequisicaoTransporte extends TpModel implements Comparable<RequisicaoTransporte>, SequenceMethods {
	private static final String IMG_LINKNOVAJANELAICON = "/sigatp/public/images/linknovajanelaicon.png";

	public static final ActiveRecord<RequisicaoTransporte> AR = new ActiveRecord<>(RequisicaoTransporte.class);

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence_generator")
	@SequenceGenerator(name = "hibernate_sequence_generator", sequenceName = "SIGATP.hibernate_sequence")
	public Long id;

	@Sequence(propertieOrgao = "cpOrgaoUsuario", siglaDocumento = SiglaDocumentoType.RTP)
	@Column(updatable = false)
	public Long numero;

	@As(lang = { "*" }, value = { "dd/MM/yyyy HH:mm" })
	@ValidarAnoData(descricaoCampo = "Data/Hora")
	public Calendar dataHora;

	@Required
	@As(lang = { "*" }, value = { "dd/MM/yyyy HH:mm" })
	@ValidarAnoData(descricaoCampo = "Data/Hora Saida Prevista")
	public Calendar dataHoraSaidaPrevista;

	@As(lang = { "*" }, value = { "dd/MM/yyyy HH:mm" })
	@ValidarAnoData(descricaoCampo = "Data/Hora Retorno Previsto")
	public Calendar dataHoraRetornoPrevisto;

	@Required
	@Enumerated(EnumType.STRING)
	public TipoRequisicao tipoRequisicao;

	@ElementCollection(targetClass = TipoDePassageiro.class)
	@JoinTable(name = "requisicao_tipopassageiro", joinColumns = @JoinColumn(name = "requisicaoTransporte_Id"))
	@Column(name = "tipoPassageiro", nullable = false)
	@Enumerated(EnumType.STRING)
	public List<TipoDePassageiro> tiposDePassageiro;

	@Required
	@ManyToOne
	@JoinColumn(name = "ID_FINALIDADE")
	public FinalidadeRequisicao tipoFinalidade;

	public String finalidade;

	public String passageiros;

	@Required
	public String itinerarios;

	@OneToMany(orphanRemoval = true, mappedBy = "requisicaoTransporte")
	public List<Andamento> andamentos;

	@Transient
	private Andamento ultimoAndamento;

	@Transient
	public EstadoRequisicao ultimoEstado;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "missao_requisTransporte", joinColumns = @JoinColumn(name = "requisicaoTransporte_Id"), inverseJoinColumns = @JoinColumn(name = "missao_Id"))
	public List<Missao> missoes;

	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@JoinColumn(name = "ID_ORGAO_USU")
	public CpOrgaoUsuario cpOrgaoUsuario;

	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@JoinColumn(name = "ID_SOLICITANTE")
	public DpPessoa solicitante;

	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@JoinColumn(name = "ID_COMPLEXO")
	public CpComplexo cpComplexo;

	@OneToOne(fetch = FetchType.LAZY, optional = true, mappedBy = "requisicaoTransporte")
	public ServicoVeiculo servicoVeiculo;

	public Integer numeroDePassageiros;

	@Required
	public boolean origemExterna;

	@Transient
	public Long idSolicitante;

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
		saida.append(getSequence().toString());
		// saida.append(" - ");
		// saida.append(passageiros.toString());

		boolean temTipoPassageiro = false;
		for (Iterator<TipoDePassageiro> iterator = tiposDePassageiro.iterator(); iterator.hasNext();) {
			TipoDePassageiro tipo = (TipoDePassageiro) iterator.next();
			if (!temTipoPassageiro) {
				saida.append(" - ");
				temTipoPassageiro = true;
			} else {
				saida.append("; ");
			}
			saida.append(tipo.getDescricao());
		}

		// saida.append(" - ");
		// saida.append(itinerarios.toString());

		if (servicoVeiculo != null) {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("sequence", servicoVeiculo.getSequence());
			param.put("popUp", true);

			FormataCaminhoDoContextoUrl formata = new FormataCaminhoDoContextoUrl();
			String caminhoUrl = formata.retornarCaminhoContextoUrl(Router.getFullUrl("ServicosVeiculo.buscarServico", param));

			saida.append(" - ");
			saida.append("Servi&ccedil;o: " + servicoVeiculo.getSequence() + " <a href=\"#\" onclick=\"javascript:window.open('" + caminhoUrl + "');\">");
			saida.append("<img src=\"" + IMG_LINKNOVAJANELAICON + "\" alt=\"Abrir em uma nova janela\" title=\"Abrir em uma nova janela\"></a>");
			saida.append(" (");
			saida.append(servicoVeiculo.veiculo.getDadosParaExibicao() + ")");
		} else {
			saida.append(" - ");
			saida.append(itinerarios.toString());
		}

		return saida.toString();
	}

	@Override
	public String getSequence() {
		if (this.numero != null && this.numero != 0) {
			return cpOrgaoUsuario.getAcronimoOrgaoUsu().replace("-", "").toString() + "-" + Reflexao.recuperaAnotacaoField(this).substring(1) + "-"
					+ String.format("%04d", this.dataHora.get(Calendar.YEAR)) + "/" + String.format("%05d", numero) + "-" + Reflexao.recuperaAnotacaoField(this).substring(0, 1);
		} else {
			return "";
		}
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
		Query qry = JPA.em().createQuery(qrl);
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
			throw new Exception(Messages.get("requisicaoTransporte.codigoRequisicao.exception", codigoRequisicao));
		}

		CpOrgaoUsuario cpOrgaoUsuario = CpOrgaoUsuario.AR.find("acronimoOrgaoUsu", partesDoCodigo[0]).first();
		Integer ano = new Integer(Integer.parseInt(partesDoCodigo[2]));
		Long numero = new Long(Integer.parseInt(partesDoCodigo[3]));
		String siglaDocumento = partesDoCodigo[4] + partesDoCodigo[1];
		if (!Reflexao.recuperaAnotacaoField(requisicaoTransporte).equals(siglaDocumento)) {
			throw new Exception(Messages.get("requisicaoTransporte.siglaDocumento.exception", codigoRequisicao));
		}
		List<RequisicaoTransporte> requisicoesTransporte = RequisicaoTransporte.AR.find("cpOrgaoUsuario = ? and numero = ? and YEAR(dataHora) = ?", cpOrgaoUsuario, numero, ano).fetch();
		if (requisicoesTransporte.size() > 1)
			throw new Exception(Messages.get("requisicaoTransporte.codigoDuplicado.exception"));
		if (requisicoesTransporte.size() == 0)
			throw new Exception(Messages.get("requisicaoTransporte.codigoInvalido.exception"));
		return requisicoesTransporte.get(0);
	}

	public Andamento getUltimoAndamento() {
		if (andamentos != null && andamentos.size() > 0) {
			ordenarAndamentosESetarUltimo();
		} else {
			if (!this.recarregarAndamentos()) {
				setUltimoAndamento(new Andamento());
			} else {
				ordenarAndamentosESetarUltimo();
			}
		}
		return this.ultimoAndamento;
	}

	private boolean recarregarAndamentos() {
		this.andamentos = Andamento.find("", this.id).fetch();
		if (this.andamentos == null || this.andamentos.isEmpty()) {
			return false;
		}
		return true;
	}

	private void ordenarAndamentosESetarUltimo() {
		Collections.sort(andamentos);
		setUltimoAndamento(andamentos.get(andamentos.size() - 1));
	}

	public EstadoRequisicao getUltimoEstadoNestaMissao(Long idMissao) throws Exception {
		Missao missao = Missao.AR.findById(idMissao);
		Andamento andamento = Andamento.find("missao=? order by id desc", missao).first();
		return andamento.estadoRequisicao;
	}

	public EstadoRequisicao getUltimoEstado() {
		Andamento andamento = getUltimoAndamento();
		if (andamento.id == null) {
			return null;
		}
		return andamento.estadoRequisicao;
	}

	private void setUltimoAndamento(Andamento andamento) {
		this.ultimoAndamento = andamento;
	}

	@SuppressWarnings("unchecked")
	public static List<RequisicaoTransporte> listar(EstadoRequisicao... estadoRequisicao) {
		List<RequisicaoTransporte> requisicoes;

		String qrl = "SELECT req from RequisicaoTransporte req," + "Andamento an1 " + "WHERE req.id = an1.requisicaoTransporte.id " + "AND (an1.requisicaoTransporte.id, an1.dataAndamento) IN ("
				+ "SELECT an1.requisicaoTransporte.id, max(an1.dataAndamento) " + "FROM Andamento an1 " + "GROUP BY an1.requisicaoTransporte.id) " + "AND (";

		for (EstadoRequisicao estado : estadoRequisicao) {
			qrl += "an1.estadoRequisicao = '" + estado.getDescricao() + "' OR ";
		}

		qrl = qrl.substring(0, qrl.length() - 3) + ")";
		qrl += "ORDER BY req.id";

		try {
			Query qry = JPA.em().createQuery(qrl);
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

		for (EstadoRequisicao estado : Arrays.asList(EstadoRequisicao.values())) {
			if (estado.podeAgendar()) {
				qrl += "an1.estadoRequisicao = '" + estado.getDescricao() + "' OR ";
			}
		}

		qrl = qrl.substring(0, qrl.length() - 3) + ")";
		qrl += "ORDER BY req.id";

		try {
			Query qry = JPA.em().createQuery(qrl);
			requisicoes = (List<RequisicaoTransporte>) qry.getResultList();
		} catch (NoResultException ex) {
			requisicoes = null;
		}

		return requisicoes;
	}

	public boolean getPodeAgendar() throws Exception {
		Andamento ultAnd = getUltimoAndamento();
		boolean retorno = false;

		retorno = ultAnd.estadoRequisicao.podeAgendar();

		return retorno;

	}

	public Boolean cancelar(DpPessoa responsavel, String descricao) throws Exception {
		EstadoRequisicao ultimoAndamentoRequisicao = this.getUltimoAndamento().estadoRequisicao;
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

				if (missao.requisicoesTransporte.size() == 0) {
					missao.estadoMissao = EstadoMissao.CANCELADA;
				}

				if (missaoAlterada) {
					missao.save();
				}
			}
		}

		if (ultimoAndamentoRequisicao == EstadoRequisicao.ABERTA || ultimoAndamentoRequisicao == EstadoRequisicao.AUTORIZADA || ultimoAndamentoRequisicao == EstadoRequisicao.REJEITADA
				|| ultimoAndamentoRequisicao == EstadoRequisicao.PROGRAMADA) {
			Andamento andamento = new Andamento();
			andamento.responsavel = responsavel;
			andamento.dataAndamento = Calendar.getInstance();
			andamento.estadoRequisicao = EstadoRequisicao.CANCELADA;
			andamento.descricao = descricao;
			andamento.requisicaoTransporte = this;
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
		EstadoRequisicao ultimoAndamentoRequisicao = this.getUltimoAndamento().estadoRequisicao;

		if (ultimoAndamentoRequisicao == EstadoRequisicao.ABERTA || ultimoAndamentoRequisicao == EstadoRequisicao.REJEITADA || ultimoAndamentoRequisicao == EstadoRequisicao.AUTORIZADA) {

			if (ehRequisicaoServico) {
				if (servicoVeiculo != null) {
					servicoVeiculo.delete();
				} else {
					throw new SigaTpException(Messages.get("requisicaoTransporte.naoEhRequisicaoServico.exception"));
				}
			} else {
				if (servicoVeiculo != null) {
					throw new SigaTpException(Messages.get("requisicaoTransporte.ehRequisicaoServico.exception"));
				}
			}

			this.refresh();
			this.delete();
			return;
		}

		if (!ehRequisicaoServico) {
			throw new SigaTpException(Messages.get("requisicaoTransporte.naoPodeSerExcluida.exception"));
		} else {
			throw new SigaTpException(Messages.get("requisicaoTransporte.favorCancelarServico.exception"));
		}
	}

	public boolean contemTipoDePassageiro(TipoDePassageiro tipo) {
		if (tiposDePassageiro == null || tiposDePassageiro.isEmpty()) {
			return false;
		}

		if (tiposDePassageiro.contains(tipo)) {
			return true;
		}

		return false;
	}

	public boolean getPodeAlterar() throws Exception {
		Andamento ultAnd = getUltimoAndamento();
		return (ultAnd.estadoRequisicao == EstadoRequisicao.ABERTA && this.origemExterna == false);
		// return ultAnd.estadoRequisicao.comparar(EstadoRequisicao.ABERTA) <= 0;
	}

	@Override
	public Long getId() {
		return id;
	}
}
