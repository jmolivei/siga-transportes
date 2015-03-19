import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class testeCurrency {
	
	/**
	 * @param args
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws ParseException {
		// TODO Auto-generated method stub
		
		Locale defaultLocale = new Locale("pt", "BR", "BRL");
		NumberFormat nf = NumberFormat.getCurrencyInstance(defaultLocale);
		System.out.println(nf.format(23.90));
		
		DecimalFormat f = (DecimalFormat) DecimalFormat.getInstance(new Locale("pt", "BR"));  
		Double d = (Double)f.parse("1.234,56");  
		System.out.println(d.doubleValue());  	
		
	}

}
