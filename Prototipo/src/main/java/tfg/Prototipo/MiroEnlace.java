package tfg.Prototipo;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class MiroEnlace {

    public static void main(String[] args) {
        String enlace = "http://www.clam-project.org";//este es el enlace del pdf a mirar

        if (verificarExistencia(enlace)) {//llamo a verificarExistencia para mirar si existe el enlace
            System.out.println("El enlace existe.");//si exsite
        } else {
            System.out.println("El enlace no existe o no se pudo verificar.");//si no existe o hubo algún problema
        }
    }
//Con esta funcion verifico si existen los enlaces de las referencias de los pdfs
    public static boolean verificarExistencia(String enlace) {
        try {
            Connection.Response r = Jsoup.connect(enlace)//conecto con el enlace con Jsoup
                    .followRedirects(true) //Sigo las redirecciones
                    .timeout(1000000) //Establezco un tiempo de espera en ms
                    .method(Connection.Method.HEAD) //Utilizo el metodo head
                    .execute();//lo ejecuto

            int rCodigo = r.statusCode();//Obtengo el codigo de respuesta que me devuelve el servidor

            //Si la respuesta del servidor esta entre 200 y 300 -> 2xx entonces es positiva
            return (rCodigo >= 200 && rCodigo < 300);
        } catch (IOException e) {
            e.printStackTrace();
            return false;// si no es negativa
        }
    }
}
