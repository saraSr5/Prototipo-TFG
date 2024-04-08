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
    private static final String DIR_CACHE = "C:\\Users\\Admin\\Desktop\\PDF descargado\\";
    private static final String PDF_EXTENSION = "pdf";

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Uso: java DescargarPdfCache <url del PDF> <título del artículo>");
            return;
        }

        String url = args[0];
        String tituloArticulo = args[1];
        
        // Inicializar la base de datos
        ConexionBaseDeDatos.initializeDatabase();

        try {
            // Verificar si el nombre del PDF ya existe en la base de datos
            if (!ConexionBaseDeDatos.isNombrePDFExistente(tituloArticulo)) {
                String destino = DIR_CACHE + limpiarNombreArticulo(tituloArticulo) + PDF_EXTENSION;
                if (!archivoExiste(destino)) {
                    descargarPDF(url, destino);
                    System.out.println("PDF descargado correctamente en: " + destino);
                    
                    // Insertar el nombre del PDF en la tabla de nombres
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

    public static boolean archivoExiste(String ruta) {
        Path direccion = Paths.get(ruta);
        return Files.exists(direccion);
    }

    public static String limpiarNombreArticulo(String nombre) {
        return nombre.replaceAll("[<>:\"/\\|?*]", "_");
    }
}
