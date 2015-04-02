package controllers;

import java.text.ParseException;
import java.util.Calendar;

import play.mvc.*;

@With(AutorizacaoGIAntigo.class)
public class Agendas extends Controller {
	
	public static Long NOVOVEICULO = null;
	public static String ACTION_EDITAR = "Veiculos/Editar";


    public static void listar(Long id) {
       emdesenvolvimento();
    }
    
    
    public static void listarPorCondutor(Long idCondutor) throws ParseException {
        Relatorios.listarAgendaPorCondutor(Calendar.getInstance(),idCondutor);
     }
    
    public static void listarPorVeiculo(Long idVeiculo) throws ParseException {
        Relatorios.listarAgendaPorVeiculo(Calendar.getInstance(),idVeiculo);
     }
    
    
    public static void emdesenvolvimento() {
       render();
    }

}
