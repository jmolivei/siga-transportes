package play.modules.br.jus.jfrj.siga.uteis.validadores.upperCase;

import java.util.ArrayList;
import java.util.List;

import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;
import play.Logger;
import play.classloading.ApplicationClasses.ApplicationClass;
import play.classloading.enhancers.Enhancer;

public class UpperCaseHelperEnhancer extends Enhancer {

	@Override
	public void enhanceThisClass(ApplicationClass applicationClass) throws Exception {
		CtClass ctClass = makeClass(applicationClass);


		/** Pesquisando para verificar se classe é deirvada de GenericModel ou Model e se
		 *  tem algum atributo marcado com a annotation Sequence.
		 */

		if (!ctClass.subtypeOf(classPool.get("play.db.jpa.GenericModel")) &&
				!ctClass.subtypeOf(classPool.get("play.db.jpa.Model")))  
		{
			// Logger.info("Classe: " + ctClass.getName());
			return;
		}
		
		// Logger.info("Classe filha de Model ou GenericModel " + ctClass.getName());
		 
		for (CtMethod ctMethod : ctClass.getMethods()) {
			//	Logger.info("Nome do método já adicionado: " + ctMethod.getName());
				if (ctMethod.getName().equals("_toUpperCase")) {
					Logger.info("Classe filha de Model ou GenericModel " + ctClass.getName());
					return;
				}
			}   
		
		
		List<CtField> fieldsToUppercase = new ArrayList<CtField>(); 
		
		for (CtField ctField : ctClass.getFields()) {
			if (hasAnnotation(ctField, "play.modules.br.jus.jfrj.siga.uteis.UpperCase")) {
				Logger.info("Classe a ser adicionada metodo toUpperCase : " + ctClass.getName());
				fieldsToUppercase.add(ctField);
			}
		} 
		
		StringBuilder metodoGeradorSequencia = new StringBuilder();
		
		if (fieldsToUppercase.size() > 0) {
			metodoGeradorSequencia.append("public void _toUpperCase() {");
		 	metodoGeradorSequencia.append(System.getProperty("line.separator"));
		}
		
		for (CtField ctField : fieldsToUppercase) {
			/**
			 *    Criando o método que converterá todos os atributos do tipo String anotados
			 *    com @UpperCase
			 */

			
			Logger.info("Field : " + ctField.getName() + "da classe :" + ctField.getType().getName());
			if (ctField.getType().getName().equals("java.lang.String")) { 
				metodoGeradorSequencia.append("if (this." + ctField.getName() + " != null) {");
				metodoGeradorSequencia.append(System.getProperty("line.separator"));
				metodoGeradorSequencia.append("this." + ctField.getName());
				metodoGeradorSequencia.append(" = ");
				metodoGeradorSequencia.append("this." + ctField.getName());
				metodoGeradorSequencia.append(".toUpperCase();");
				metodoGeradorSequencia.append(System.getProperty("line.separator"));
				metodoGeradorSequencia.append("}");
				metodoGeradorSequencia.append(System.getProperty("line.separator"));
			}
		}
		
		if (fieldsToUppercase.size() > 0) {
			metodoGeradorSequencia.append("}");			
			Logger.info("Método gerado na classe " + ctClass.getName() + " : " +  metodoGeradorSequencia.toString());
			CtMethod geraSequencia = CtMethod.make(metodoGeradorSequencia.toString(), ctClass);
 			ctClass.addMethod(geraSequencia);
			
			// create the annotation
			ClassFile ccFile = ctClass.getClassFile();
			ConstPool constpool = ccFile.getConstPool();
			AnnotationsAttribute attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
			Annotation annotPersist = new Annotation("javax.persistence.PrePersist", constpool);
			Annotation annotUpdate = new Annotation("javax.persistence.PreUpdate", constpool);
			attr.addAnnotation(annotPersist);
			attr.addAnnotation(annotUpdate);
			Logger.info("Anotações geradas no método " + geraSequencia.getName() + " : " +  annotPersist.toString() + "-" + annotUpdate.toString());
			// add the annotation to the method descriptor
			geraSequencia.getMethodInfo().addAttribute(attr);
		
		}
 
		applicationClass.enhancedByteCode = ctClass.toBytecode();
		ctClass.defrost();
	}
}
