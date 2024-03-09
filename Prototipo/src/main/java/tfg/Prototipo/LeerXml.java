package tfg.Prototipo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class LeerXml {

    public static void main(String[] args) {
        String url = "https://dblp.org/search/publ/api?q=toc%3Adb/journals/tse/tse37.bht%3A&h=1000&format=xml";//url del archivo xml

        try {
            Document documento = Jsoup.connect(url).get();//guardo en documento el xml

            Elements eeElementos = documento.select("ee");//busco en el documento xml las etiquetas <ee> ya que es donde se encuentran las url de los articulos

            for (Element eeElemento : eeElementos) {//itero sobre las etiquetas <ee>
                String link = eeElemento.text();//extraigo el texto del elemento
                System.out.println("Enlace: " + link);//lo muestro por pantalla
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
