package br.gov.jfrj.siga.tp.interceptor;

import javax.persistence.EntityManager;

import org.hibernate.Session;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.util.jpa.extra.ParameterLoaderInterceptor;
import br.gov.jfrj.siga.model.ContextoPersistencia;
import br.gov.jfrj.siga.tp.model.TpDao;
import br.gov.jfrj.siga.vraptor.ParameterOptionalLoaderInterceptor;

/**
 * Interceptor que inicia a instancia do DAO a ser utilizado pelo sistema.
 * 
 * @author db1.
 *
 */
@RequestScoped
@Intercepts(before = { ParameterLoaderInterceptor.class, ParameterOptionalLoaderInterceptor.class })
public class ContextInterceptor implements Interceptor {

	public ContextInterceptor(EntityManager em, Result result) {
		ContextoPersistencia.setEntityManager(em);
		TpDao.freeInstance();
		TpDao.getInstance((Session) em.getDelegate(), ((Session) em.getDelegate()).getSessionFactory().openStatelessSession());
	}

	@Override
	public boolean accepts(ResourceMethod method) {
		return Boolean.TRUE;
	}

	@Override
	public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws InterceptionException {
		try {
			stack.next(method, resourceInstance);
		} catch (Exception ex) {
			throw new InterceptionException(ex);
		}
	}
}