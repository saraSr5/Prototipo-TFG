package tfg.Prototipo;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DescargarPdfCache {
    private static final String DIR_CACHE = "C:\\Users\\Admin\\Desktop\\PDF descargado\\";//La ruta de los pdfs
    private static final String PDF_EXTENSION = "pdf";//la extension final

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Error tienen que ser 2 argumentos");
            return;
        }

        String url = args[0];//cojo la url de descarga del pdf de la linea de comandos
        String tituloArticulo = args[1];//cojo el titulo del articulo de la linea de comandos
        
        //Inicializo la base de datos
        ConexionBaseDeDatos.initializeDatabase();

        try {
            //Miro si el nombre del pdf ya existe en la base de datos
            if (!ConexionBaseDeDatos.isNombrePDFExistente(tituloArticulo)) {
                String destino = DIR_CACHE + limpiarNombreArticulo(tituloArticulo) + PDF_EXTENSION;//Construyo la ruta donde va a ser descargado
                if (!archivoExiste(destino)) {//Si no existe lo descargo
                    descargarPDF(url, destino);
                    System.out.println("PDF descargado correctamente en: " + destino);
                    
                    //Inserto el nombre del PDF en la tabla de nombres
                    ConexionBaseDeDatos.insertNombrePDF(tituloArticulo);
                    System.out.println("Nombre de PDF insertado en la base de datos.");
                } else {
                    System.out.println("El PDF ya ha sido descargado previamente.");
                }
            } else {
                System.out.println("El PDF ya ha sido descargado previamente.");
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static void descargarPDF(String url, String destino) throws IOException, URISyntaxException {
        URI pdfURI = new URI(url);
        URL pdfURL = pdfURI.toURL();

        try (BufferedInputStream entrada = new BufferedInputStream(pdfURL.openStream());
             FileOutputStream fichero = new FileOutputStream(destino)) {

            byte[] buffer = new byte[1024];
            int bytesLeidos;

            while ((bytesLeidos = entrada.read(buffer, 0, 1024)) != -1) {
                fichero.write(buffer, 0, bytesLeidos);
            }
        }
    }
//Funcion para mirar si el archivo existe en la ruta especificada de descarga
    public static boolean archivoExiste(String ruta) {
        Path direccion = Paths.get(ruta);
        return Files.exists(direccion);
    }
//Funcion para que el nombre del pdf no de problemas 
    public static String limpiarNombreArticulo(String nombre) {
        return nombre.replaceAll("[<>:\"/\\|?*]", "_");
    }
}
