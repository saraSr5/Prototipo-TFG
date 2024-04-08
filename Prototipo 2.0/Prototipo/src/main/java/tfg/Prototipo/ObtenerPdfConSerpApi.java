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
    private static final String API_KEY = "4d30e7fc9aaf5249b6828c95a24364fd48a1128914289250328a1f522ce5663f"; // Tu API Key

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Uso: java ObtenerPdfConSerpApi <título>");
            System.exit(1);
        }

        String tituloArticulo = args[0];
        System.out.println("Título del artículo: " + tituloArticulo); // Mostrar el título del artículo

        ObtenerPdfConSerpApi obtPdf = new ObtenerPdfConSerpApi();

        try {
            String jsonRespuesta = obtPdf.obtenerJSON(tituloArticulo);

            String link = obtenerPrimerLink(jsonRespuesta);
            if (link != null) {
                System.out.println("El primer enlace encontrado es: " + link);
            } else {
                System.out.println("No se encontraron enlaces en el JSON obtenido.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String obtenerJSON(String tituloArticulo) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String tituloEscapado = URLEncoder.encode(tituloArticulo, StandardCharsets.UTF_8);

        String urlSerpApi = "https://serpapi.com/search.json?engine=google_scholar&api_key=" + API_KEY + "&q=" + tituloEscapado;

        Request respuestaBuilder = new Request.Builder()
                .url(urlSerpApi)
                .build();

        try (Response r = client.newCall(respuestaBuilder).execute()) {
            if (!r.isSuccessful()) throw new IOException("Fallo " + r);

            return r.body().string();
        }
    }

    public static String obtenerPrimerLink(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray organicResults = jsonObject.getJSONArray("organic_results");
            if (organicResults.length() > 0) {
                JSONObject firstResult = organicResults.getJSONObject(0);
                JSONArray resources = firstResult.getJSONArray("resources");
                if (resources.length() > 0) {
                    JSONObject firstResource = resources.getJSONObject(0);
                    return firstResource.getString("link");
                }
            }
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
