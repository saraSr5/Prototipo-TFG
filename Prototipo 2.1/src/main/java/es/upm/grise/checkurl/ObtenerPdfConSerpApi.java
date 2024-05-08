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
    private static final String API_KEY = "4d30e7fc9aaf5249b6828c95a24364fd48a1128914289250328a1f522ce5663f";//API_KEY de SerpApi para buscar

    public static String[][] main(String[] args) {
        if (args.length == 0) {
            System.err.println("No se ha proporcionado ningún título.");
            System.exit(1);
        }
        
        List<String[]> titulosYEnlaces = new ArrayList<>();
        
        for(String tituloArticulo : args) { //Cojo el título de la línea de comandos
            String link = obtenerEnlacePDF(tituloArticulo);
            if (link != null) {
                titulosYEnlaces.add(new String[]{tituloArticulo, link});
            } else {
                System.err.println("No se encontraron enlaces para: " + tituloArticulo);
            }
        }
        
        //Convierto a String[][] 
        return titulosYEnlaces.toArray(new String[0][0]);
    }

    //Método para obtener el enlace PDF de un título de artículo utilizando la API de SerpApi
    public static String obtenerEnlacePDF(String tituloArticulo) {
    OkHttpClient client = new OkHttpClient();

    try {
        String tituloBuscar = URLEncoder.encode(tituloArticulo, StandardCharsets.UTF_8);

        String urlSerpApi = "https://serpapi.com/search.json?engine=google_scholar&api_key=" + API_KEY + "&q=" + tituloBuscar;

        Request request = new Request.Builder()
                .url(urlSerpApi)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Fallo " + response);

            String JSONrespuesta = response.body().string();
            String link = obtenerPrimerLink(JSONrespuesta);

            if (link != null) {
                return link;
            } else {
                System.err.println("No se encontraron enlaces para: " + tituloArticulo);
                System.err.println("Respuesta JSON de SERP: " + JSONrespuesta);
                return null;
            }
        }
    } catch (IOException e) {
        System.err.println("Error al obtener enlace PDF para: " + tituloArticulo);
        e.printStackTrace();
        return null;
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
            e.printStackTrace();
            return null;
        }
    }
}
