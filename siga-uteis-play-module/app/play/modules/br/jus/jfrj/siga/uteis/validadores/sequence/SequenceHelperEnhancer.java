package play.modules.br.jus.jfrj.siga.uteis.validadores.sequence;

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
import play.classloading.ApplicationClasses.ApplicationClass;
import play.classloading.enhancers.Enhancer;

public class SequenceHelperEnhancer extends Enhancer {

        @Override
        public void enhanceThisClass(ApplicationClass applicationClass) throws Exception {
                CtClass ctClass = makeClass(applicationClass);

        
        /** Pesquisando para verificar se classe é deirvada de GenericModel ou Model e se
         *  tem algum atributo marcado com a annotation Sequence.
         */
               
        if (!ctClass.subtypeOf(classPool.get("play.db.jpa.GenericModel")) ||
        	!ctClass.subtypeOf(classPool.get("play.db.jpa.Model")) ||	
            !hasAnnotation(ctClass, "play.modules.br.jus.jfrj.siga.uteis.Sequence")) {
            return;
        }
        
        for (CtField ctField : ctClass.getFields()) {

                if (hasAnnotation(ctField, "play.modules.br.jus.jfrj.siga.uteis.Sequence")) {
                       
                	   /**
                	    *    Criando o método que criará gerará a sequência
                	    */
                	   String atribuicaoSequencia="";
                       String nomeAtribuitoSequencia="";
                       atribuicaoSequencia =  "this." + ctField.getName() + "=000012;";
                       nomeAtribuitoSequencia = ctField.getName();
                       StringBuilder metodoGeradorSequencia = new StringBuilder();
                       metodoGeradorSequencia.append("public void gerarSequencia() {");
                       metodoGeradorSequencia.append(atribuicaoSequencia.toString());
                       metodoGeradorSequencia.append("system.out.println('Sequencia gerada: ' +");
                       metodoGeradorSequencia.append(nomeAtribuitoSequencia + ");");
                       metodoGeradorSequencia.append("}");
                       
                       CtMethod geraSequencia = CtMethod.make(metodoGeradorSequencia.toString(), ctClass);
                       ctClass.addMethod(geraSequencia);
           			   System.out.println("Método gerado na classe " + ctClass.getName() + " : " +  metodoGeradorSequencia.toString());
                       
                    // create the annotation
                       ClassFile ccFile = ctClass.getClassFile();
                       ConstPool constpool = ccFile.getConstPool();
                       AnnotationsAttribute attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
                       Annotation annot = new Annotation("javax.persistence.PrePersist", constpool);
           		       System.out.println("Anotação geradas no método " + geraSequencia.getName() + " : " +  annot.toString());
                       // Exemplo de como adicionar parâmetros na annotation 
                       // annot.addMemberValue("name", new StringMemberValue("World!! (dynamic annotation)",ccFile.getConstPool()));
                       attr.addAnnotation(annot);
                       // add the annotation to the method descriptor
                       geraSequencia.getMethodInfo().addAttribute(attr);
                       break;
                }
        } 
      
        applicationClass.enhancedByteCode = ctClass.toBytecode();
        ctClass.defrost();
        }
}
