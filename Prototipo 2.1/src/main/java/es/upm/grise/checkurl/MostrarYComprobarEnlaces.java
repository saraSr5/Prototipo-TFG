package es.upm.grise.checkurl;

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

    public static void main(String[][] args) {
        if (args.length == 0) {
            System.err.println("No se ha proporcionado ningún path");
            System.exit(1);
        }

        if (args[0].length % 2 != 0) {
            System.err.println("Algun path no tiene asociado un título, o viceversa");
            System.exit(1);
        }

        for (int i = 0; i < args[0].length; i += 2) {
            String rutaPDF = args[0][i]; //Cojo la ruta del PDF
            String doi = args[0][i + 1]; //Cojo el DOI del artículo 

            try (PDDocument documento = PDDocument.load(new File(rutaPDF))) {
                PDFTextStripper textStripper = new PDFTextStripper();
                String contenido = textStripper.getText(documento);

                //Guardo la verificación de la URL en la base de datos
                guardarVerificacionEnBaseDeDatos(contenido, doi);

            } catch (IOException e) {
                System.err.println(e.getMessage() + "\"" + rutaPDF + "\"");
            }
        }
    }

    private static void guardarVerificacionEnBaseDeDatos(String texto, String doi) {
        String simbolos = "\\b(?:https?|ftp):\\/\\/[-a-zA-Z0-9+&@#\\/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#\\/%=~_|]";
        Pattern p = Pattern.compile(simbolos);
        Matcher matcher = p.matcher(texto);

        while (matcher.find()) {
            String enlace = matcher.group();
            int codigoRespuesta = verificarExistencia(enlace);
            ConexionBaseDeDatos.insertPDF(doi, enlace, codigoRespuesta);
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
            if (e instanceof HttpStatusException) {
                HttpStatusException httpException = (HttpStatusException) e;
                return httpException.getStatusCode();
            } else {
                System.err.println(e.getMessage() + "\"" + enlace + "\"");
                return 0;
            }
        }
    }
}
