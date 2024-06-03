package es.upm.grise.checkurl;

public class RunEntireFlow {

	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("Se debe proporcionar una URL como argumento.");
			System.exit(1);
		}

		String url = args[0]; // Obtengo la URL de los argumentos

		System.err.println();
		System.err.println();
		System.err.println();
		System.err.println("Reading titles from DBML file/URL");
        System.err.println("==================================================================================");
		System.err.println();
		// Guardo los títulos de los artículos en un array
		String[] titulosArticulos = LeerXml.obtenerTitulos(url);
		System.err.println(titulosArticulos.length + " titles read");
		System.err.println();
		System.err.println();
		System.err.println();
		System.err.println("Getting links from SerAPI");
        System.err.println("==================================================================================");
		System.err.println();
		// Guardo en String [][] los titulos y los enlaces
		String[][] titulosYEnlaces = ObtenerPdfConSerpApi.obtenerTodosLosEnlacesPDF(titulosArticulos);
		System.err.println(titulosYEnlaces.length + " titles read");
		System.err.println();
		System.err.println();
		System.err.println();
		System.err.println("Downloading PDFs to local cache and populating DB");
        System.err.println("==================================================================================");
		System.err.println();
		// Guardo en String [][] las rutas de descarga y los titulos
		String[][] rutasYTitulos = DescargarPdfCache.descargarTodosLosPDFs(titulosYEnlaces);
		System.err.println(rutasYTitulos.length + " PDF files downloaded");
		System.err.println();
		System.err.println();
		System.err.println();
		System.err.println("Checking URLs");
        System.err.println("==================================================================================");
		System.err.println();
		int numeroTotalDelinks = MostrarYComprobarEnlaces.extraerTodosLosEnlaces(rutasYTitulos);
		System.err.println();
		System.err.println(numeroTotalDelinks + " links found");

	}
}


//ATENCIÓN
//Una vez ejecutada esta clase se debe ejecutar ClasificarURL.java por separado 


//mvn exec:java -Dexec.mainClass="es.upm.grise.checkurl.RunEntireFlow" -Dexec.args="https://dblp.org/search/publ/api?q=toc%3Adb/journals/tse/tse37.bht%3A&h=1000&format=xml"
//https://dblp.org/search/publ/api?q=conf/icse/2024/workshop&h=3&format=xml