package br.gov.jfrj.siga.tp.interceptor;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.gov.jfrj.siga.tp.auth.AutorizacaoGI;
import br.gov.jfrj.siga.tp.vraptor.AutorizacaoGIController.RoleAdmin;
import br.gov.jfrj.siga.tp.vraptor.AutorizacaoGIController.RoleAdminFrota;
import br.gov.jfrj.siga.tp.vraptor.AutorizacaoGIController.RoleAdminGabinete;
import br.gov.jfrj.siga.tp.vraptor.AutorizacaoGIController.RoleAdminMissao;
import br.gov.jfrj.siga.tp.vraptor.AutorizacaoGIController.RoleAdminMissaoComplexo;
import br.gov.jfrj.siga.tp.vraptor.AutorizacaoGIController.RoleAgente;
import br.gov.jfrj.siga.tp.vraptor.AutorizacaoGIController.RoleAprovador;
import br.gov.jfrj.siga.tp.vraptor.AutorizacaoGIController.RoleGabinete;

@RequestScoped
@Intercepts(after = { PreencherDadosAutorizacaoInterceptor.class }, before = ExecuteMethodInterceptor.class)
public class AutorizacaoAcessoInterceptor implements Interceptor {

	private AutorizacaoGI dadosAutorizacao;

	public AutorizacaoAcessoInterceptor(AutorizacaoGI dadosAutorizacao) {
		this.dadosAutorizacao = dadosAutorizacao;
	}
	
	@Override
	public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws InterceptionException {
		DadosParaValidacao dados = new DadosParaValidacao(method);

		this.validarAdmin(dados);
		this.validarAprovador(dados);
		this.validarAgente(dados);
		this.validarGabinete(dados);
		this.validarAdminGabinete(dados);
		this.validarAdminFrota(dados);
		this.validarAdminMissao(dados);
		this.validarAdminMissaoComplexo(dados);
		
		stack.next(method, resourceInstance);
	}

	private void validarAdminMissaoComplexo(DadosParaValidacao dados) {
		if (dados.isAdmMissaoComplexoAnnotation()) {
			if (!dadosAutorizacao.ehAdministradorMissaoPorComplexo() 
					&& !dados.isAdminAnnotation()
					&& !dados.isGabineteAnnotation() 
					&& !dados.isAprovadorAnnotation() 
					&& !dados.isAgenteAnnotation()
					&& !dados.isAdminGabineteAnnotation()
					&& !dados.isAdmFrotaAnnotation() 
					&& !dados.isAdmMissaoAnnotation()) {
				try {
					throw new Exception("admMissaoComplexoAnnotation.exception");
					// TODO Heidi Message Mudar!
					// throw new Exception(Messages.get("admMissaoComplexoAnnotation.exception"));
				} catch (Exception e) {
					// tratarExcecoes(e);
				}
			}
			if (!dadosAutorizacao.ehAdministradorMissaoPorComplexo())
				dados.setAdmMissaoComplexoAnnotation(false);
		}
	}

	private void validarAdminMissao(DadosParaValidacao dados) {
		if (dados.isAdmMissaoAnnotation()) {
			if (!dadosAutorizacao.ehAdministradorMissao()
					&& !dados.isAdminAnnotation()
					&& !dados.isGabineteAnnotation() 
					&& !dados.isAprovadorAnnotation() 
					&& !dados.isAgenteAnnotation()
					&& !dados.isAdminGabineteAnnotation() 
					&& !dados.isAdmFrotaAnnotation()
					&& !dados.isAdmMissaoComplexoAnnotation()) {
				try {
					throw new Exception("admMissaoAnnotation.exception");
					// TODO Heidi Message Mudar!
					// throw new Exception(Messages.get("admMissaoAnnotation.exception"));
				} catch (Exception e) {
					// tratarExcecoes(e);
				}
			}

			if (!dadosAutorizacao.ehAdministradorMissao())
				dados.setAdmMissaoAnnotation(false);
		}
	}

