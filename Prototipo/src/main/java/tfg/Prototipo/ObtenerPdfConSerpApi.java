package tfg.Prototipo;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ObtenerPdfConSerpApi {
    private final String apiKey;

    public ObtenerPdfConSerpApi(String apiKey) {
        this.apiKey = apiKey;
    }

    public String obtenerJSON(String tituloArticulo) throws IOException {
        OkHttpClient client = new OkHttpClient();//creo insatncia de conexion cliente

        //Obviar los caracteres especiales del título del artículo
        String tituloEscapado = URLEncoder.encode(tituloArticulo, StandardCharsets.UTF_8);

        //Construir la URL para buscar con SerpApi el título del articulo
        String urlSerpApi = "https://serpapi.com/search.json?engine=google_scholar&api_key=" + apiKey + "&q=" + tituloEscapado;

        //Establecer solicitud HTTP
        Request respuestaBuilder = new Request.Builder()//instancia
                .url(urlSerpApi)//con serpapi
                .build();//construir

        try (Response r = client.newCall(respuestaBuilder).execute()) {
            if (!r.isSuccessful()) throw new IOException("Fallo " + r);//control de errores

            return r.body().string();//Obtengo JSON
            
        }
    }

    public static void main(String[] args) {
        String apiKey = "4d30e7fc9aaf5249b6828c95a24364fd48a1128914289250328a1f522ce5663f";//mi apiKey
        ObtenerPdfConSerpApi obtPdf = new ObtenerPdfConSerpApi(apiKey);//instancia

        try {
            String tituloArticulo = "Frameworks Generate Domain-Specific Languages: A Case Study in the Multimedia Domain"; //Titulo del articulo
            String jsonRespuesta = obtPdf.obtenerJSON(tituloArticulo);//Obtener el JSON del articulo

            System.out.println("JSON obtenido:");
            System.out.println(jsonRespuesta);//se muestra
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
