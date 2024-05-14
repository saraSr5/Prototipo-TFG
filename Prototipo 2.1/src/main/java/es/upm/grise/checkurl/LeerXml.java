package es.upm.grise.checkurl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.List;

import java.util.ArrayList;

public class LeerXml {

	// Hace lo mismo que obtenerTitulos(), pero genera la salida a stdout
	// Al usar stdout, los strings deben ir entrecomillados para facilitar el procesamiento posterior
    public static void main(String[] args) {
        if (args.length != 1) {//si hay mas de un argumento
            System.err.println("Error: solo debe tener un argumento");
            System.exit(1);
        }

        String url = args[0];//cojo la url de la linea de comandos
        int numeroTitulos = 0;
        
        System.err.println();
		System.err.println();
		System.err.println();
		System.err.println("Reading titles from DBML file/URL");
		System.err.println("---------------------------------------------------------");
        
        String[] titulos = obtenerTitulos(url);
        for (String titulo : titulos) {
            System.out.println("\"" + titulo + "\"");
            numeroTitulos++;
        }    
        
        System.err.println();
        System.err.println(numeroTitulos + " titles read");
    }
    
    public static String[] obtenerTitulos(String url) {
	    Document documento = null;
	
	    try {
			documento = Jsoup.connect(url).get(); //guardo en documento el xml
		} catch (Exception e1) {
				try {
					documento = Jsoup.parse(new File(url), "UTF-8", ""); //intentamos leer desde un fichero local
				} catch (Exception e2) {
					System.err.println("No existe o es incorrecto el fichero/URL: " + "\"" + url + "\"");
					System.exit(1); //No se puede hacer nada. Salimos con error
				} 
			}
	        
	    Elements titleElementos = documento.select("title");//busco en el documento xml las etiquetas <ee> ya que es donde se encuentran las url de los articulos
	    List<String> titulos = new ArrayList<>();//ArrayList para guardar los titulos de los articulos
	    for (Element titleElemento : titleElementos) {//itero sobre las etiquetas <ee>
	        String link = titleElemento.text();//extraigo el texto del elemento
	        link = link.substring(0, link.length() - 1); // eliminamos el punto final
	        titulos.add(link);//Meto los titulos encontrados al array
        	System.err.println("Título encontrado: " + link);

	    }
	    return titulos.toArray(new String[0]);
	    }
}