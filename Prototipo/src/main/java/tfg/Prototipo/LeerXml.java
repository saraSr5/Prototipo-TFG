package tfg.Prototipo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class LeerXml {

    public static void main(String[] args) {
        String url = "https://dblp.org/search/publ/api?q=toc%3Adb/journals/tse/tse37.bht%3A&h=1000&format=xml";

        try {
            Document document = Jsoup.connect(url).get();

            // Encuentra todas las etiquetas <ee>
            Elements eeElements = document.select("ee");

            // Itera sobre las etiquetas <ee> y muestra los enlaces
            for (Element eeElement : eeElements) {
                String link = eeElement.text();
                System.out.println("Enlace: " + link);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
