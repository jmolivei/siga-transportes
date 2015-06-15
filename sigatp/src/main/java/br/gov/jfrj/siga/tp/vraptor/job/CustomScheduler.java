package br.gov.jfrj.siga.tp.vraptor.job;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.tasks.helpers.TriggerBuilder;
import br.com.caelum.vraptor.tasks.scheduler.Scheduled;
import br.com.caelum.vraptor.tasks.scheduler.TaskScheduler;
import br.gov.jfrj.siga.model.ContextoPersistencia;
import br.gov.jfrj.siga.tp.model.Parametro;

@ApplicationScoped
//@Scheduled(fixedRate = 1)
public class CustomScheduler {
	
    private static final String CRON_EMAIL = "cron.inicio";
    private static final String CRON_WORKFLOW = "cron.iniciow";
    
	public CustomScheduler(TaskScheduler scheduler) 
	{
		criaEntityManager();
		
        String cronWorkFlow = Parametro.buscarConfigSistemaEmVigor(CRON_WORKFLOW);
        
        //System.out.println("CRON--------- :"+cronWorkFlow);
        
        //scheduler.schedule(WorkFlowNotificacao.class,new TriggerBuilder().cron("0 0/1 8-19 1/1 * ? *"), "Teste");
    }
    
    public static void criaEntityManager()
    {
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("default");
		EntityManager em =  factory.createEntityManager();
		ContextoPersistencia.setEntityManager(em);
    }
}
