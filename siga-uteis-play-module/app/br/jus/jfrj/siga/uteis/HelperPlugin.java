package br.jus.jfrj.siga.uteis;

import play.PlayPlugin;
import play.classloading.ApplicationClasses.ApplicationClass;

public class HelperPlugin extends PlayPlugin {

        private UpperCaseHelperEnhancer enhancer = new UpperCaseHelperEnhancer();

    	@Override
    	public void onConfigurationRead() {
    		super.onConfigurationRead();
    	}
        
        @Override
        public void enhance(ApplicationClass applicationClass) throws Exception {
                enhancer.enhanceThisClass(applicationClass);
        }
        
        
        

}
