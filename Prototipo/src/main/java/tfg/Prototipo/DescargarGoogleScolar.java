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
        String articleUrl = "https://doi.org/10.1109/TSE.2010.48";

        try {
            List<String> pdfLinks = extractPdfLinksFromArticle(articleUrl);
            System.out.println("Enlaces de PDF encontrados:");
            for (String pdfLink : pdfLinks) {
                System.out.println(pdfLink);
                descargarPdfDesdeEnlace(pdfLink);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> extractPdfLinksFromArticle(String articleUrl) throws IOException {
        Document articlePage = Jsoup.connect(articleUrl).get();
        Elements eeElements = articlePage.select("ee");

        List<String> pdfLinks = new ArrayList<>();
        for (Element eeElement : eeElements) {
            String link = eeElement.text();
            pdfLinks.add(link);
        }

        return pdfLinks;
    }

    private static void descargarPdfDesdeEnlace(String pdfLink) {
        String destino = "C:\\Users\\Admin\\Desktop\\PDF descargado\\miPdf.pdf";

        try {
            DescargarPdf.descargarPDF(pdfLink, destino);
            System.out.println("PDF descargado correctamente en: " + destino);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
