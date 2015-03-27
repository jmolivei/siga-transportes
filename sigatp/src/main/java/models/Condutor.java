package models;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Query;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import play.data.binding.As;
import play.data.validation.CheckWith;
import play.data.validation.Email;
import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.GenericModel;
import play.db.jpa.JPA;
import play.i18n.Messages;
import play.modules.br.jus.jfrj.siga.uteis.validadores.telefone.TelefoneCheck;
import play.modules.br.jus.jfrj.siga.uteis.validadores.validarAnoData.ValidarAnoData;
import uteis.PerguntaSimNao;
import validadores.CnhCheck;
import br.gov.jfrj.siga.cp.CpComplexo;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.tp.model.CpRepository;
import br.jus.jfrj.siga.uteis.UpperCase;

@SuppressWarnings("serial")
@Entity
//@Table(name = "CONDUTOR_2", schema="SIGAOR")
@Audited
@Table(schema = "SIGATP")
public class Condutor extends GenericModel implements Comparable<Condutor> {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence_generator") @SequenceGenerator(name = "hibernate_sequence_generator", sequenceName="SIGATP.hibernate_sequence") 
	public Long id;
	
 	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@JoinColumn(name = "ID_ORGAO_USU")
	public CpOrgaoUsuario cpOrgaoUsuario;
	
 	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@OneToOne(targetEntity = DpPessoa.class)
	@Unique(message="condutor.dppessoa.unique")
	public DpPessoa dpPessoa;
	
	@Enumerated(EnumType.STRING)
	public CategoriaCNH categoriaCNH;
	
	@Required
	@As(lang={"*"}, value={"dd/MM/yyyy"})
	@ValidarAnoData(intervalo=5, descricaoCampo="Data de Vencimento da CNH")
	public Calendar dataVencimentoCNH;
	
	@Required
	@CheckWith(CnhCheck.class)
	public String numeroCNH;
	
	@CheckWith(TelefoneCheck.class)
	public String telefonePessoal;
	
	@CheckWith(TelefoneCheck.class)
	public String celularPessoal;
	
	@Required
	@CheckWith(TelefoneCheck.class)
	public String telefoneInstitucional;
	
	@CheckWith(TelefoneCheck.class)
	public String celularInstitucional;
	
	@OneToMany(mappedBy="condutor")
	private List<EscalaDeTrabalho> escala;
	
	@Email(message="Email invalido")
	public String emailPessoal;
	
	//fvg - Mudar depois para Imagem
	public byte[] conteudoimagemblob;
	
	@Transient
	public Imagem arquivo; 
	
	@Transient
	public String situacaoImagem;

	public void setArquivo(File file) {
		this.arquivo = Imagem.newInstance(file);
	}
	//fim fvg - Mudar depois para Imagem
	
	public String endereco;

	@UpperCase
	private String observacao;
	
	public Condutor() {
		this.dpPessoa = new DpPessoa();
		this.id = new Long(0);
		this.categoriaCNH = CategoriaCNH.D;
		escala = new ArrayList<EscalaDeTrabalho>();
	}
	
	public static List<DpPessoa> getPossiveisCondutores(CpOrgaoUsuario cpOrgaoUsuario) {
		List<LotacaoAtdRequisicao> lotacoesAtdRequisicao = LotacaoAtdRequisicao.find("cpOrgaoUsuario", cpOrgaoUsuario).fetch();
		List<DpLotacao> lotacoes = new ArrayList<DpLotacao>();
		for (LotacaoAtdRequisicao lotacaoAtdRequisicao : lotacoesAtdRequisicao) {
			lotacoes.add(lotacaoAtdRequisicao.dpLotacao);
		}
		
		List<DpPessoa> possiveisCondutores = DpPessoa.AR.find("lotacao in (?)", lotacoes.toArray()).fetch();
		return possiveisCondutores;
	}
	
	@Override
	public int compareTo(Condutor o) {
		return this.dpPessoa.getNomePessoa().compareTo(o.dpPessoa.getNomePessoa());
	}
	
	public String getMatricula() {
		return this.dpPessoa.getMatricula().toString();
	}

	public String getNomePessoaAI() {
		return this.dpPessoa.getNomePessoaAI();
	}
	
	public boolean getVencimentoCNHExpirado() {
		if(this.dataVencimentoCNH == null) {
			return false;
		}
		if(this.dataVencimentoCNH.compareTo(Calendar.getInstance()) > 0 ) {
			return false;
		}
		return true;
	}
	
	public String getNome() {
		return this.dpPessoa.getNomePessoaAI().toString();
	}
	
