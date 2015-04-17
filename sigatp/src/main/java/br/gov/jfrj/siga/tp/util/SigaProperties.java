package br.gov.jfrj.siga.tp.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SigaProperties {

	public static String getValue(String key) {
		Properties prop = new Properties();
		InputStream input = null;

		try {
			input = SigaProperties.class.getClassLoader().getResourceAsStream(
					"conf/config.properties");
			prop.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return prop.getProperty(key);
	}
}
