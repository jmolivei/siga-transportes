package controllers;

import play.data.validation.Validation;
import play.mvc.*;

@With(AutorizacaoGIAntigo.class)
public class Erros extends Controller {

    public static void testErroLink() {
    //	Validation.addError("LinkErro", "TRF2-MTP-2014/00002,TRF2-MTP-2014/00013,TRF2-MTP-2014/00010");    
    	Validation.addError("LinkGenericoErro", "erros.LinkGenericoErro.validation");    
    	render();
    }
    
    public static void mostrar() {
         	Validation.addError("LinkGenericoErro", "erros.LinkGenericoErro.validation");    
        	render();
        }

}
