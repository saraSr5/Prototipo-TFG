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
        if (args.length != 2) {
            System.out.println("Uso: java MostrarYComprobarEnlaces <ruta del PDF> <DOI>");
            return;
        }

        String rutaPDF = args[0]; // Obtener la ruta del PDF desde los argumentos de la línea de comandos
        String doi = args[1]; // Obtener el DOI del artículo desde los argumentos de la línea de comandos

        try (PDDocument documento = PDDocument.load(new File(rutaPDF))) {
            PDFTextStripper textStripper = new PDFTextStripper();
            String contenido = textStripper.getText(documento);

            // Guardar la URL y el resultado de la verificación en la base de datos
            guardarVerificacionEnBaseDeDatos(contenido, doi);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void guardarVerificacionEnBaseDeDatos(String texto, String doi) {
        String simbolos = "\\b(?:https?|ftp):\\/\\/[-a-zA-Z0-9+&@#\\/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#\\/%=~_|]";
        Pattern p = Pattern.compile(simbolos);
        Matcher matcher = p.matcher(texto);

        while (matcher.find()) {
            String enlace = matcher.group();
            int codigoRespuesta = verificarExistencia(enlace);
            
            // Insertar el DOI, la URL del PDF y el código de respuesta en la base de datos
            ConexionBaseDeDatos.insertPDF(doi, enlace, codigoRespuesta);
            break; // Solo necesitamos la primera URL encontrada en el PDF
        }
    }

    private static int verificarExistencia(String enlace) {
        try {
            Connection.Response respuesta = Jsoup.connect(enlace)
                    .followRedirects(true)
                    .timeout(1000000)
                    .method(Connection.Method.HEAD)
                    .execute();

            return respuesta.statusCode();
        } catch (IOException e) {
            e.printStackTrace();
            return -1; // Se puede elegir un valor especial para indicar un error
        }
    }
}
