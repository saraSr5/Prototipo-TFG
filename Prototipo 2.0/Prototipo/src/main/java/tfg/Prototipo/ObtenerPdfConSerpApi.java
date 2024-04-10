package tfg.Prototipo;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ObtenerPdfConSerpApi {
    private static final String API_KEY = "4d30e7fc9aaf5249b6828c95a24364fd48a1128914289250328a1f522ce5663f";//API_KEY de SerpApi para buscar

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Error debe ser 1 argumento");
            System.exit(1);
        }

        String tituloArticulo = args[0];//Cojo el titulo de la linea de comandos
        System.out.println("Título del artículo: " + tituloArticulo); 

        ObtenerPdfConSerpApi obtPdf = new ObtenerPdfConSerpApi();

        try {
            String jsonRespuesta = obtPdf.obtenerJSON(tituloArticulo);//Conecto para buscar el JSON

            String link = obtenerPrimerLink(jsonRespuesta);//Miro el JSON y cojo el enlace de descarga
            if (link != null) {
                System.out.println("El primer enlace encontrado es: " + link);
            } else {
                System.out.println("No se encontraron enlaces en el JSON obtenido.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//Funcion para obtener el JSON
    public String obtenerJSON(String tituloArticulo) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String tituloBuscar = URLEncoder.encode(tituloArticulo, StandardCharsets.UTF_8);

        String urlSerpApi = "https://serpapi.com/search.json?engine=google_scholar&api_key=" + API_KEY + "&q=" + tituloBuscar;

        Request respuestaBuilder = new Request.Builder()//Respuesta de SerpApi
                .url(urlSerpApi)
                .build();

        try (Response r = client.newCall(respuestaBuilder).execute()) {
            if (!r.isSuccessful()) throw new IOException("Fallo " + r);

            return r.body().string();
        }
    }
//Funcion que lee y recorre el JSON para encontrar el enlace de descarga del pdf
    public static String obtenerPrimerLink(String jsonObt) {
        try {
            JSONObject jsonObjecto = new JSONObject(jsonObt);
            JSONArray organicResults = jsonObjecto.getJSONArray("organic_results");//Miro en la parte de organic_results
            if (organicResults.length() > 0) {
                JSONObject primerResult = organicResults.getJSONObject(0);
                JSONArray resources = primerResult.getJSONArray("resources");//Miro en la parte de resources
                if (resources.length() > 0) {
                    JSONObject primeraResource = resources.getJSONObject(0);
                    return primeraResource.getString("link");//Cojo el primer link que se obtiene de descarga
                }
            }
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
