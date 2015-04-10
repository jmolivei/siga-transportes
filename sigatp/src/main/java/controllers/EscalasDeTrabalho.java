package controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import play.data.validation.Valid;
import play.data.validation.Validation;
import play.db.jpa.JPA;
import play.mvc.Controller;
import play.mvc.With;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdmin;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissao;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissaoComplexo;
import br.gov.jfrj.siga.tp.model.Condutor;
import br.gov.jfrj.siga.tp.model.DiaDaSemana;
import br.gov.jfrj.siga.tp.model.DiaDeTrabalho;
import br.gov.jfrj.siga.tp.model.EscalaDeTrabalho;
import br.gov.jfrj.siga.tp.model.ItemMenu;
import br.gov.jfrj.siga.tp.model.Missao;
import br.gov.jfrj.siga.tp.util.MenuMontador;

@With(AutorizacaoGIAntigo.class)
public class EscalasDeTrabalho extends Controller {
	
    public static void listar() {
    	List<EscalaDeTrabalho> escalas = EscalaDeTrabalho.buscarTodasVigentes();
        
        render(escalas);
    }
    
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
    public static void incluir(Long idCondutor) throws Exception {
    	MenuMontador.instance().recuperarMenuCondutores(idCondutor, ItemMenu.ESCALASDETRABALHO);
    	Condutor condutor = Condutor.AR.findById(idCondutor);
    	
    	EscalaDeTrabalho escala = new EscalaDeTrabalho();
    	escala.setCondutor(condutor);
        
    	// resolver
    	DiaDaSemana diaSemana = DiaDaSemana.SEGUNDA;
    	DiaDeTrabalho diaTrabalho = new DiaDeTrabalho();
    	escala.getDiasDeTrabalho().add(diaTrabalho);
    	
    	render(escala, diaSemana);
    }
    
	@RoleAdmin
	@RoleAdminMissao   
	@RoleAdminMissaoComplexo
    public static void editar(Long id) throws Exception {
    	EscalaDeTrabalho escala = EscalaDeTrabalho.AR.findById(id);
    	
    	MenuMontador.instance().recuperarMenuCondutores(escala.getCondutor().getId(), ItemMenu.ESCALASDETRABALHO);
    	
    	DiaDaSemana diaSemana = DiaDaSemana.SEGUNDA;
    	
    	render(escala, diaSemana);
    }
    
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void excluir(Long id) throws Exception {
    	EscalaDeTrabalho escala = EscalaDeTrabalho.AR.findById(id);
    	Long idCondutor = escala.getCondutor().getId();
    	escala.delete();
    	
    	listarPorCondutor(idCondutor);
    }
        
