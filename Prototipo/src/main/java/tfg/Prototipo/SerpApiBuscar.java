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
        String url = "https://dblp.org/search/publ/api?q=toc%3Adb/journals/tse/tse37.bht%3A&h=1000&format=xml";

        try {
            Document document = Jsoup.connect(url).get();

            // Encuentra todas las etiquetas <ee>
            Elements eeElements = document.select("ee");

            // Itera sobre las etiquetas <ee> y muestra los enlaces
            for (Element eeElement : eeElements) {
                String link = eeElement.text();
                System.out.println("Enlace: " + link);

                if (esEnlacePDF(link)) {
                    System.out.println("Enlace PDF encontrado: " + link);

                    // Realizar búsqueda en Google Scholar
                    buscarEnGoogleScholar(link);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean esEnlacePDF(String enlace) {
        return enlace.toLowerCase().endsWith(".pdf");
    }

    private static void buscarEnGoogleScholar(String enlaceArticulo) {
        try {
            URL url = new URL("https://scholar.google.com/scholar?hl=en&q=" + enlaceArticulo);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Configuración de la solicitud HTTP
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            // Realizar la solicitud HTTP
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Leer la respuesta
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                // Agrega aquí la lógica para analizar la respuesta y descargar el PDF si está disponible
                System.out.println("Respuesta de Google Scholar: " + response.toString());
            } else {
                System.err.println("Error en la solicitud a Google Scholar. Código de respuesta: " + responseCode);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
