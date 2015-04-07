package br.gov.jfrj.siga.tp.interceptor;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.InstantiateInterceptor;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.gov.jfrj.siga.tp.auth.AutorizacaoGI;
import br.gov.jfrj.siga.tp.auth.Autorizacoes;

/**
 * Inteceptor responsavel por preencher as permissoes disponiveis para o usuario.
 * 
 * @author db1
 *
 */
@RequestScoped
@Intercepts(after = { ContextInterceptor.class }, before = InstantiateInterceptor.class)
public class PreencherDadosAutorizacaoInterceptor implements Interceptor {

	private AutorizacaoGI autorizacaoGI;

	public PreencherDadosAutorizacaoInterceptor(AutorizacaoGI autorizacaoGI) {
		this.autorizacaoGI = autorizacaoGI;
	}

	@Override
	public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws InterceptionException {
		// TODO: Adicionar autorizacoes em lista para preencher automaticamente
		autorizacaoGI.incluir(Autorizacoes.ADM_ADMINISTRAR);
		autorizacaoGI.incluir(Autorizacoes.ADMFROTA_ADMINISTRAR_FROTA);
		autorizacaoGI.incluir(Autorizacoes.ADMMISSAO_ADMINISTRAR_MISSAO);
		autorizacaoGI.incluir(Autorizacoes.APR_APROVADOR);
		autorizacaoGI.incluir(Autorizacoes.GAB_GABINETE);
		autorizacaoGI.incluir(Autorizacoes.ADMGAB_ADMIN_GABINETE);
		autorizacaoGI.incluir(Autorizacoes.AGN_AGENTE);
		autorizacaoGI.incluirAdministrarMissaoComplexo();

		stack.next(method, resourceInstance);
	}

	@Override
	public boolean accepts(ResourceMethod method) {
		return Boolean.TRUE;
	}
}