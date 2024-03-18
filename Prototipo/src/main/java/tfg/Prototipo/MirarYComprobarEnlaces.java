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
        String rutaPDF = "C:/Users/Admin/Desktop/PDF descargado/articulo.pdf";

        try (PDDocument documento = PDDocument.load(new File(rutaPDF))) {//cargo documento
            PDFTextStripper textStripper = new PDFTextStripper();//Miro el documento
            String contenido = textStripper.getText(documento);//Guardo en la variable contenido el contenido del pdf

            //Obtengo los enlaces y verifico si existen o no
            mostrarEnlacesYVerificarExistencia(contenido);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void mostrarEnlacesYVerificarExistencia(String texto) {
        
        String simbolos = "\\b(?:https?|ftp):\\/\\/[-a-zA-Z0-9+&@#\\/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#\\/%=~_|]";//simbolos para los enlaces
        Pattern p = Pattern.compile(simbolos);//patron con los simbolos
        Matcher matcher = p.matcher(texto);//igualador con el texto

        System.out.println("Enlaces encontrados y verificados:");

        while (matcher.find()) {//se busca
            String enlace = matcher.group();
            System.out.println(enlace);//se muestra por pantalla el enlace encontrado

            //Miro si el enlace encontrado existe
            if (verificarExistencia(enlace)) {
                System.out.println("El enlace existe.");
            } else {
                System.out.println("El enlace no existe o no se pudo verificar.");
            }
        }
    }

    private static boolean verificarExistencia(String enlace) {
        try {
            Connection.Response respuesta = Jsoup.connect(enlace)//conexion con el enlace
                    .followRedirects(true)//Sigue las redirecciones
                    .timeout(1000000)//Tiempo de espera de respuesta
                    .method(Connection.Method.HEAD)//HEAD
                    .execute();//Ejecutar

            //Obtengo código de respuesta del servidor
            int codigoResp = respuesta.statusCode();

            //Verifico si la respuesta está entre 200 y 300 ya que eso es éxito 
            return (codigoResp >= 200 && codigoResp < 300);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