	private void validarAdminFrota(DadosParaValidacao dados) {
		if (dados.isAdmFrotaAnnotation()) {
			if (!dadosAutorizacao.ehAdministradorFrota() 
					&& !dados.isAdminAnnotation()
					&& !dados.isGabineteAnnotation() 
					&& !dados.isAprovadorAnnotation() 
					&& !dados.isAgenteAnnotation()
					&& !dados.isAdminGabineteAnnotation() 
					&& !dados.isAdmMissaoAnnotation()
					&& !dados.isAdmMissaoComplexoAnnotation()) {
				try {
					throw new Exception("admFrotaAnnotation.exception");
					// TODO Heidi Message Mudar!
					// throw new Exception(Messages.get("admFrotaAnnotation.exception"));
				} catch (Exception e) {
					// tratarExcecoes(e);
				}
			}

			if (!dadosAutorizacao.ehAdministradorFrota())
				dados.setAdmFrotaAnnotation(false);
		}
	}

	private void validarAdminGabinete(DadosParaValidacao dados) {
		if (dados.isAdminGabineteAnnotation()) {
			if (!dadosAutorizacao.ehAdminGabinete() 
					&& !dados.isAdminAnnotation()
					&& !dados.isGabineteAnnotation() 
					&& !dados.isAprovadorAnnotation() 
					&& !dados.isAgenteAnnotation()
					&& !dados.isAdmFrotaAnnotation() 
					&& !dados.isAdmMissaoAnnotation()
					&& !dados.isAdmMissaoComplexoAnnotation()) {
				try {
					throw new Exception("adminGabineteAnnotation.exception");
					// TODO Heidi Message Mudar!
					// throw new Exception(Messages.get("adminGabineteAnnotation.exception"));
				} catch (Exception e) {
					// tratarExcecoes(e);
				}
			}

			if (!dadosAutorizacao.ehAdminGabinete())
				dados.setAdminGabineteAnnotation(false);
		}
	}

	private void validarGabinete(DadosParaValidacao dados) {
		if (dados.isGabineteAnnotation()) {
			if (!dadosAutorizacao.ehGabinete() 
					&& !dados.isAdminAnnotation()
					&& !dados.isAprovadorAnnotation() 
					&& !dados.isAdminGabineteAnnotation() 
					&& !dados.isAgenteAnnotation()
					&& !dados.isAdmFrotaAnnotation() 
					&& !dados.isAdmMissaoAnnotation()
					&& !dados.isAdmMissaoComplexoAnnotation()) {
				try {
					throw new Exception("gabineteAnnotation.exception");
					// TODO Heidi Message Mudar!
					// throw new Exception(Messages.get("gabineteAnnotation.exception"));
				} catch (Exception e) {
					// tratarExcecoes(e);
				}
			}

			if (!dadosAutorizacao.ehGabinete())
				dados.setGabineteAnnotation(false);
		}
	}

	private void validarAgente(DadosParaValidacao dados) {
		if (dados.isAgenteAnnotation()) {
			if (!dadosAutorizacao.ehAgente() 
					&& !dados.isAdminAnnotation()
					&& !dados.isGabineteAnnotation() 
					&& !dados.isAdminGabineteAnnotation() 
					&& !dados.isAprovadorAnnotation() 
					&& !dados.isAdmFrotaAnnotation() 
					&& !dados.isAdmMissaoAnnotation()
					&& !dados.isAdmMissaoComplexoAnnotation()) {
				try {
					throw new Exception("agenteAnnotation.exception");
					// TODO Heidi Message Mudar!
					// throw new Exception(Messages.get("agenteAnnotation.exception"));
				} catch (Exception e) {
					// tratarExcecoes(e);
				}
			}

			if (!dadosAutorizacao.ehAgente())
				dados.setAgenteAnnotation(false);
		}
	}

	private void validarAprovador(DadosParaValidacao dados) {
		if (dados.isAprovadorAnnotation()) {
			if (!dadosAutorizacao.ehAprovador() 
					&& !dados.isAdminAnnotation() 
					&& !dados.isGabineteAnnotation() 
					&& !dados.isAdminGabineteAnnotation() 
					&& !dados.isAgenteAnnotation()
					&& !dados.isAdmFrotaAnnotation() 
					&& !dados.isAdmMissaoAnnotation()
					&& !dados.isAdmMissaoComplexoAnnotation()) {
				try {
					throw new Exception("aprovadorAnnotation.exception");
					// TODO Heidi Message Mudar!
					// throw new Exception(Messages.get("aprovadorAnnotation.exception"));
				} catch (Exception e) {
					// tratarExcecoes(e);
				}
			}

			if (!dadosAutorizacao.ehAprovador())
				dados.setAprovadorAnnotation(false);
		}
	}

