package br.gov.jfrj.siga.tp.interceptor;

import java.lang.annotation.Annotation;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;

@RequestScoped
@Intercepts(after = { MotivoLogInterceptor.class }, before = ExecuteMethodInterceptor.class)
public class BeanValidationInterceptor implements Interceptor {

	private MethodInfo info;
	private Validator validator;

	public BeanValidationInterceptor(Validator validator, MethodInfo info) {
		this.info = info;
		this.validator = validator;
	}

	@Override
	public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws InterceptionException {
		Object[] parametros = info.getParameters();

		for (int indiceParametro = 0; indiceParametro < parametros.length; indiceParametro++) {
			if (contemAnotacaoValid(method, indiceParametro)) {
				validator.validate(parametros[indiceParametro]);
			}
		}
		stack.next(method, resourceInstance);
	}

	private Boolean contemAnotacaoValid(ResourceMethod method, int i) {
		Annotation[][] parameterAnnotations = method.getMethod().getParameterAnnotations();

		for (Annotation annotation : parameterAnnotations[i]) {
			// TODO: alterar forma da comparacao
			if ("@javax.validation.Valid()".equals(annotation.toString())) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	@Override
	public boolean accepts(ResourceMethod method) {
		return metodoPossuiParametros(method);
	}

	private boolean metodoPossuiParametros(ResourceMethod method) {
		return method.getMethod().getParameterTypes().length > 0;
	}
}
