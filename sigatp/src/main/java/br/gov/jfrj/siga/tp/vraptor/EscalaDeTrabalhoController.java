package br.gov.jfrj.siga.tp.vraptor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.validator.I18nMessage;
import br.com.caelum.vraptor.view.Results;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdmin;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissao;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissaoComplexo;
import br.gov.jfrj.siga.tp.model.Condutor;
import br.gov.jfrj.siga.tp.model.DiaDaSemana;
import br.gov.jfrj.siga.tp.model.DiaDeTrabalho;
import br.gov.jfrj.siga.tp.model.EscalaDeTrabalho;
import br.gov.jfrj.siga.tp.model.ItemMenu;
import br.gov.jfrj.siga.tp.model.Missao;
import br.gov.jfrj.siga.tp.model.TpDao;
import br.gov.jfrj.siga.vraptor.SigaObjects;

@Resource
@Path("/app/escalaDeTrabalho")
public class EscalaDeTrabalhoController extends TpController {
	
	private static final String MODO = "modo";
	private static final String BTN_EDITAR = "views.botoes.editar";
	private static final String BTN_INCLUIR = "views.botoes.incluir";

	public EscalaDeTrabalhoController(HttpServletRequest request, Result result, CpDao dao, Localization localization, Validator validator, SigaObjects so, EntityManager em) throws Exception {
		super(request, result, TpDao.getInstance(), validator, so, em);
	}

	@Path("/listar")
	public void listar() {
    	List<EscalaDeTrabalho> escalas = EscalaDeTrabalho.buscarTodasVigentes();
        
        result.include("escalas", escalas);
    }
	
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/incluir/{idCondutor}")
    public void incluir(Long idCondutor) throws Exception {
		MenuMontador.instance(result).recuperarMenuCondutores(idCondutor, ItemMenu.ESCALASDETRABALHO);
    	Condutor condutor = Condutor.AR.findById(idCondutor);
    	
    	EscalaDeTrabalho escala = new EscalaDeTrabalho();
    	escala.setCondutor(condutor);
        
    	DiaDaSemana diaSemana = DiaDaSemana.SEGUNDA;
    	DiaDeTrabalho diaTrabalho = new DiaDeTrabalho();
    	escala.getDiasDeTrabalho().add(diaTrabalho);
    	
    	result.include(MODO, BTN_INCLUIR);
    	result.include("escala", escala);
    	result.include("diaSemana", diaSemana);
    	
    	result.use(Results.page()).of(EscalaDeTrabalhoController.class).editar(null);
	}
	
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/editar/{id}")
    public void editar(final Long id) throws Exception {
		EscalaDeTrabalho escala = EscalaDeTrabalho.AR.findById(id);
    	MenuMontador.instance(result).recuperarMenuCondutores(escala.getCondutor().getId(), ItemMenu.ESCALASDETRABALHO);
    	DiaDaSemana diaSemana = DiaDaSemana.SEGUNDA;
    	
    	result.include(MODO, BTN_EDITAR);
    	result.include("escala", escala);
    	result.include("diaSemana", diaSemana);
    }
	
