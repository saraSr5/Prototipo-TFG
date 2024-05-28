package es.upm.grise.checkurl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.Pair;

public class ClasificarURL {
	
	static String OTHER = "";
	
	public static void main(String [] args) throws IOException, SQLException {
		
		Properties sites = new Properties();
		
		InputStream inputStream = new FileInputStream("sites.properties");
		
		sites.load(inputStream);
		
		List<String> linkList = ConexionBaseDeDatos.getAllURLs();
		
		for(String link : linkList) {

			String type = getURLType(link, sites);
			
			if(!type.equals(OTHER)) {
				
				System.out.print(type);
				
			}
			
			System.out.println("\t" + link);
						
			ConexionBaseDeDatos.saveURLType(link, type);

		}
		
	}
	
	public static String getURLType(String link, Properties sites) {
		
		for (Entry<Object, Object> category : sites.entrySet())
		{
			
			List<String> partialURLs = Arrays.asList(category.getValue().toString().split(","));
			
			for(String partialURL : partialURLs) {
				
				Pattern p = Pattern.compile(partialURL);
			    Matcher m = p.matcher(link);
								
				if(m.find()) {
					
					return category.getKey().toString();
					
				}
				
			}
			
		}
		
		return OTHER;
		
	}

}
