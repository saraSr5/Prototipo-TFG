package tfg.Prototipo;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class DescargarPdf {
    public static void main(String[] args) {
        String url = "https://ieeexplore.ieee.org/stamp/stamp.jsp?arnumber=5441292&casa_token=KhOx_Pqq9AUAAAAA:_CoOzEik5EQjSFRUoY3L1l5YqjiUb_qf_IFaNoyNfTIVgjKC__Rm5cRyVI86WnvVTCBxIOBj9p8&tag=1";// url
                                                                                                                // del
                                                                                                                // pdf
        String destino = "C:\\Users\\Admin\\Desktop\\PDF descargado\\miPdf.pdf"; // destino donde se descargara el pdf

        try {
            descargarPDF(url, destino);
            System.out.println("PDF descargado correctamente en: " + destino);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static void descargarPDF(String url, String destino) throws IOException, URISyntaxException {
        URI pdfURI = new URI(url);// creo uri
        URL pdfURL = pdfURI.toURL();// lo cambio a url

        try (BufferedInputStream entrada = new BufferedInputStream(pdfURL.openStream());
                FileOutputStream fichero = new FileOutputStream(destino)) {

            byte[] buffer = new byte[1024];
            int bytesLeidos;

            while ((bytesLeidos = entrada.read(buffer, 0, 1024)) != -1) {
                fichero.write(buffer, 0, bytesLeidos);
            }
        }
    }
}