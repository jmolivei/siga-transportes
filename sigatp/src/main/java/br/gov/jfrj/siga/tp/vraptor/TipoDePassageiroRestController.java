package br.gov.jfrj.siga.tp.vraptor;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.view.Results;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.tp.model.TipoDePassageiro;
import br.gov.jfrj.siga.tp.model.TpDao;
import br.gov.jfrj.siga.vraptor.SigaObjects;

@Resource
@Path("/app/tipoDePassageiroRest")
public class TipoDePassageiroRestController extends TpController {

    public TipoDePassageiroRestController(HttpServletRequest request,
            Result result, CpDao dao, Validator validator, SigaObjects so,
            EntityManager em) {
        super(request, result, TpDao.getInstance(), validator, so, em);
    }

    @Path("/ver")
    public void ver() throws Exception {
        ObjectMapper oM = new ObjectMapper();

        ObjectWriter oW = oM.writer().withDefaultPrettyPrinter();
        String json = oW.writeValueAsString(TipoDePassageiro.valuesString());

        result.use(Results.http()).body(json);
    }
    
    public void incluir() throws Exception {
        result.use(Results.http()).body("Funcao nao implementada");
    }
    
    public void alterar(Long id) throws Exception {
        result.use(Results.http()).body("Funcao nao implementada");
    }
    
    public void excluir(Long id) throws Exception {
        result.use(Results.http()).body("Funcao nao implementada");
    }
}
