package br.gov.jfrj.siga.tp.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import play.mvc.Http;

public class FormataCaminhoDoContextoUrl {
	public String retornarCaminhoContextoUrl(String url) {
		Matcher m;
		String pattern = "";
		
        if (Http.Request.current() == null) {
        	//Para as urls iniciadas com application.base
        	pattern = "[\\w]*.[\\w]*";
        }
        
        else {
        	//Para as urls iniciadas com http://
			pattern = ".*://([^:^/]*)(:\\d*)?(.*)?";
			//pattern = ".*\\/\\/[\\d|\\w]*(:[0-9]*)*";
        }

		Pattern r = Pattern.compile(pattern);
		m = r.matcher(url);

		if (m.find()) {
			//return url.replace(m.group(),"");
			return m.group(3).toString().substring(1);
		}
        else {
        	return url;
        }
	}
}