package es.upm.grise.checkurl;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ObtenerPdfConSerpApi {
    private static final String API_KEY = "";//API_KEY de SerpApi para buscar

    // Hace lo mismo que obtenerTodosLosEnlacesPDF(), pero genera salida a stdout
	// Al usar stdout, los strings deben ir entrecomillados para facilitar el procesamiento posterior
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("No se ha proporcionado ningún título");
            System.exit(1);
        }
        
        int numeroEnlaces = 0;
        
		System.err.println("Getting links from SerAPI");
        System.err.println("==================================================================================");
		System.err.println();
        
        for(String tituloArticulo : args) { //Cojo el título de la línea de comandos, pero uno a uno
        	
        	String[] respuesta = obtenerEnlacePDF(tituloArticulo);
        	
            if (respuesta != null) {
                System.out.println( "\"" + respuesta[0] + "\"" + " " + "\"" + respuesta[1] + "\"");
                numeroEnlaces++;
            }
        }
        
        System.err.println();
        System.err.println(numeroEnlaces + " links read");
    }
    
    //Método para obtener todos los enlaces PDF
    public static String[][] obtenerTodosLosEnlacesPDF(String[] titulosArticulos) {
        List<String[]> titulosYEnlaces = new ArrayList<String[]>();
        
        if (titulosArticulos.length == 0) {
            System.err.println("No se ha proporcionado ningún título");
            System.exit(1);
        }
        
        for(String tituloArticulo : titulosArticulos) {
        	
        	String[] respuesta = obtenerEnlacePDF(tituloArticulo);
        	
            if (respuesta != null) {
            	titulosYEnlaces.add(respuesta);
            }
        }
        
        //Convierto a String[][] 
        return titulosYEnlaces.toArray(new String[0][0]);
    }

    //Método para obtener el enlace PDF de un título de artículo utilizando la API de SerpApi
    public static String[] obtenerEnlacePDF(String tituloArticulo) {
    	
    	OkHttpClient client = new OkHttpClient();
    	String link = null;
    	Boolean skip = false;
    	
        if(ConexionBaseDeDatos.BDExist()) {
        	
        	if(ConexionBaseDeDatos.isNombrePDFExistente(tituloArticulo)) {
        		
				System.err.println("El PDF ya había sido descargado previamente: " + "\"" + tituloArticulo + "\"");
				skip = true;
        		
        	} else {
        		
        		ConexionBaseDeDatos.initializeDatabase();
        		
        	}
        	
        }
        
        if(!skip) {
        	
        	try {

        		String tituloBuscar = URLEncoder.encode(tituloArticulo, StandardCharsets.UTF_8);

        		String urlSerpApi = "https://serpapi.com/search.json?engine=google_scholar&api_key=" + API_KEY + "&q=" + tituloBuscar;

        		Request request = new Request.Builder()
        				.url(urlSerpApi)
        				.build();

        		try (Response response = client.newCall(request).execute()) {
        			if (!response.isSuccessful()) {
        				throw new IOException("Fallo " + response);
        			}

        			String JSONrespuesta = response.body().string();
        			link = obtenerPrimerLink(JSONrespuesta);
        		}

        	} catch (IOException e) {
        		System.err.println("Error al obtener enlace PDF para: " + tituloArticulo);
        	}
        	
        }
        
    	if (link == null) {
    		System.err.println("No se encontraron enlaces para: " + tituloArticulo);
    		return null;
    	} else {
        	System.err.println("Link encontrado: " + link);
    		return new String[]{tituloArticulo, link};
    	}

    }


    //Método para extraer el primer enlace PDF del JSON de respuesta de SerpApi
    private static String obtenerPrimerLink(String jsonObt) {
        try {
            JSONObject jsonObject = new JSONObject(jsonObt);
            JSONArray organicResults = jsonObject.getJSONArray("organic_results");

            if (organicResults.length() > 0) {
                JSONObject primerResultado = organicResults.getJSONObject(0);
                JSONArray recursos = primerResultado.getJSONArray("resources");

                if (recursos.length() > 0) {
                    JSONObject primerRecurso = recursos.getJSONObject(0);
                    return primerRecurso.getString("link");
                }
            }
            return null;
        } catch (JSONException e) {
            System.err.println("Error al analizar la respuesta JSON de SerpApi");
            return null;
        }
    }

}
