package play.modules.br.jus.jfrj.siga.uteis;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import play.Play;
import play.PlayPlugin;
import play.classloading.ApplicationClasses.ApplicationClass;
import play.db.jpa.GenericModel;
import play.modules.br.jus.jfrj.siga.uteis.validadores.upperCase.UpperCaseHelperEnhancer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class HelperPlugin extends PlayPlugin {

        private UpperCaseHelperEnhancer enhancer = new UpperCaseHelperEnhancer();

        @Override
        public void enhance(ApplicationClass applicationClass) throws Exception {
                enhancer.enhanceThisClass(applicationClass);
              
        }
        
        
        

}
