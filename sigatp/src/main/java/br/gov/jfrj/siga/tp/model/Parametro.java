package br.gov.jfrj.siga.tp.model;

import java.util.Calendar;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.gov.jfrj.siga.cp.CpComplexo;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.model.ActiveRecord;
import br.gov.jfrj.siga.tp.validation.annotation.Data;
import br.gov.jfrj.siga.vraptor.converter.ConvertableEntity;

@SuppressWarnings("serial")
@Entity
@Audited
@Table(schema = "SIGATP")
public class Parametro extends TpModel implements ConvertableEntity {

    public static final ActiveRecord<Parametro> AR = new ActiveRecord<Parametro>(Parametro.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(Parametro.class);

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence_generator")
    @SequenceGenerator(name = "hibernate_sequence_generator", sequenceName = "SIGATP.hibernate_sequence")
    private Long id;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name = "ID_ORGAO_USU")
    private CpOrgaoUsuario cpOrgaoUsuario;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name = "ID_LOTACAO")
    private DpLotacao dpLotacao;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name = "ID_PESSOA")
    private DpPessoa dpPessoa;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name = "ID_COMPLEXO")
    private CpComplexo cpComplexo;

    @NotNull
    private String nomeParametro;

    @NotNull
    private String valorParametro;

    @Data(descricaoCampo = "Data de Inicio")
    private Calendar dataInicio;

    @Data(descricaoCampo = "Data do Fim")
    private Calendar dataFim;

    @NotNull
    private String descricao;

    public Parametro() {
        this.id = 0L;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CpOrgaoUsuario getCpOrgaoUsuario() {
        return cpOrgaoUsuario;
    }

    public void setCpOrgaoUsuario(CpOrgaoUsuario cpOrgaoUsuario) {
        this.cpOrgaoUsuario = cpOrgaoUsuario;
    }

    public DpLotacao getDpLotacao() {
        return dpLotacao;
    }

    public void setDpLotacao(DpLotacao dpLotacao) {
        this.dpLotacao = dpLotacao;
    }

    public DpPessoa getDpPessoa() {
        return dpPessoa;
    }

    public void setDpPessoa(DpPessoa dpPessoa) {
        this.dpPessoa = dpPessoa;
    }

    public CpComplexo getCpComplexo() {
        return cpComplexo;
    }

    public void setCpComplexo(CpComplexo cpComplexo) {
        this.cpComplexo = cpComplexo;
    }

    public String getNomeParametro() {
        return nomeParametro;
    }

    public void setNomeParametro(String nomeParametro) {
        this.nomeParametro = nomeParametro;
    }

    public String getValorParametro() {
        return valorParametro;
    }

    public void setValorParametro(String valorParametro) {
        this.valorParametro = valorParametro;
    }

    public Calendar getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Calendar dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Calendar getDataFim() {
        return dataFim;
    }

    public void setDataFim(Calendar dataFim) {
        this.dataFim = dataFim;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @SuppressWarnings("unchecked")
    public static List<Parametro> listarTodos() {
        return Parametro.AR.findAll();
    }

    public static Parametro buscar(Long idBuscar) {
        Parametro retorno = null;
        try {
            retorno = Parametro.AR.find("id = ?", idBuscar).first();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
        return retorno;
    }

    public static String buscarConfigSistemaEmVigor(String stringCron) {
        /* TODO OSI_22 - PROBLEMA DE MERGE
         * Metodo implementado pois ocorreu um conflito com o código do cliente
         * ao efetuar o MERGE entre os branchs da DB1 e do SIGA.
         * 
         * Como é utilizado nos controllers de Andamento (Andamentos.java e ParametroController.java)
         * foi implementado somente para nao quebrar o build.
         * 
         * Procuramos nos fontes do git (https://github.com/trfrj/siga-tp) nas branchs e nao encontramos a implementação do 
         * metodo.
         */
        throw new UnsupportedOperationException();
    }
}
