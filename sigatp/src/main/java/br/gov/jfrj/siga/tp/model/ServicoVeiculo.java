package br.gov.jfrj.siga.tp.model;

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
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.OneToOne;
import javax.persistence.Query;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import play.data.binding.As;
import play.data.validation.Required;
import play.db.jpa.GenericModel;
import play.db.jpa.JPA;
import play.i18n.Messages;
import play.modules.br.jus.jfrj.siga.uteis.validadores.sequence.SequenceMethods;
import play.modules.br.jus.jfrj.siga.uteis.validadores.validarAnoData.ValidarAnoData;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.tp.util.Reflexao;
import br.jus.jfrj.siga.uteis.Sequence;
import br.jus.jfrj.siga.uteis.SiglaDocumentoType;

@SuppressWarnings("serial")
@Entity
//@Table(name = "VEICULO_2", schema="SIGAOR")
@Audited
@Table(schema = "SIGATP")
public class ServicoVeiculo extends GenericModel implements Comparable<ServicoVeiculo>, SequenceMethods {
	public ServicoVeiculo() {
		this.id = new Long(0);
		this.numero = new Long(0);
		this.dataHora = Calendar.getInstance();
		this.situacaoServico = EstadoServico.AGENDADO;
		this.tiposDeServico = TiposDeServico.VISTORIA;
	}
	
	public String getDadosParaExibicao() {
		return this.numero.toString();
	}

	@Override
	public int compareTo(ServicoVeiculo o) {
		// ordenação decrescente
		if (this.dataHoraInicioPrevisto.before(o.dataHoraInicioPrevisto)) {
            return -1 * -1 ;
        }
        if (this.dataHoraInicioPrevisto.after(o.dataHoraInicioPrevisto)) {
            return 1 * -1;
        }
        return 0;		
	}

	@Override
	public String getSequence() {
		if (this.numero != 0) {
			return cpOrgaoUsuario.getAcronimoOrgaoUsu().replace("-","").toString() +  "-" + 
					   Reflexao.recuperaAnotacaoField(this).substring(1) + "-" + 
					   String.format("%04d",this.dataHora.get(Calendar.YEAR)) + "/" + 
					   String.format("%05d", numero) + "-" + 
					   Reflexao.recuperaAnotacaoField(this).substring(0,1);
		} else 
		{
			return "";
		}
	}
		
