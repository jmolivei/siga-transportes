package uteis;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import play.templates.JavaExtensions;

public class CustomJavaExtensions extends JavaExtensions {
	public static String formataMoedaBrasileira(Number number) {
		Locale defaultLocale = new Locale("pt", "BR", "BRL");
		NumberFormat nf = NumberFormat.getCurrencyInstance(defaultLocale);
		return  nf.format(number);

	}
	
	public static String formataMoedaBrasileiraSemSimbolo(Number number) {
		Locale defaultLocale = new Locale("pt", "BR", "BRL");
		NumberFormat nf = NumberFormat.getCurrencyInstance(defaultLocale);
		String moeda =  nf.format(number).replace("R$", "").trim();
		return moeda;
	}
	
	public static String formataValorExponencialParaDecimal(Number number) {
		return BigDecimal.valueOf((Double) number).toPlainString();
	}
}