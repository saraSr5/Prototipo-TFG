package tfg.Prototipo;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class MiroEnlace {

    public static void main(String[] args) {
        String enlace = "http://www.clam-project.org";

        if (verificarExistencia(enlace)) {
            System.out.println("El enlace existe.");
        } else {
            System.out.println("El enlace no existe o no se pudo verificar.");
        }
    }

    public static boolean verificarExistencia(String enlace) {
        try {
            Connection.Response response = Jsoup.connect(enlace)
                    .followRedirects(true) // Seguir redirecciones
                    .timeout(1000000) // Establecer tiempo de espera en milisegundos
                    .method(Connection.Method.HEAD) // Utilizar el método HEAD
                    .execute();

            // Obtener el código de respuesta
            int responseCode = response.statusCode();

            // Verificar si la respuesta es 2xx (éxito)
            return (responseCode >= 200 && responseCode < 300);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