	public void setSequence(Object cpOrgaoUsuarioObject) {
		CpOrgaoUsuario cpOrgaoUsuario = (CpOrgaoUsuario) cpOrgaoUsuarioObject;
		int year = Calendar.getInstance().get(Calendar.YEAR);
		String qrl = "SELECT sv FROM ServicoVeiculo sv where sv.numero = "; 
		qrl = qrl + "(SELECT MAX(t.numero) FROM ServicoVeiculo t";
		qrl = qrl + " where cpOrgaoUsuario.id = " + cpOrgaoUsuario.getId();
		qrl = qrl + " and YEAR(dataHora) = " + year;
		qrl = qrl + " and sv.cpOrgaoUsuario.id = t.cpOrgaoUsuario.id";
		qrl = qrl + " and YEAR(sv.dataHora) = YEAR(t.dataHora)";		
		qrl = qrl + ")";
		Query qry = JPA.em().createQuery(qrl);
		try {
			Object obj = qry.getSingleResult();
			this.numero = ((ServicoVeiculo) obj).numero + 1;
		} catch(NoResultException ex) {
			this.numero = new Long(1);
		}
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence_generator") @SequenceGenerator(name = "hibernate_sequence_generator", sequenceName="SIGATP.hibernate_sequence") 
	public Long id;
	
	@Sequence(propertieOrgao="cpOrgaoUsuario",siglaDocumento=SiglaDocumentoType.STP)
	@Column(updatable = false)
    private Long numero;
	
 	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@JoinColumn(name = "ID_ORGAO_USU")
	public CpOrgaoUsuario cpOrgaoUsuario;  
 	
 	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@JoinColumn(name = "ID_PESSOA")
	public DpPessoa executor;
 	
	@Required
	@As(lang={"*"}, value={"dd/MM/yyyy HH:mm"})
	@ValidarAnoData(descricaoCampo="Data/Hora")
 	public Calendar dataHora;
 	
	@As(lang={"*"}, value={"dd/MM/yyyy HH:mm"})
 	public Calendar ultimaAlteracao;

	@Required
 	@ManyToOne
	@NotNull
	@JoinColumn(name = "VEICULO_ID")
	public Veiculo veiculo;

	@Required
	@As(lang={"*"}, value={"dd/MM/yyyy HH:mm"})
	@ValidarAnoData(descricaoCampo="Data/Hora Inicio Previsto")
	public Calendar dataHoraInicioPrevisto;

	@Required
	@As(lang={"*"}, value={"dd/MM/yyyy HH:mm"})
	@ValidarAnoData(descricaoCampo="Data/Hora Fim Previsto")
	public Calendar dataHoraFimPrevisto;
	
	@Required
	public String descricao;
	
	@Required
	@Enumerated(EnumType.STRING)
	public TiposDeServico tiposDeServico;

	@Required
	@Enumerated(EnumType.STRING)
	public EstadoServico situacaoServico;
	
	@OneToOne(targetEntity = RequisicaoTransporte.class)
	public RequisicaoTransporte requisicaoTransporte;

	@As(lang={"*"}, value={"dd/MM/yyyy HH:mm"})
	@ValidarAnoData(descricaoCampo="Data/Hora Inicio")
	public Calendar dataHoraInicio;
	
	@As(lang={"*"}, value={"dd/MM/yyyy HH:mm"})
	@ValidarAnoData(descricaoCampo="Data/Hora Fim")
	public Calendar dataHoraFim;

	public String motivoCancelamento;
	

	public static List<ServicoVeiculo> buscarEmAndamento() {
		return ServicoVeiculo.find("trunc(dataHoraFim) = trunc(sysdate)").fetch();
	}
	
	public static ServicoVeiculo buscar(String sequence) throws Exception {
		String[] partesDoCodigo=null;
		ServicoVeiculo servico = new ServicoVeiculo();
		
		try {
			 partesDoCodigo = sequence.split("[-/]");			
			
		} catch (Exception e) {
			throw new Exception(Messages.get("servicoVeiculo.sequence.exception", sequence));
		}
		
		CpOrgaoUsuario cpOrgaoUsuario = CpOrgaoUsuario.AR.find("acronimoOrgaoUsu",partesDoCodigo[0]).first();
		Integer ano = new Integer(Integer.parseInt(partesDoCodigo[2]));
		Long numero = new Long(Integer.parseInt(partesDoCodigo[3]));
		
		String siglaDocumento = partesDoCodigo[4] + partesDoCodigo[1];
		if (! Reflexao.recuperaAnotacaoField(servico).equals(siglaDocumento)) {
			throw new Exception(Messages.get("servicoVeiculo.siglaDocumento.exception", sequence));
		}
		
		List<ServicoVeiculo> servicos =  ServicoVeiculo.find("cpOrgaoUsuario = ? and numero = ? and YEAR(dataHora) = ?" , 
										 cpOrgaoUsuario,numero,ano).fetch();
		if (servicos.size() > 1) throw new Exception(Messages.get("servicoVeiculo.codigoDuplicado.exception"));
		if (servicos.size() == 0 ) throw new Exception(Messages.get("servicoVeiculo.codigoInvalido.exception"));
	 	return servicos.get(0);
	}

	public boolean ordemDeDatasPrevistas(Calendar dtInicio, Calendar dtFim){
		return dtInicio.before(dtFim);
	}

	@SuppressWarnings("unchecked")
	public static List<ServicoVeiculo> buscarPorVeiculo(Long idVeiculo, String dataHoraInicio) {
		String filtroVeiculo = "";
		if (idVeiculo != null) {
			filtroVeiculo = "veiculo.id = " + idVeiculo + " AND ";  
		}
		
		String dataFormatadaOracle = "to_date('" + dataHoraInicio + "', 'DD/MM/YYYY')";
		List<ServicoVeiculo> servicosVeiculo;
		
		String qrl = 	"SELECT s FROM ServicoVeiculo s WHERE " + filtroVeiculo +
					    "  situacaoServico NOT IN ('" + EstadoServico.CANCELADO + "','" + EstadoServico.REALIZADO + "')" +
						" AND trunc(dataHoraInicio) <= trunc(" + dataFormatadaOracle + ")" +  	
						" AND (dataHoraFim IS NULL OR trunc(dataHoraFim) >= trunc(" + dataFormatadaOracle + "))";

		Query qry = JPA.em().createQuery(qrl);
		try {
			servicosVeiculo = (List<ServicoVeiculo>) qry.getResultList();
		} catch(NoResultException ex) {
			servicosVeiculo = null;
		}
		return servicosVeiculo;
	}
}