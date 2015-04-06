package br.gov.jfrj.siga.tp.vraptor;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.Localization;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.tp.auth.AutorizacaoGI;
import br.gov.jfrj.siga.vraptor.SigaObjects;

public class EscalaDeTrabalhoController extends TpController {

	public EscalaDeTrabalhoController(HttpServletRequest request, Result result, Localization localization, SigaObjects so, AutorizacaoGI dadosAutorizacao, EntityManager em) throws Exception {
		super(request, result, CpDao.getInstance(), localization, so, dadosAutorizacao, em);
	}

	
}
