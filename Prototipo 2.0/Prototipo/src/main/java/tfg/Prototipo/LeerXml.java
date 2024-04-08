package tfg.Prototipo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class LeerXml {

    public static void main(String[] args) {
        if (args.length != 1) {//si hay mas de un argumento
            System.out.println("Error: solo debe tener un argumento");
            return;
        }

        String url = args[0];//la url de la linea de comandos

        try {
            Document documento = Jsoup.connect(url).get();//guardo en documento el xml

            Elements titleElementos = documento.select("title");//busco en el documento xml las etiquetas <ee> ya que es donde se encuentran las url de los articulos

            for (Element titleElemento : titleElementos) {//itero sobre las etiquetas <ee>
                String link = titleElemento.text();//extraigo el texto del elemento
                System.out.println("Titulo: " + link);//lo muestro por pantalla
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
