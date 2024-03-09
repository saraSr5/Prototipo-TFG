package tfg.Prototipo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class DescargarGoogleScolar {

    public static void main(String[] args) {
        String urlArticulo = "https://doi.org/10.1109/TSE.2010.48";//meto la url del articulo que quiero descargar

        try {
            List<String> pdfLinks = extraerLinksPdfDeArticulo(urlArticulo);//guardo los links de los pdfs de la url del articulo llamando a la funcion extraerLinksPdfDeArticulo
            System.out.println("Enlaces de PDF encontrados:");//me muestra los enlaces de los pdfs
            for (String pdfLink : pdfLinks) {//itero sobre los links de los pdfs que he encontrado
                System.out.println(pdfLink);//los muestro
                descargarPdfDesdeEnlace(pdfLink);//llamo a la funcion para descargar el pdf
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//Con esta funcion extraigo los links de los pdf que hay en los articulos
    private static List<String> extraerLinksPdfDeArticulo(String articuloUrl) throws IOException {
        Document paginaArt = Jsoup.connect(articuloUrl).get();//con Jsoup me conecto con la pagina del articulo
        Elements eeElementos = paginaArt.select("ee");//escogo los elementos ee

        List<String> pdfLinks = new ArrayList<>();//creo un ArrayList con los links de los pdf
        for (Element eeElemento : eeElementos) {//itero sobre los elementos ee
            String link = eeElemento.text();//meto en la variable link el link que obtuve
            pdfLinks.add(link);//lo meto en el ArrayList de links de pdfs
        }

        return pdfLinks;//devuelvo el ArrayList de pdfs
    }
//Con esta funcion descargo el pdf desde el enlace
    private static void descargarPdfDesdeEnlace(String pdfLink) {
        String destino = "C:\\Users\\Admin\\Desktop\\PDF descargado\\miPdf.pdf";//pongo ruta donde quiero que se descargue

        try {
            DescargarPdf.descargarPDF(pdfLink, destino);//llamo a la clase DescargarPdf para descargarlo
            System.out.println("PDF descargado correctamente en: " + destino);//lo muestro por pantalla el destino donde se descargo el pdf
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
