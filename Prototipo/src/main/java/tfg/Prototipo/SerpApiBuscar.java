package tfg.Prototipo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SerpApiBuscar {

    public static void main(String[] args) {
        String url = "https://dblp.org/search/publ/api?q=toc%3Adb/journals/tse/tse37.bht%3A&h=1000&format=xml";//url del archivo xml

        try {
            Document documento = Jsoup.connect(url).get();//guardo en documento el xml
            
            Elements eeElementos = documento.select("ee");//cojo todos los elementos que estén entre las etiquetas <ee> ya que son los articulos
            for (Element eeElemento : eeElementos) {//itero sobre los elementos <ee>
                String link = eeElemento.text();//guardo el enlace en la variable link
                System.out.println("Enlace: " + link);//lo muestro

                if (esEnlacePDF(link)) {//llamo a la funcion para saber si el link es un pdf
                    System.out.println("Enlace PDF encontrado: " + link);//si es cierto lo muestro

                  
                    buscarEnGoogleScolar(link);//Busco en GoogleScolar
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//Funcion que comprueba si el enlace que le paso es un enlace de un pdf
    private static boolean esEnlacePDF(String enlace) {
        return enlace.toLowerCase().endsWith(".pdf");//miro que el enlace termine en .pdf
    }
//Funcion que me permite buscar el pdf con GoogleScolar
    private static void buscarEnGoogleScolar(String enlaceArticulo) {
        try {
            URL url = new URL("https://scholar.google.com/scholar?hl=en&q=" + enlaceArticulo);//para buscar el enlace
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();//conexion para buscar la url creada

            //Configuro la solicitud HTTP
            connection.setRequestMethod("GET");//quiero que sea un GET
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");//necesito un agente para evitar que me bloqueen. Elijo Mozilla porque suele cambiar menos de politicas 

            //Realizo la solicitud HTTP
            int rCodigo = connection.getResponseCode();//guardo el codigo de respuesta del servidor

            if (rCodigo == HttpURLConnection.HTTP_OK) {//si es un HTTP_OK
                //Leo la respuesta
                BufferedReader leer = new BufferedReader(new InputStreamReader(connection.getInputStream()));//buffer para guardar la respuesta leida
                StringBuilder respuesta = new StringBuilder();//creo la respuesta con StringBuilder para que me permita concatenar las cadenas y modififcarlas de forma mas eficiente que un String
                String linea;//creo la variable linea para leer por linea

                while ((linea = leer.readLine()) != null) {//leo por linea el buffer donde guarde la respuesta
                    respuesta.append(linea);//agrego al final de la cadena la linea leida
                }

                leer.close();

                
                System.out.println("Respuesta de Google Scholar: " + respuesta.toString());//muestro la respuesta de google scolar
            } else {
                System.err.println("Error en la solicitud a Google Scholar. Código de respuesta: " + rCodigo);//muestro el codigo de respuesta del servidor
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