	private void validarAdmin(DadosParaValidacao dados) {
		if (dados.isAdminAnnotation()) {
			if (!dadosAutorizacao.ehAdministrador() 
					&& !dados.isAprovadorAnnotation() 
					&& !dados.isGabineteAnnotation() 
					&& !dados.isAdminGabineteAnnotation() 
					&& !dados.isAgenteAnnotation()
					&& !dados.isAdmFrotaAnnotation() 
					&& !dados.isAdmMissaoAnnotation()
					&& !dados.isAdmMissaoComplexoAnnotation()) {
				try {
					throw new Exception("adminAnnotation.exception");
					// TODO Heidi Message Mudar!
					// throw new Exception(Messages.get("adminAnnotation.exception"));
				} catch (Exception e) {
					// tratarExcecoes(e);
				}
			}

			if (!dadosAutorizacao.ehAdministrador())
			dados.setAdminAnnotation(Boolean.FALSE);
		}
	}

	@Override
	public boolean accepts(ResourceMethod method) {
		return Boolean.TRUE;
	}
	
	class DadosParaValidacao {
		boolean adminAnnotation;
		boolean aprovadorAnnotation;
		boolean gabineteAnnotation;
		boolean adminGabineteAnnotation;
		boolean agenteAnnotation;
		boolean admFrotaAnnotation;
		boolean admMissaoAnnotation;
		boolean admMissaoComplexoAnnotation;

		public DadosParaValidacao(ResourceMethod method) {
			this.adminAnnotation = method.containsAnnotation(RoleAdmin.class);
			this.aprovadorAnnotation = method.containsAnnotation(RoleAprovador.class);
			this.gabineteAnnotation = method.containsAnnotation(RoleGabinete.class);
			this.adminGabineteAnnotation = method.containsAnnotation(RoleAdminGabinete.class);
			this.agenteAnnotation = method.containsAnnotation(RoleAgente.class);
			this.admFrotaAnnotation = method.containsAnnotation(RoleAdminFrota.class);
			this.admMissaoAnnotation = method.containsAnnotation(RoleAdminMissao.class);
			this.admMissaoComplexoAnnotation = method.containsAnnotation(RoleAdminMissaoComplexo.class);
		}

		public boolean isAdminAnnotation() {
			return adminAnnotation;
		}

		public boolean isAprovadorAnnotation() {
			return aprovadorAnnotation;
		}

		public boolean isGabineteAnnotation() {
			return gabineteAnnotation;
		}

		public boolean isAdminGabineteAnnotation() {
			return adminGabineteAnnotation;
		}

		public boolean isAgenteAnnotation() {
			return agenteAnnotation;
		}

		public boolean isAdmFrotaAnnotation() {
			return admFrotaAnnotation;
		}

		public boolean isAdmMissaoAnnotation() {
			return admMissaoAnnotation;
		}

		public boolean isAdmMissaoComplexoAnnotation() {
			return admMissaoComplexoAnnotation;
		}

		public void setAdminAnnotation(boolean adminAnnotation) {
			this.adminAnnotation = adminAnnotation;
		}

		public void setAprovadorAnnotation(boolean aprovadorAnnotation) {
			this.aprovadorAnnotation = aprovadorAnnotation;
		}

		public void setGabineteAnnotation(boolean gabineteAnnotation) {
			this.gabineteAnnotation = gabineteAnnotation;
		}

		public void setAdminGabineteAnnotation(boolean adminGabineteAnnotation) {
			this.adminGabineteAnnotation = adminGabineteAnnotation;
		}

		public void setAgenteAnnotation(boolean agenteAnnotation) {
			this.agenteAnnotation = agenteAnnotation;
		}

		public void setAdmFrotaAnnotation(boolean admFrotaAnnotation) {
			this.admFrotaAnnotation = admFrotaAnnotation;
		}

		public void setAdmMissaoAnnotation(boolean admMissaoAnnotation) {
			this.admMissaoAnnotation = admMissaoAnnotation;
		}

		public void setAdmMissaoComplexoAnnotation(boolean admMissaoComplexoAnnotation) {
			this.admMissaoComplexoAnnotation = admMissaoComplexoAnnotation;
		}
	}
}