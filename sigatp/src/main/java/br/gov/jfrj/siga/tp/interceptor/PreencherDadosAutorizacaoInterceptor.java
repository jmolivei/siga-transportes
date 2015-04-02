package br.gov.jfrj.siga.tp.interceptor;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.InstantiateInterceptor;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.gov.jfrj.siga.tp.auth.Autorizacoes;
import br.gov.jfrj.siga.tp.auth.AutorizacaoGI;

@RequestScoped
@Intercepts(after = { ContextInterceptor.class }, before = InstantiateInterceptor.class)
public class PreencherDadosAutorizacaoInterceptor implements Interceptor {

	private AutorizacaoGI dadosAutorizacao;

	public PreencherDadosAutorizacaoInterceptor(AutorizacaoGI dadosAutorizacao) {
		this.dadosAutorizacao = dadosAutorizacao;
	}

	@Override
	public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws InterceptionException {
		// TODO: Adicionar autorizacoes em lista para preencher automaticamente
		dadosAutorizacao.incluir(Autorizacoes.ADM_ADMINISTRAR);
		dadosAutorizacao.incluir(Autorizacoes.ADMFROTA_ADMINISTRAR_FROTA);
		dadosAutorizacao.incluir(Autorizacoes.ADMMISSAO_ADMINISTRAR_MISSAO);
		dadosAutorizacao.incluir(Autorizacoes.APR_APROVADOR);
		dadosAutorizacao.incluir(Autorizacoes.GAB_GABINETE);
		dadosAutorizacao.incluir(Autorizacoes.ADMGAB_ADMIN_GABINETE);
		dadosAutorizacao.incluir(Autorizacoes.AGN_AGENTE);
		dadosAutorizacao.incluirAdministrarMissaoComplexo();
		
		stack.next(method, resourceInstance);
	}

	@Override
	public boolean accepts(ResourceMethod method) {
		return Boolean.TRUE;
	}
}