    public static void listarPorCondutor(Long idCondutor) throws Exception {
    	MenuMontador.instance().recuperarMenuCondutores(idCondutor, ItemMenu.ESCALASDETRABALHO);
        Condutor condutor = Condutor.AR.findById(idCondutor);
        
        List<EscalaDeTrabalho> escalas = EscalaDeTrabalho.buscarTodosPorCondutor(condutor);
        
        EscalaDeTrabalho escala = new EscalaDeTrabalho();
        escala.setCondutor(condutor);
    	DiaDaSemana diaSemana = DiaDaSemana.SEGUNDA;
    	escala.setDataVigenciaInicio(Calendar.getInstance());
    	DiaDeTrabalho diaTrabalho = new DiaDeTrabalho();
    	escala.getDiasDeTrabalho().add(diaTrabalho);
       
        if (escalas.size() > 0 &&
        	(escalas.get(0).getDataVigenciaFim() == null ||
        	 escalas.get(0).getDataVigenciaFim().after(Calendar.getInstance()))
        	) {
        	 escala = escalas.get(0);
        } else {
        	if (escalas.size() > 0 && ehMesmoDia(escalas.get(0).getDataVigenciaFim(),Calendar.getInstance())) {
        		escalas.add(0,escala);
        	}
        }
        
        render(escalas, condutor, escala, diaSemana);
     }
    

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
    public static void finalizar(@Valid EscalaDeTrabalho escala) throws Exception { 
		escala.setDataVigenciaFim(Calendar.getInstance());
    	escala.getDataVigenciaFim().set(Calendar.HOUR_OF_DAY, 23);
    	escala.getDataVigenciaFim().set(Calendar.MINUTE, 59);
    	escala.getDataVigenciaFim().set(Calendar.SECOND, 59);
		escala.save();
		listarPorCondutor(escala.getCondutor().getId());
	}
    
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void salvar(EscalaDeTrabalho escala, EscalaDeTrabalho novaEscala) throws Exception {
        if(! Validation.hasErrors()) {
        	String diaDaSemana = (String) params.get("novaEscala.diasDeTrabalho[0].diaEntrada", String.class);
        	if (diaDaSemana == null) {
        		Validation.addError("diasDeTrabalho", "escalasDeTrabalho.diaDaSemana.validation");
        		novaEscala.setDiasDeTrabalho(null);
        	}	
        }
        
        EscalaDeTrabalho escalaAntiga = new EscalaDeTrabalho();
        escalaAntiga.setDiasDeTrabalho(new ArrayList<DiaDeTrabalho>());
        escalaAntiga.getDiasDeTrabalho().addAll(escala.getDiasDeTrabalho());
        escalaAntiga.setId(escala.getId());

        if(! Validation.hasErrors()) {
	        escala.setDiasDeTrabalho(new ArrayList<DiaDeTrabalho>());
	        escala.getDiasDeTrabalho().addAll(novaEscala.getDiasDeTrabalho());
        }
        
        if(! Validation.hasErrors()) {
        	validarEscala(escala.getDiasDeTrabalho());
        }
        
        if(! Validation.hasErrors()) {
        	validarMissoesParaNovaEscala(escala);
        }
        
        if(Validation.hasErrors()) {
     /*       for(play.data.validation.Error error : validation.errors()) {
                System.out.println(error.message());
                System.out.println(error.getKey());
            } */

        	MenuMontador.instance().recuperarMenuCondutores(escala.getCondutor().getId(), ItemMenu.ESCALASDETRABALHO);
        	
            Condutor condutor = escala.getCondutor();
            
        	List<EscalaDeTrabalho> escalas = EscalaDeTrabalho.buscarTodosPorCondutor(condutor);
        	
        	for (EscalaDeTrabalho escalaDeTrabalho : escalas) {
        		if (escala.getId() == escalaDeTrabalho.getId()) {
        			escalaDeTrabalho.setDiasDeTrabalho(escalaAntiga.getDiasDeTrabalho());
        		}
			}
        	
        	DiaDaSemana diaSemana = DiaDaSemana.SEGUNDA;
        	
	    	renderTemplate("@listarPorCondutor",escalas,condutor,escala,diaSemana);
        }
        
        if (ehMesmoDia(escala.getDataVigenciaInicio(), Calendar.getInstance())) {
        	if (escala.getId() > 0) {
        	 	//DiaDeTrabalho.delete("escalaDeTrabalho", escala);
        	} 
        	
        	escala.save();
    		
    		for (DiaDeTrabalho diaDeTrabalho : escala.getDiasDeTrabalho()) {
                DiaDeTrabalho diaDeTrabalhoNovo = new DiaDeTrabalho();
                diaDeTrabalhoNovo.setDiaEntrada(diaDeTrabalho.getDiaEntrada());
                diaDeTrabalhoNovo.setDiaSaida(diaDeTrabalho.getDiaSaida());;
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
            JPA.em().detach(escala);

            //EscalaDeTrabalho novaEscala = new EscalaDeTrabalho();
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

    		//listarPorCondutor(escala.condutor.id); ALterado para vol
        }   Condutores.listar();
    }
	
	private static boolean validarMissoesParaNovaEscala(EscalaDeTrabalho escala) throws Exception {
		// TODO Auto-generated method stub
		List<Missao> missoes = Missoes.buscarPorCondutoreseEscala(escala);
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
				Validation.addError("LinkErroEscalaDeTrabalho", listaMissoes);
			}
		}
		return valido;
	}

	private static boolean  ehMesmoDia(Calendar cal1, Calendar cal2) {
	    if (cal1 == null || cal2 == null)
	        return false;
	    return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
	            && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) 
	            && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
	}
	
	private static boolean validarEscala(List<DiaDeTrabalho> diasDeTrabalho) {
		Collections.sort(diasDeTrabalho);
		ArrayList<String> periodosDeTrabalho = new ArrayList<String>();
		for (DiaDeTrabalho diaDeTrabalho : diasDeTrabalho) {
			if (diaDeTrabalho.getDiaEntrada().getOrdem() > diaDeTrabalho.getDiaSaida().getOrdem()) {
				Validation.addError("diasDeTrabalho", "escalasDeTrabalho.diaEntrada.validation");
				return false;
			}
			
			if (diaDeTrabalho.getHoraEntrada().after(diaDeTrabalho.getHoraSaida()) &&
				diaDeTrabalho.getDiaEntrada().getOrdem() == diaDeTrabalho.getDiaSaida().getOrdem() ) {
				Validation.addError("diasDeTrabalho", "escalasDeTrabalho.horaEntrada.validation");
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
					Validation.addError("diasDeTrabalho", "escalasDeTrabalho.periodosDeTrabalho.validation");
					return false;
				}
			}
		}
		
		
		return true;
	}
}