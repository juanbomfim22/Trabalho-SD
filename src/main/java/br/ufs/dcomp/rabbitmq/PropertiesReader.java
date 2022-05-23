package br.ufs.dcomp.rabbitmq;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {
	public final static Properties properties = new Properties();
	private final InputStream is;
	
	public static String getProperty(String key) {
		return properties.getProperty(key);
	}
	
	public PropertiesReader() {
		is = this.getClass().getResourceAsStream("/system.properties");
		try {
			properties.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