	@Path("/listarPorCondutor/{id}")
	public void listarPorCondutor(Long id) throws Exception {
    	MenuMontador.instance(result).recuperarMenuCondutores(id, ItemMenu.ESCALASDETRABALHO);
        Condutor condutor = Condutor.AR.findById(id);
        
        List<EscalaDeTrabalho> escalas = EscalaDeTrabalho.buscarTodosPorCondutor(condutor);
        
        EscalaDeTrabalho escala = new EscalaDeTrabalho();
        escala.setCondutor(condutor);
    	DiaDaSemana diaSemana = DiaDaSemana.SEGUNDA;
    	escala.setDataVigenciaInicio(Calendar.getInstance());
    	DiaDeTrabalho diaTrabalho = new DiaDeTrabalho();
    	escala.getDiasDeTrabalho().add(diaTrabalho);
       
        if (escalas.size() > 0 && (escalas.get(0).getDataVigenciaFim() == null || escalas.get(0).getDataVigenciaFim().after(Calendar.getInstance()))) {
        	 escala = escalas.get(0);
        } else if (escalas.size() > 0 && ehMesmoDia(escalas.get(0).getDataVigenciaFim(),Calendar.getInstance())) {
       		escalas.add(0,escala);
        }
        
        result.include("escalas", escalas);
        result.include("condutor", condutor);
        result.include("escala", escala);
        result.include("diaSemana", diaSemana);
     }
	
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/finalizar")
    public void finalizar(@Valid EscalaDeTrabalho escala) throws Exception { 
		escala.setDataVigenciaFim(Calendar.getInstance());
    	escala.getDataVigenciaFim().set(Calendar.HOUR_OF_DAY, 23);
    	escala.getDataVigenciaFim().set(Calendar.MINUTE, 59);
    	escala.getDataVigenciaFim().set(Calendar.SECOND, 59);
		escala.save();
		
		result.redirectTo(this).listarPorCondutor(escala.getCondutor().getId());
	}
    
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/salvar")
	public void salvar(EscalaDeTrabalho escala, EscalaDeTrabalho novaEscala) throws Exception {
        if(!validator.hasErrors()) {
        	String diaDaSemana = novaEscala.getDiasDeTrabalho().get(0).getDiaEntrada().toString();
        	
        	if (diaDaSemana == null) {
        		validator.add(new I18nMessage("diasDeTrabalho", "escalasDeTrabalho.diaDaSemana.validation"));
        		novaEscala.setDiasDeTrabalho(null);
        	}	
        }
        
        EscalaDeTrabalho escalaAntiga = new EscalaDeTrabalho();
        escalaAntiga.setDiasDeTrabalho(new ArrayList<DiaDeTrabalho>());
        escalaAntiga.getDiasDeTrabalho().addAll(escala.getDiasDeTrabalho());
        escalaAntiga.setId(escala.getId());

        if(!validator.hasErrors()) {
	        escala.setDiasDeTrabalho(new ArrayList<DiaDeTrabalho>());
	        escala.getDiasDeTrabalho().addAll(novaEscala.getDiasDeTrabalho());
        }
        
        if(!validator.hasErrors()) {
        	validarEscala(escala.getDiasDeTrabalho());
        }
        
        if(!validator.hasErrors()) {
        	validarMissoesParaNovaEscala(escala);
        }
        
        if(validator.hasErrors()) {
        	MenuMontador.instance(result).recuperarMenuCondutores(escala.getCondutor().getId(), ItemMenu.ESCALASDETRABALHO);
        	
            Condutor condutor = escala.getCondutor();
            
        	List<EscalaDeTrabalho> escalas = EscalaDeTrabalho.buscarTodosPorCondutor(condutor);
        	
        	for (EscalaDeTrabalho escalaDeTrabalho : escalas) {
        		if (escala.getId() == escalaDeTrabalho.getId()) {
        			escalaDeTrabalho.setDiasDeTrabalho(escalaAntiga.getDiasDeTrabalho());
        		}
			}
        	
        	DiaDaSemana diaSemana = DiaDaSemana.SEGUNDA;
        	
        	result.include("escalas", escalas);
            result.include("condutor", condutor);
            result.include("escala", escala);
            result.include("diaSemana", diaSemana);
            
            validator.onErrorUse(Results.logic()).forwardTo(EscalaDeTrabalhoController.class).listarPorCondutor(condutor.getId());
        }
        
        escala.setDataVigenciaInicio(Calendar.getInstance());
        if (ehMesmoDia(escala.getDataVigenciaInicio(), Calendar.getInstance())) {
        	if (escala.getId() > 0) {
        		Object[] escalas = new Object[1];
        		escalas[0] = escala;
        		DiaDeTrabalho.AR.delete("escalaDeTrabalho", escalas);
        	} 
        	
        	escala.save();
    		
    		for (DiaDeTrabalho diaDeTrabalho : escala.getDiasDeTrabalho()) {
                DiaDeTrabalho diaDeTrabalhoNovo = new DiaDeTrabalho();
                diaDeTrabalhoNovo.setDiaEntrada(diaDeTrabalho.getDiaEntrada());
                diaDeTrabalhoNovo.setDiaSaida(diaDeTrabalho.getDiaSaida());
                diaDeTrabalhoNovo.setHoraEntrada(diaDeTrabalho.getHoraEntrada());
                diaDeTrabalhoNovo.setHoraSaida(diaDeTrabalho.getHoraSaida());
                diaDeTrabalhoNovo.setEscalaDeTrabalho(escala);
                
                diaDeTrabalhoNovo.save();
    		}
    		
        } else {
        	escala.setDataVigenciaFim(Calendar.getInstance());
        	escala.getDataVigenciaFim().add(Calendar.DATE,-1);
        	escala.getDataVigenciaFim().set(Calendar.HOUR_OF_DAY, 23);
        	escala.getDataVigenciaFim().set(Calendar.MINUTE, 59);
        	escala.getDataVigenciaFim().set(Calendar.SECOND, 59);
           	escala.save();
            EscalaDeTrabalho.AR.em().detach(escala);

        	novaEscala.setCondutor(escala.getCondutor());
        	novaEscala.setDataVigenciaInicio(Calendar.getInstance());
        	novaEscala.getDataVigenciaInicio().set(Calendar.HOUR_OF_DAY, 0);
        	novaEscala.getDataVigenciaInicio().set(Calendar.MINUTE, 0);
        	novaEscala.getDataVigenciaInicio().set(Calendar.SECOND, 0);
        	novaEscala.setDiasDeTrabalho(escala.getDiasDeTrabalho());
        	novaEscala.save();
        	
    		for (DiaDeTrabalho diaDeTrabalho : novaEscala.getDiasDeTrabalho()) {
                DiaDeTrabalho diaDeTrabalhoNovo = new DiaDeTrabalho();
                diaDeTrabalhoNovo.setDiaEntrada(diaDeTrabalho.getDiaEntrada());
                diaDeTrabalhoNovo.setDiaSaida(diaDeTrabalho.getDiaSaida());
                diaDeTrabalhoNovo.setHoraEntrada(diaDeTrabalho.getHoraEntrada());
                diaDeTrabalhoNovo.setHoraSaida(diaDeTrabalho.getHoraSaida());
                diaDeTrabalhoNovo.setEscalaDeTrabalho(novaEscala);
                
                diaDeTrabalhoNovo.save();
    		}
        }
        
        result.redirectTo(CondutorController.class).listar();
    }
	
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/excluir{id}")
	public void excluir(final Long id) throws Exception {
    	EscalaDeTrabalho escala = EscalaDeTrabalho.AR.findById(id);
    	Long idCondutor = escala.getCondutor().getId();
    	escala.delete();
    	
    	result.redirectTo(this).listarPorCondutor(idCondutor);
    }
	
