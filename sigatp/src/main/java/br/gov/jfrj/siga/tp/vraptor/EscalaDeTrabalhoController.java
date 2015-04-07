package br.gov.jfrj.siga.tp.vraptor;

import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.Localization;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.tp.auth.AutorizacaoGI;
import br.gov.jfrj.siga.tp.model.EscalaDeTrabalho;
import br.gov.jfrj.siga.vraptor.SigaObjects;

//@With(AutorizacaoGIAntigo.class)
@Resource
public class EscalaDeTrabalhoController extends TpController {

	public EscalaDeTrabalhoController(HttpServletRequest request, Result result, CpDao dao, Localization localization, Validator validator, SigaObjects so, AutorizacaoGI dadosAutorizacao, EntityManager em) throws Exception {
		super(request, result, dao, localization, validator, so, dadosAutorizacao, em);
	}

	@Path("/app/escalaDeTrabalho/listar")
	public void listar() {
    	List<EscalaDeTrabalho> escalas = EscalaDeTrabalho.buscarTodasVigentes();
        
        result.include("escalas", escalas);
    }
	
	@Path("/app/escalaDeTrabalho/editar/{id}")
	public void editar(final Long id) {
		
	}
}