package play.modules.br.jus.jfrj.siga.uteis.validadores.email;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import play.data.validation.Check;

public class EmailCheck extends Check{
	private static final String EMAIL_PATTERN = 
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	@Override
	public boolean isSatisfied(Object validatedObject, Object value) {
		boolean aceitaNulos = false;
		this.setMessage("Formato incorreto de e-mail.");

		if (value.equals("")) {
			if (verificarSeValorNuloEhValido(validatedObject)) {
				aceitaNulos = true;
			}
		}
    	return validarEmail(((String) value).toString(), aceitaNulos);
 	}

	private boolean verificarSeValorNuloEhValido(Object validatedObject) {
		if (validatedObject.getClass().getName().contains("Fornecedor")) { 
			return true;
		}
		
		return false;
	}

	public boolean validarEmail(String valor, Boolean aceitaNulos) {
      Pattern p = Pattern.compile(EMAIL_PATTERN);  
      Matcher m = p.matcher(valor);  
      boolean matchFound = m.matches();
      
      if (valor.equals("") && aceitaNulos) {
    	  return true;
      }
  
      if (matchFound) {  
        return true;
      }
      else {  
		return false;
      }
	}
}