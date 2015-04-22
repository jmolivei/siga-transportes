package br.gov.jfrj.siga.tp.interceptor;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.gov.jfrj.siga.tp.auth.annotation.LogMotivo;

/**
 * Interceptor que processa a anotacao {@link LogMotivo}.
 * 
 * Funciona junto a tag MotivoLog nos formulários Abastecimentos/listar e
 * ControlesGabinete/listar, metodo de exclusão.
 * Necessário incluir uma tag <form> com id="formulario".
 * Incluir também a tag #{motivoLog /} antes de </form>
 * 
 *  @author db1
 *
 */
@RequestScoped
@Intercepts(after = { AutorizacaoAcessoInterceptor.class }, before = ExecuteMethodInterceptor.class)
public class MotivoLogInterceptor implements Interceptor {

	private Result result;
	private HttpServletRequest request;

	public MotivoLogInterceptor(Result result, HttpServletRequest request) {
		this.result = result;
		this.request = request;
	}

	@Override
	public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws InterceptionException {
		LogMotivo motivoLogAnnotation = method.getMethod().getAnnotation(LogMotivo.class);

		if (motivoLogAnnotation != null) {
			String motivoLog = request.getParameter("motivoLog");
			if (motivoLog == null) {
				throw new InterceptionException("motivoLogAnnotation.exception");
			}
			result.include("motivoLog", motivoLog);
		}
		stack.next(method, resourceInstance);
	}

	@Override
	public boolean accepts(ResourceMethod method) {
		return method.containsAnnotation(LogMotivo.class);
	}
}