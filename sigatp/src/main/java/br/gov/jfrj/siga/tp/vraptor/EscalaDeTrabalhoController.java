package br.gov.jfrj.siga.tp.vraptor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
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
	private static final String LABEL_EDITAR = "views.label.editar";
	private static final String LABEL_INCLUIR = "views.label.incluir";

	public EscalaDeTrabalhoController(HttpServletRequest request, Result result, CpDao dao, Validator validator, SigaObjects so, EntityManager em) {
		super(request, result, TpDao.getInstance(), validator, so, em);
	}

	@Path("/listar")
	public void listar() {
    	result.include("escalas", EscalaDeTrabalho.buscarTodasVigentes());
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
    	
    	result.include(MODO, LABEL_INCLUIR);
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
    	
    	result.include(MODO, LABEL_EDITAR);
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
       
        if (!escalas.isEmpty() && (escalas.get(0).getDataVigenciaFim() == null || escalas.get(0).getDataVigenciaFim().after(Calendar.getInstance()))) {
        	 escala = escalas.get(0);
        } else if (!escalas.isEmpty() && ehMesmoDia(escalas.get(0).getDataVigenciaFim(),Calendar.getInstance())) {
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
    public void finalizar(EscalaDeTrabalho escalaDeTrabalho) throws Exception { 
		escalaDeTrabalho.setDataVigenciaFim(Calendar.getInstance());
    	escalaDeTrabalho.getDataVigenciaFim().set(Calendar.HOUR_OF_DAY, 23);
    	escalaDeTrabalho.getDataVigenciaFim().set(Calendar.MINUTE, 59);
    	escalaDeTrabalho.getDataVigenciaFim().set(Calendar.SECOND, 59);
		escalaDeTrabalho.save();
		
		result.redirectTo(this).listarPorCondutor(escalaDeTrabalho.getCondutor().getId());
	}
    
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/salvar")
	public void salvar(EscalaDeTrabalho escalaDeTrabalho, List<DiaDeTrabalho> diasDeTrabalho) throws Exception {
       
		if(!validator.hasErrors()) {
        	error(diasDeTrabalho == null || diasDeTrabalho.isEmpty(), "diasDeTrabalho", "escalasDeTrabalho.diaDaSemana.validation");
        }
        
        EscalaDeTrabalho escalaAntiga = new EscalaDeTrabalho();
        escalaAntiga.setDiasDeTrabalho(new ArrayList<DiaDeTrabalho>());
        escalaAntiga.getDiasDeTrabalho().addAll(escalaDeTrabalho.getDiasDeTrabalho());
        escalaAntiga.setId(escalaDeTrabalho.getId());

        if(!validator.hasErrors()) {
	        escalaDeTrabalho.setDiasDeTrabalho(new ArrayList<DiaDeTrabalho>());
	        escalaDeTrabalho.getDiasDeTrabalho().addAll(diasDeTrabalho);
        }
        
        if(!validator.hasErrors()) {
        	validarEscala(escalaDeTrabalho.getDiasDeTrabalho());
        }
        
        if(!validator.hasErrors()) {
        	validarMissoesParaNovaEscala(escalaDeTrabalho);
        }
        
        if(validator.hasErrors()) {
        	MenuMontador.instance(result).recuperarMenuCondutores(escalaDeTrabalho.getCondutor().getId(), ItemMenu.ESCALASDETRABALHO);
        	
            Condutor condutor = escalaDeTrabalho.getCondutor();
            
        	List<EscalaDeTrabalho> escalas = EscalaDeTrabalho.buscarTodosPorCondutor(condutor);
        	
        	for (EscalaDeTrabalho escala : escalas) {
        		if (escalaDeTrabalho.getId().equals(escala.getId())) {
        			escala.setDiasDeTrabalho(escalaAntiga.getDiasDeTrabalho());
        		}
			}
        	
        	DiaDaSemana diaSemana = DiaDaSemana.SEGUNDA;
        	
        	result.include("escalas", escalas);
            result.include("condutor", condutor);
            result.include("escala", escalaDeTrabalho);
            result.include("diaSemana", diaSemana);
            
            validator.onErrorUse(Results.logic()).forwardTo(EscalaDeTrabalhoController.class).listarPorCondutor(condutor.getId());
        }
        
        escalaDeTrabalho.setDataVigenciaInicio(Calendar.getInstance());
        if (ehMesmoDia(escalaDeTrabalho.getDataVigenciaInicio(), Calendar.getInstance())) {
        	if (escalaDeTrabalho.getId() > 0) {
        		Object[] escalas = new Object[1];
        		escalas[0] = escalaDeTrabalho;
        		DiaDeTrabalho.AR.delete("escalaDeTrabalho", escalas);
        	} 
        	
        	escalaDeTrabalho.save();
    		
    		for (DiaDeTrabalho diaDeTrabalho : escalaDeTrabalho.getDiasDeTrabalho()) {
	                DiaDeTrabalho diaDeTrabalhoNovo = new DiaDeTrabalho();
	                diaDeTrabalhoNovo.setDiaEntrada(diaDeTrabalho.getDiaEntrada());
	                diaDeTrabalhoNovo.setDiaSaida(diaDeTrabalho.getDiaSaida());
	                diaDeTrabalhoNovo.setHoraEntrada(diaDeTrabalho.getHoraEntrada());
	                diaDeTrabalhoNovo.setHoraSaida(diaDeTrabalho.getHoraSaida());
	                diaDeTrabalhoNovo.setEscalaDeTrabalho(escalaDeTrabalho);
	                
	                diaDeTrabalhoNovo.save();
    		}
    		
        } else {
        	escalaDeTrabalho.setDataVigenciaFim(Calendar.getInstance());
        	escalaDeTrabalho.getDataVigenciaFim().add(Calendar.DATE,-1);
        	escalaDeTrabalho.getDataVigenciaFim().set(Calendar.HOUR_OF_DAY, 23);
        	escalaDeTrabalho.getDataVigenciaFim().set(Calendar.MINUTE, 59);
        	escalaDeTrabalho.getDataVigenciaFim().set(Calendar.SECOND, 59);
           	escalaDeTrabalho.save();
            EscalaDeTrabalho.AR.em().detach(escalaDeTrabalho);

            EscalaDeTrabalho novaEscala = new EscalaDeTrabalho(); 
        	novaEscala.setCondutor(escalaDeTrabalho.getCondutor());
        	novaEscala.setDataVigenciaInicio(Calendar.getInstance());
        	novaEscala.getDataVigenciaInicio().set(Calendar.HOUR_OF_DAY, 0);
        	novaEscala.getDataVigenciaInicio().set(Calendar.MINUTE, 0);
        	novaEscala.getDataVigenciaInicio().set(Calendar.SECOND, 0);
        	novaEscala.setDiasDeTrabalho(escalaDeTrabalho.getDiasDeTrabalho());
        	novaEscala.save();
        	
    		for (DiaDeTrabalho diaDeTrabalho : diasDeTrabalho) {
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
	@Path("/excluir/{id}")
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
		List<String> periodosDeTrabalho = new ArrayList<String>();
		for (DiaDeTrabalho diaDeTrabalho : diasDeTrabalho) {

			if (diaDeTrabalho.getDiaEntrada().getOrdem() > diaDeTrabalho.getDiaSaida().getOrdem()) {
				validator.add(new I18nMessage("diasDeTrabalho", "escalasDeTrabalho.diaEntrada.validation"));
				return false;
			}
			
			if (diaDeTrabalho.getHoraEntrada().after(diaDeTrabalho.getHoraSaida()) &&
				diaDeTrabalho.getDiaEntrada().getOrdem().equals(diaDeTrabalho.getDiaSaida().getOrdem())) {
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
		StringBuilder listaMissoes = new StringBuilder();
		String delimitador="";

		if (missoes != null && !missoes.isEmpty()) {
			for (Missao missao : missoes) {
				String dataMissao = formatar.format(missao.dataHoraSaida.getTime());
				String dataFormatadaOracle = "to_date('" + dataMissao + "', 'DD/MM/YYYY HH24:mi')";
				if (! missao.condutor.estaEscalado(dataMissao) &&
					! missao.condutor.estaDePlantao(dataFormatadaOracle)	) {
					listaMissoes.append(delimitador).append(missao.getSequence());
					delimitador=",";
					valido = false;	
				}
			} 
			if (! valido) {
				validator.add(new I18nMessage("LinkErroEscalaDeTrabalho", listaMissoes.toString()));
			}
		}
		return valido;
	}
}
