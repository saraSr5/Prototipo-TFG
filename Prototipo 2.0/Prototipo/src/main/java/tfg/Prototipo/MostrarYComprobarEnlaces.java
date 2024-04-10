package tfg.Prototipo;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

public class MostrarYComprobarEnlaces {

    public static void main(String[] args) {
        if (args.length != 2) {//Los argumentos tienen que ser 2
            System.out.println("Error tienen que ser 2 argumentos");
            return;
        }

        String rutaPDF = args[0]; //Cojo la ruta del pdf desde la línea de comandos
        String doi = args[1]; //Cojo el doi del artículo desde la línea de comandos

        try (PDDocument documento = PDDocument.load(new File(rutaPDF))) {
            PDFTextStripper textStripper = new PDFTextStripper();//instancia de testStripper
            String contenido = textStripper.getText(documento);//guardo en contenido el texto pdf

            //Guardo la verificacion de la url en la base de datos
            guardarVerificacionEnBaseDeDatos(contenido, doi);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void guardarVerificacionEnBaseDeDatos(String texto, String doi) {
        String simbolos = "\\b(?:https?|ftp):\\/\\/[-a-zA-Z0-9+&@#\\/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#\\/%=~_|]";//para las urls
        Pattern p = Pattern.compile(simbolos);//Meto los simbolos en un pattern para mirar la coincidencia y encontrar urls
        Matcher matcher = p.matcher(texto);//busco las urls en el pdf con matcher

        while (matcher.find()) {//busco por el pdf las urls
            String enlace = matcher.group();
            int codigoRespuesta = verificarExistencia(enlace);//Miro si el enlace existe
            //Inserto el doi, la url y el codigo de respuesta en la base de datos
            ConexionBaseDeDatos.insertPDF(doi, enlace, codigoRespuesta);
            
        }
    }

    private static int verificarExistencia(String enlace) {
        try {
            Connection.Response respuesta = Jsoup.connect(enlace)//conecto con el enlace encontrado para ver si existe
                    .followRedirects(true)
                    .timeout(1000000)
                    .method(Connection.Method.HEAD)
                    .execute();

            return respuesta.statusCode();//Codigo del servidor cuando no falla
        } catch (IOException e) {
            if (e instanceof HttpStatusException) {
                HttpStatusException httpException = (HttpStatusException) e;
                return httpException.getStatusCode();//Cojo el codigo del servidor cuando falla
            } else {
                e.printStackTrace();
                return 0;
            }
        }
    }
}
