package es.upm.grise.checkurl;

public class RunEntireFlow {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Se debe proporcionar una URL como argumento.");
            System.exit(1);
        }

        String url = args[0]; //Obtengo la URL de los argumentos
        System.out.println("<<<<<<<<<<LeerXml>>>>>>>>>>>");
        LeerXml.main(new String[]{url});

        String[] titulosArticulos = LeerXml.obtenerTitulos(url); //Guardo los títulos de los artículos en un array
        System.out.println("<<<<<<<<<<ObtenerPdfConSerpApi>>>>>>>>>>");
        String[][] titulosYEnlaces = ObtenerPdfConSerpApi.main(titulosArticulos);//Guardo en String [][] los titulos y los enlaces

        System.out.println("<<<<<<<<<<DescargarPdfCache>>>>>>>>>>");
        String[][] rutasYTitulos = DescargarPdfCache.main(titulosYEnlaces);//Guardo en String [][] las rutas de descarga y los titulos
        
        System.out.println("<<<<<<<<<<MostrarYComprobarEnlaces>>>>>>>>>>");
        MostrarYComprobarEnlaces.main(rutasYTitulos);
    }
}


//mvn exec:java -Dexec.mainClass="es.upm.grise.checkurl.RunEntireFlow" -Dexec.args="https://dblp.org/search/publ/api?q=toc%3Adb/journals/tse/tse37.bht%3A&h=1000&format=xml"
//https://dblp.org/search/publ/api?q=conf/icse/2024/workshop&h=3&format=xml

