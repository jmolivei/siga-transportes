package br.gov.jfrj.siga.tp.vraptor.job;

import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.Session;
import org.hibernate.cfg.Configuration;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.tasks.helpers.TriggerBuilder;
import br.com.caelum.vraptor.tasks.scheduler.Scheduled;
import br.com.caelum.vraptor.tasks.scheduler.TaskScheduler;
import br.gov.jfrj.siga.base.auditoria.hibernate.auditor.SigaAuditor;
import br.gov.jfrj.siga.base.auditoria.hibernate.auditor.SigaHibernateChamadaAuditor;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.model.ContextoPersistencia;
import br.gov.jfrj.siga.model.dao.HibernateUtil;
import br.gov.jfrj.siga.tp.model.Parametro;
import br.gov.jfrj.siga.tp.model.TpDao;

@ApplicationScoped
//@Scheduled()
public class CustomScheduler {
	
    private static final String CRON_EMAIL = "cron.inicio";
    private static final String CRON_WORKFLOW = "cron.iniciow";
	private static final String DATASOURCE = "java:/jboss/datasources/SigaCpDS";
    
	public CustomScheduler(TaskScheduler scheduler) 
	{
		try{
			criaEntityManager();
			
	        String cronWorkFlow = Parametro.buscarConfigSistemaEmVigor(CRON_WORKFLOW);
	        String cronEmail = Parametro.buscarConfigSistemaEmVigor(CRON_EMAIL);
	        	        
	        //TODO mudar para valores de cron recuperados anteriormente
	        //scheduler.schedule(WorkFlowNotificacao.class,new TriggerBuilder().cron("0 0/1 8-19 1/1 * ? *"), "WorkFlowNotificacao");
	        //scheduler.schedule(EmailNotificacao.class,new TriggerBuilder().cron(cronEmail), "EmailNotificacao");
		}catch(Exception e){
			Logger.getLogger("custom.schedule").info("Erro no Agendador de tarefas: " + e.getMessage());
		}
    }
    
    public static void criaEntityManager() throws Exception
    {
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("default");
		EntityManager em =  factory.createEntityManager();
		ContextoPersistencia.setEntityManager(em);
		
		TpDao.freeInstance();
		TpDao.getInstance((Session) em.getDelegate(), ((Session) em.getDelegate()).getSessionFactory().openStatelessSession());
		
		Configuration cfg = CpDao.criarHibernateCfg(DATASOURCE);
		HibernateUtil.configurarHibernate(cfg);
		SigaAuditor.configuraAuditoria(new SigaHibernateChamadaAuditor(cfg));
    }
}
