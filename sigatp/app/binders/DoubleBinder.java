package binders;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import org.apache.log4j.Logger;

import play.data.binding.TypeBinder;

public class DoubleBinder implements TypeBinder<Double> {
    public Object bind(String name, Annotation[] annotations, String value, Class actualClass, Type genericType) {
    	try {
    		Logger.getLogger(this.getClass()).warn("propriedade : " + name + " para ser transformado em double : " + value);
        	String valorInput = (value.equals("") ? "0.00" : value.replace(".","").replace(",", "."));
            return new Double(valorInput);
    	} catch (Exception ex) {
			throw new RuntimeException(ex);
    	}
    }
}
