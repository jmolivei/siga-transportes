package br.gov.jfrj.siga.tp.vraptor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.Localization;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdmin;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissao;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissaoComplexo;
import br.gov.jfrj.siga.tp.model.EscalaDeTrabalho;
import br.gov.jfrj.siga.tp.model.EstadoMissao;
import br.gov.jfrj.siga.tp.model.Missao;
import br.gov.jfrj.siga.tp.model.RequisicaoTransporte;
import br.gov.jfrj.siga.tp.model.TpDao;
import br.gov.jfrj.siga.tp.util.PerguntaSimNao;
import br.gov.jfrj.siga.vraptor.SigaObjects;

@Resource
public class MissaoController extends TpController {

	public MissaoController(HttpServletRequest request, Result result, CpDao dao, Localization localization, Validator validator, SigaObjects so, EntityManager em){
		super(request, result, TpDao.getInstance(), validator, so, em);
	}

	@SuppressWarnings("unchecked")
	public static List<Missao> buscarPorCondutoreseEscala(EscalaDeTrabalho escala) {
		SimpleDateFormat formatar = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String dataFormatadaFimOracle;
		if (escala.getDataVigenciaFim() == null) {
			@SuppressWarnings("unused")
			Calendar dataFim = Calendar.getInstance();
			dataFormatadaFimOracle = "to_date('31/12/9999 23:59', 'DD/MM/YYYY HH24:mi')";
		} else {
			String dataFim = formatar.format(escala.getDataVigenciaFim().getTime());
			dataFormatadaFimOracle = "to_date('" + dataFim + "', 'DD/MM/YYYY HH24:mi')";
		}

		String dataInicio = formatar.format(escala.getDataVigenciaInicio().getTime());
		String dataFormatadaInicioOracle = "to_date('" + dataInicio + "', 'DD/MM/YYYY HH24:mi')";
		List<Missao> missoes = null;

		String qrl = 	"SELECT p FROM Missao p" +
		" WHERE  p.condutor.id = " + escala.getCondutor().getId() +
		" AND    p.estadoMissao NOT IN ('" + EstadoMissao.CANCELADA + "','" + EstadoMissao.FINALIZADA + "')" +
		" AND   (p.dataHoraSaida >= " + dataFormatadaInicioOracle +
		" AND    p.dataHoraSaida <= " + dataFormatadaFimOracle + "))";

		Query qry = Missao.AR.em().createQuery(qrl);
		try {
			missoes = (List<Missao>) qry.getResultList();
		} catch (NoResultException ex) {
			missoes = null;
		}

		return missoes;
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public void incluirComRequisicoes(Long[] req) throws Exception {
		if (req == null) {
			incluir();
		}
		Missao missao = new Missao();
		missao.inicioRapido = PerguntaSimNao.NAO;
		missao.requisicoesTransporte = new ArrayList<RequisicaoTransporte>();
		for (int cont = 0; cont < req.length; cont++) {
			missao.requisicoesTransporte.add((RequisicaoTransporte) RequisicaoTransporte.AR.findById(req[cont]));
		}

		removerRequisicoesDoRenderArgs(missao.requisicoesTransporte);

		//renderTemplate("@incluir", missao);

		result.include("missao", missao);

		result.forwardTo(this).incluir();
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public void incluir() {
		Missao missao = new Missao();
		missao.inicioRapido = PerguntaSimNao.NAO;
		MenuMontador.instance(result).recuperarMenuMissao(missao.getId(), missao.estadoMissao);

		result.include("missao", missao);
	}

	private void removerRequisicoesDoRenderArgs(List<RequisicaoTransporte> requisicoesTransporte) {
		@SuppressWarnings("unchecked")
		List<RequisicaoTransporte> requisicoes = (List<RequisicaoTransporte>) getRequest().getAttribute("requisicoesTransporte");
		if (requisicoes != null) {
			requisicoes.removeAll(requisicoesTransporte);
		}

		result.include("requisicoesTransporte", requisicoes);
	}
}
