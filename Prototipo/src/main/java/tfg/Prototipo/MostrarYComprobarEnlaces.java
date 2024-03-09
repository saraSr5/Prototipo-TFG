package tfg.Prototipo;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class MostrarYComprobarEnlaces {

    public static void main(String[] args) {
        String rutaPDF = "C:/Users/Admin/Desktop/Ejemplo para descargar pdf/Frameworks_Generate_Domain-Specific_Languages_A_Case_Study_in_the_Multimedia_Domain.pdf";

        try (PDDocument document = PDDocument.load(new File(rutaPDF))) {
            PDFTextStripper textStripper = new PDFTextStripper();
            String contenido = textStripper.getText(document);

            // Obtener enlaces y verificar existencia
            mostrarEnlacesYVerificarExistencia(contenido);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void mostrarEnlacesYVerificarExistencia(String texto) {
        // Expresión regular para encontrar enlaces
        String regex = "\\b(?:https?|ftp):\\/\\/[-a-zA-Z0-9+&@#\\/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#\\/%=~_|]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(texto);

        System.out.println("Enlaces encontrados y verificados:");

        while (matcher.find()) {
            String enlace = matcher.group();
            System.out.println(enlace);

            // Verificar existencia del enlace
            if (verificarExistencia(enlace)) {
                System.out.println("  El enlace existe.");
            } else {
                System.out.println("  El enlace no existe o no se pudo verificar.");
            }
        }
    }

    private static boolean verificarExistencia(String enlace) {
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
