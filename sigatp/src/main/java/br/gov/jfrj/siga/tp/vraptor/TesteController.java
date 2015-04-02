package br.gov.jfrj.siga.tp.vraptor;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.view.Results;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.tp.auth.AutorizacaoGI;
import br.gov.jfrj.siga.vraptor.SigaObjects;
import controllers.AutorizacaoGIAntigo.RoleAdmin;
import controllers.AutorizacaoGIAntigo.RoleAdminFrota;

@Resource
public class TesteController extends TpController {

	public TesteController(HttpServletRequest request, Result result, CpDao dao, Localization localization, SigaObjects so, AutorizacaoGI dadosAutorizacao, EntityManager em) throws Exception {
		super(request, result, dao, localization, so, dadosAutorizacao, em);
	}

	@RoleAdmin
	@RoleAdminFrota
	@Get("/app/teste/")
	public void testar() {
		result
			.use(Results.http())
			.body(localization.getMessage("veiculo.anoFabricacao.maxSize"));
	}
}