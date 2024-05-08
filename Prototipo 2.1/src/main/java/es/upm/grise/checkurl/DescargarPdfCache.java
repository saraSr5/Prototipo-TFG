package es.upm.grise.checkurl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DescargarPdfCache {
    private static final String DIR_CACHE = "./cache/"; //Ruta de los PDFs
    private static final String PDF_EXTENSION = "pdf"; //Extensión final de los archivos PDF

    public static String[][] main(String[][] titulosYEnlaces) {
    List<String[]> rutasYTitulos = new ArrayList<>();

    if (titulosYEnlaces.length == 0) {
        System.err.println("No se han proporcionado títulos con enlaces.");
        System.exit(1);
    }

    //Inicializar la base de datos
    ConexionBaseDeDatos.initializeDatabase();

    for (String[] tituloYEnlace : titulosYEnlaces) {
        String tituloArticulo = tituloYEnlace[0];
        String url = tituloYEnlace[1];

        try {
            File cacheDirectory = new File(DIR_CACHE);
            if (!cacheDirectory.exists()) {
                cacheDirectory.mkdir();
            } else {
                if (!cacheDirectory.isDirectory()) {
                    System.err.println("La ruta de caché no es un directorio válido: " + DIR_CACHE);
                    continue; //Salto este título y continuo con el siguiente
                }
            }

            //Miro si el nombre del PDF ya existe en la base de datos
            if (!ConexionBaseDeDatos.isNombrePDFExistente(tituloArticulo)) {
                String destino = DIR_CACHE + limpiarNombreArticulo(tituloArticulo) + "." + PDF_EXTENSION; // Construir la ruta de descarga del PDF
                if (!archivoExiste(destino)) { //Si el archivo no existe, lo descargo
                    descargarPDF(url, destino);
                    System.out.println("\"" + destino + "\"" + " \"" + tituloArticulo + "\"");

                    //Inserto el nombre del PDF en la tabla de nombres
                    ConexionBaseDeDatos.insertNombrePDF(tituloArticulo);
                    System.err.println(
                            "Nombre de PDF insertado en la base de datos: " + "\"" + tituloArticulo + "\"");

                    //Agrego la ruta y el título a la salida
                    rutasYTitulos.add(new String[]{destino, tituloArticulo});
                } else {
                    System.err.println("El PDF ya ha sido descargado previamente: " + "\"" + tituloArticulo + "\"");
                }
            } else {
                System.err.println("El PDF ya ha sido descargado previamente: " + "\"" + tituloArticulo + "\"");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Convierto salida en String[][]
    return rutasYTitulos.toArray(new String[0][]);
}

    public static void descargarPDF(String url, String destino) throws IOException { 
        URI pdfURI = null;
        try {
            pdfURI = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        URL pdfURL = pdfURI.toURL();

        try (BufferedInputStream entrada = new BufferedInputStream(pdfURL.openStream());
             FileOutputStream fichero = new FileOutputStream(destino)) {

            byte[] buffer = new byte[1024];
            int bytesLeidos;

            while ((bytesLeidos = entrada.read(buffer, 0, 1024)) != -1) {
                fichero.write(buffer, 0, bytesLeidos);
            }
        } catch (IOException e) {
            System.err.println("Error al descargar el PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //Función para comprobar si el archivo existe en la ruta especificada de descarga
    public static boolean archivoExiste(String ruta) {
        Path direccion = Paths.get(ruta);
        return Files.exists(direccion);
    }

    //Función para que el nombre del PDF no cause problemas
    public static String limpiarNombreArticulo(String nombre) {
        return nombre.replaceAll("[<>:\"/\\|?*]", "_");
    }
}