	private boolean  ehMesmoDia(Calendar cal1, Calendar cal2) {
	    if (cal1 == null || cal2 == null)
	        return false;
	    return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
	            && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) 
	            && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
	}
	
	private boolean validarEscala(List<DiaDeTrabalho> diasDeTrabalho) {
		for (DiaDeTrabalho diaDeTrabalho : diasDeTrabalho) {
			if(null == diaDeTrabalho.getHoraEntrada()) {
				validator.add(new I18nMessage("diasDeTrabalho", "escalasDeTrabalho.horaEntrada.vazia.validation"));
				return false;
			}
			
			if(null == diaDeTrabalho.getHoraSaida()) {
				validator.add(new I18nMessage("diasDeTrabalho", "escalasDeTrabalho.horaSaida.vazia.validation"));
				return false;
			}
		}
		
		Collections.sort(diasDeTrabalho);
		ArrayList<String> periodosDeTrabalho = new ArrayList<String>();
		for (DiaDeTrabalho diaDeTrabalho : diasDeTrabalho) {

			if (diaDeTrabalho.getDiaEntrada().getOrdem() > diaDeTrabalho.getDiaSaida().getOrdem()) {
				validator.add(new I18nMessage("diasDeTrabalho", "escalasDeTrabalho.diaEntrada.validation"));
				return false;
			}
			
			if (diaDeTrabalho.getHoraEntrada().after(diaDeTrabalho.getHoraSaida()) &&
				diaDeTrabalho.getDiaEntrada().getOrdem() == diaDeTrabalho.getDiaSaida().getOrdem() ) {
				validator.add(new I18nMessage("diasDeTrabalho", "escalasDeTrabalho.horaEntrada.validation"));
				return false;
			}

			periodosDeTrabalho.add(diaDeTrabalho.getDiaEntrada().getOrdem() + diaDeTrabalho.getHoraEntradaFormatada("HHmm"));
			periodosDeTrabalho.add(diaDeTrabalho.getDiaSaida().getOrdem() + diaDeTrabalho.getHoraSaidaFormatada("HHmm"));
		}
		
		for (int i = 0; i < periodosDeTrabalho.size(); i++) {
			if (i + 1 != periodosDeTrabalho.size())  {
				int intervalo1 = Integer.parseInt(periodosDeTrabalho.get(i));
				int intervalo2 = Integer.parseInt(periodosDeTrabalho.get(i+1));
				if (intervalo1 > intervalo2) {
					validator.add(new I18nMessage("diasDeTrabalho", "escalasDeTrabalho.periodosDeTrabalho.validation"));
					return false;
				}
			}
		}
		
		return true;
	}

	private boolean validarMissoesParaNovaEscala(EscalaDeTrabalho escala) throws Exception {
		List<Missao> missoes = MissaoController.buscarPorCondutoreseEscala(escala);
		SimpleDateFormat formatar = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		boolean valido = true;
		String listaMissoes = "";
		String delimitador="";

		if (missoes != null && missoes.size() > 0) {
			for (Missao missao : missoes) {
				String dataMissao = formatar.format(missao.dataHoraSaida.getTime());
				String dataFormatadaOracle = "to_date('" + dataMissao + "', 'DD/MM/YYYY HH24:mi')";
				if (! missao.condutor.estaEscalado(dataMissao) &&
					! missao.condutor.estaDePlantao(dataFormatadaOracle)	) {
					listaMissoes += delimitador;
					listaMissoes += missao.getSequence();
					delimitador=",";
					valido = false;	
				}
			} 
			if (! valido) {
				validator.add(new I18nMessage("LinkErroEscalaDeTrabalho", listaMissoes));
			}
		}
		return valido;
	}
}