	public String getDadosParaExibicao() {
		return getMatricula() + " - " + getNome();
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public String getObservacao() {
		return observacao;
	}
	
	public static List<Condutor> listarDisponiveis(String dataSaida, Long idMissao, Long idOrgao, PerguntaSimNao inicioRapido) throws Exception {
		List<Condutor> condutores;
		if(inicioRapido == null) {
			inicioRapido = PerguntaSimNao.NAO;
		}
		String dataFormatadaOracle = "to_date('" + dataSaida + "', 'DD/MM/YYYY HH24:mi')";
		
		String qrl = 	"SELECT c FROM Condutor c " +
						" WHERE trunc(c.dataVencimentoCNH) > trunc(" + dataFormatadaOracle + ")" +	
						"  AND c.cpOrgaoUsuario.id in  " + 
						"(SELECT cp.id FROM CpOrgaoUsuario cp" +
						" WHERE  cp.id = " + idOrgao + ")" +					
						" AND c.id not in ";
		if (! inicioRapido.equals(PerguntaSimNao.SIM)) { 
			qrl = qrl +	"(SELECT a.condutor.id FROM Afastamento a" +
						" WHERE  a.condutor.id = c.id" +
						" AND   a.dataHoraInicio < " + dataFormatadaOracle +
						" AND    (a.dataHoraFim = NULL " + 
						" OR    a.dataHoraFim > " + dataFormatadaOracle + "))" +
						" AND c.id not in";
		}				
			qrl = qrl +	"(SELECT m.condutor.id FROM Missao m" +
						" WHERE  m.condutor.id = c.id" +
						" AND    m.estadoMissao != '" + EstadoMissao.CANCELADA + "'" +
						" AND    m.estadoMissao != '" + EstadoMissao.FINALIZADA + "'" +
						" AND    m.estadoMissao != '" + EstadoMissao.PROGRAMADA + "'" +
						" AND    m.id != " + idMissao +
						" AND   m.dataHoraSaida < " + dataFormatadaOracle +
						" AND    (m.dataHoraRetorno = NULL " + 
						" OR    m.dataHoraRetorno > " + dataFormatadaOracle + "))" + 
						" ORDER BY c.dpPessoa.nomePessoa"; 
						 
		Query qry = JPA.em().createQuery(qrl);
		try {
			condutores = (List<Condutor>) qry.getResultList();
		} catch(NoResultException ex) {
			condutores =null;
		}
		
		if(condutores != null && condutores.size() > 0 && (! inicioRapido.equals(PerguntaSimNao.SIM) )) {
			for (Iterator<Condutor> iterator = condutores.iterator(); iterator.hasNext();) {
				Condutor condutor = (Condutor) iterator.next();
		
				if (condutor.estaDePlantao(dataFormatadaOracle)) {
					continue;
				}
				
				if (! condutor.estaEscalado(dataSaida))  {
					iterator.remove();
				}			
			}
		}
		
		return condutores;
	}
	
	
	public static List<Condutor> listarEscalados(boolean mostrarCanceladosEFinalizados, CpOrgaoUsuario cpOrgaoUsuario) throws Exception {
		return listarEscaladosDoComplexo(mostrarCanceladosEFinalizados,null, cpOrgaoUsuario);
	}
	
	public static List<Condutor> listarEscaladosDoComplexo(boolean mostrarCanceladosEFinalizados,CpComplexo cpComplexo, CpOrgaoUsuario cpOrgaoUsuario ) throws Exception {
		List<Condutor> condutores;
		
		StringBuffer query = new StringBuffer();

		query.append("SELECT c FROM Condutor c");
		query.append(" WHERE c.id in ");	
		query.append("(SELECT m.condutor.id FROM Missao m");
		query.append(" WHERE  m.condutor.id = c.id");
		query.append(" AND    c.dpPessoa.idPessoaIni in (select d.idPessoaIni from DpPessoa d where d.orgaoUsuario.idOrgaoUsu = " + cpOrgaoUsuario.getIdOrgaoUsu() + " AND DATA_FIM_PESSOA IS NULL)");
		if(!mostrarCanceladosEFinalizados) {
			query.append(" AND    m.estadoMissao != '" + EstadoMissao.CANCELADA + "'");
			query.append(" AND    m.estadoMissao != '" + EstadoMissao.FINALIZADA + "'");
		}
		
		/**
		 * Filtrar por complexo
		 */
		if(cpComplexo != null) {
			query.append(" AND    m.cpComplexo.idComplexo = " + cpComplexo.getIdComplexo());
		}
		
	 	query.append(")"); 
						 
		Query qry = JPA.em().createQuery(query.toString());
		try {
			condutores = (List<Condutor>) qry.getResultList();
			Collections.sort(condutores);
		} catch(NoResultException ex) {
			condutores =null;
		}
		
		return condutores;
	}
	
	
	public boolean estaDePlantao(String dataFormatadaOracle) {
		List<Plantao> plantoes=null;
		 		
		String qrl = 	"SELECT p.condutor.id FROM Plantao p" +
						" WHERE  p.condutor.id = " + this.id +
						" AND   (p.dataHoraInicio <= " + dataFormatadaOracle +
						" AND   ( p.dataHoraFim = NULL OR p.dataHoraFim >= " + dataFormatadaOracle + "))"; 
	
		Query qry = JPA.em().createQuery(qrl);
		try {
			plantoes = (List<Plantao>) qry.getResultList();
			if (plantoes != null && plantoes.size() > 0) {
				return true;
			} else {
				return false;
			}
	
		} catch(NoResultException ex) {
			plantoes =null;
		}
		return false;
	}
	
	public Boolean estaEscalado(String dataMissao) throws Exception {
		Condutor condutor = this;
		EscalaDeTrabalho escalaVigente;
		String dataFormatadaOracle = "to_date('" + dataMissao + "', 'DD/MM/YYYY HH24:mi')";
		StringBuffer hqlVigentes = new StringBuffer();
		hqlVigentes.append("condutor = ? and ");
		hqlVigentes.append("dataVigenciaInicio < ");
		hqlVigentes.append(dataFormatadaOracle);
		hqlVigentes.append(" and ((dataVigenciaFim is null) or (dataVigenciaFim > ");
		hqlVigentes.append(dataFormatadaOracle);
		hqlVigentes.append(")) ");
		hqlVigentes.append("order by dataVigenciaInicio desc ");
		List<EscalaDeTrabalho> escalasDeTrabalho = EscalaDeTrabalho.find(hqlVigentes.toString(),condutor).fetch();
		if (escalasDeTrabalho.size() == 0) {
			return false;
		} 
		
		escalaVigente = escalasDeTrabalho.get(0);
		
		if (escalasDeTrabalho.size() > 1) {
			throw new Exception(Messages.get("condutor.escalasDeTrabalho.exception", escalasDeTrabalho.get(0).condutor.id));
		}
		
		return escalaVigente.estaEscaladoNesteDia(dataMissao);
		
	}

	public static Boolean estaDisponivel(Missao m) throws Exception {
		/*BigDecimal contadorIndisponibilidade;
		
		String dataSaidaFormatadaOracle = "to_date('" + m.dataHoraSaida + "', 'DD/MM/YYYY HH24:mi')";
		String dataRetornoFormatadaOracle = "to_date('" + m.dataHoraRetorno + "', 'DD/MM/YYYY HH24:mi')";
		
		String qrl = 	"SELECT COUNT(*) FROM MISSAO A " + 
		" WHERE A.ID <> " + m.id +
		" AND A.CONDUTOR_ID = " + m.condutor.id  +
		" AND A.ESTADOMISSAO NOT IN  ('" + EstadoMissao.CANCELADA + "','" + EstadoMissao.FINALIZADA +"')" +
		" AND NVL(A.DATAHORARETORNO,'31/12/9999 11:59') > " + dataSaidaFormatadaOracle + 
		" AND NVL(" + dataRetornoFormatadaOracle + ",'31/12/9999 11:59') > A.DATAHORASAIDA";
		Query qry = JPA.em().createNativeQuery(qrl);
		try {
			contadorIndisponibilidade = (BigDecimal) qry.getSingleResult();
		} catch(NoResultException ex) {
			contadorIndisponibilidade = new BigDecimal(0);
		}
	    return (contadorIndisponibilidade == new BigDecimal(0) ? true : false) ; */
		SimpleDateFormat formatar = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String dataHoraSaidaStr = formatar.format(m.dataHoraSaida.getTime());
		List<Condutor> condutores = listarDisponiveis(dataHoraSaidaStr, m.id, m.cpOrgaoUsuario.getId(), m.inicioRapido);
	
		
		if(condutores.contains(m.condutor)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object other) {
		try {
			Condutor outroCondutor = (Condutor) other;
			return this.id.equals(outroCondutor.id);
		} catch(Exception e) {
			return false;
		}
	}

	public static List<Condutor> listarTodos(CpOrgaoUsuario orgaoUsuario) throws Exception {
		List<Condutor> condutores = Condutor.find("cpOrgaoUsuario", orgaoUsuario).fetch();
		Collections.sort(condutores);
		return condutores;
	}

	public static List<Condutor> listarFiltradoPor(CpOrgaoUsuario orgaoUsuario,
			DpLotacao lotacao) throws Exception  {
		List<Condutor> condutores = Condutor.find("cpOrgaoUsuario=? and dpPessoa.lotacao.idLotacaoIni = ?", orgaoUsuario, lotacao.getIdInicial() ).fetch();
		Collections.sort(condutores);
		return condutores;
	}

	public static Condutor recuperarLogado(DpPessoa titular, CpOrgaoUsuario orgaoUsuario) {
		return Condutor.find("dpPessoa.idPessoaIni=? and cpOrgaoUsuario=?",titular.getIdInicial(),orgaoUsuario).first(); 
	}
}