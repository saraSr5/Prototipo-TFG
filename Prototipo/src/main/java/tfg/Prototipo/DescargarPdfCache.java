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
    private static final String DIR_CACHE = "C:\\Users\\Admin\\Desktop\\PDF descargado\\";//ruta de la cache local
    private static final String PDF = ".pdf";//terminacion pdf

    public static void main(String[] args) {
        String url = "https://re.public.polimi.it/bitstream/11311/573236/1/tse.pdf";//url del articulo a descargar
        String tituloArticulo = "Self-supervising BPEL Processes";//titulo del articulo descargado
        
        try {
            String destino = DIR_CACHE + limpiarNombreArticulo(tituloArticulo) + PDF;;//creo la ruta de destino
            if (!archivoExiste(destino)) {//si no existe en el destino
                descargarPDF(url, destino);//descargo el pdf
                System.out.println("PDF descargado correctamente en: " + destino);//muestro donde se ha descargado
            } else {
                System.out.println("El PDF ya ha sido descargado previamente.");//si ya existia lo indico y no lo descargo
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static void descargarPDF(String url, String destino) throws IOException, URISyntaxException {
        URI pdfURI = new URI(url);//creo URI
        URL pdfURL = pdfURI.toURL();//lo paso a URL

        try (BufferedInputStream entrada = new BufferedInputStream(pdfURL.openStream());//meto la url del pdf
             FileOutputStream fichero = new FileOutputStream(destino)) {//la salida en forma de fichero (articulo) en el destino

            byte[] buffer = new byte[1024];//creo un buffer
            int bytesLeidos;//contador para los bytes que se han leido

            while ((bytesLeidos = entrada.read(buffer, 0, 1024)) != -1) {//leo los bytes del articulo original
                fichero.write(buffer, 0, bytesLeidos);//los voy escribiendo en el que creo
            }
        }
    }
//Metodo para comprobar si el articulo existe y asi no volverlo a descargar
    public static boolean archivoExiste(String ruta) {
        Path direccion = Paths.get(ruta);
        return Files.exists(direccion);
    }
//Metodo para poder utilizar el nombre del articulo para construir la ruta    
    public static String limpiarNombreArticulo(String nombre) {
        return nombre.replaceAll("[<>:\"/\\|?*]", "_");
    }
}
