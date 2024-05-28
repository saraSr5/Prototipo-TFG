package es.upm.grise.checkurl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

public class MostrarYComprobarEnlaces {
	
	static int CONTEXT_SIZE = 100;
	static String MARKER = ">>>>>>>";
		
	public static void main(String[] args) throws SQLException {
		
		if (args.length == 0) {
			System.err.println("No se ha proporcionado ninguna ruta");
			System.exit(1);
		}

		int primeraRuta = 0;
		boolean forzarRegeneracionTexto = false;
		boolean forzarBuscarEnlaces = false;
		int numeroTotalEnlaces = 0;
		
	    if (args.length %2 !=  0) {
	    	
	    	if(args[0].equals("--regenerate-text")) {
	    		primeraRuta = 1;
	    		forzarRegeneracionTexto = true;
	            System.out.println();
	            System.out.println("Se vuelve transformar el PDF a texto, se buscan de nuevo los enlaces y se comprueba su accesibilidad");
	            System.out.println();
	    	}
	    	
	    	if(args[0].equals("--force-link-search")) {
	    		primeraRuta = 1;
	    		forzarBuscarEnlaces = true;
	            System.out.println();
	            System.out.println("Se analizan de nuevo los enlaces de los ficheros, usando el texto existente. Se comprueba su accesibilidad");
	            System.out.println();
	    	}
	    	
	    	if(args[0].equals("--use-db-only")) {
	            System.out.println();
	            System.out.println("Se comprueba la accesibilidad de los enlaces que están en la base de datos (no se utiliza el texto)");
	            System.out.println();
	            
	            List<String> linkList = ConexionBaseDeDatos.getAllURLs();
	    		
	    		for(String link : linkList) {
	    			
	                String finalURL = obtenerEnlaceFinal(link);
	                int codigoRespuesta = verificarExistencia(finalURL);
	                
	                System.out.println("Link identificado: " + link + " (" + codigoRespuesta + ")");
	                
	    			ConexionBaseDeDatos.updateResponseCode(finalURL, codigoRespuesta);
	    			
	    			numeroTotalEnlaces++;

	    		}
	            
	    	} else {
	    		
	    		java.util.logging.Logger.getLogger("org.apache.pdfbox").setLevel(java.util.logging.Level.SEVERE);
	    		

	    		for (int i = primeraRuta; i < args.length; i += 2) {
	    			String rutaPDF = args[i]; //Cojo la ruta del PDF
	    			String doi = args[i + 1]; //Cojo el DOI del artículo 
	    			
	    			numeroTotalEnlaces += extraeEnlacesDeArticulo(doi, rutaPDF, forzarRegeneracionTexto, forzarBuscarEnlaces);
	    		}
	    		
	    	}
	    	
	    }
		
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println(numeroTotalEnlaces + " links found");

	}
	
	public static int extraerTodosLosEnlaces(String[][] rutasYTitulos) {

	    if (rutasYTitulos == null) {
	        System.err.println("No se han proporcionado rutas y títulos de artículos");
	        System.exit(1);
	    }
	    	    
	    java.util.logging.Logger.getLogger("org.apache.pdfbox").setLevel(java.util.logging.Level.SEVERE);
		
		int numeroTotalEnlaces = 0;
		
	    for (String[] rutaYTitulo : rutasYTitulos) {
	    	
	    	if(rutaYTitulo == null) {
	    		
	    		continue;
	    		
	    	}
	    	
	        String rutaPDF = rutaYTitulo[0];
	    	String doi = rutaYTitulo[1];
			numeroTotalEnlaces += extraeEnlacesDeArticulo(doi, rutaPDF, false, false);
		}
	    
		return numeroTotalEnlaces;
	}

	private static int extraeEnlacesDeArticulo(String doi, String rutaPDF, boolean forzarRegeneracionTexto, boolean forzarBuscarEnlaces) {
		//Comprobamos si la version TXT del documento ya existe en la cache
		String nombreSinExtension = rutaPDF.substring(0, rutaPDF.length() - 3);
		String nombreConExtensionTXT = nombreSinExtension + "txt";
		String paperText = "";
		int numeroDeEnlaces = 0;
		
		//Eliminamos los enlaces existentes en la BD
		ConexionBaseDeDatos.EliminarEnlacesDeUnArticulo(doi);

		// Si no hay un fichero de texto asociado, el PDF corresponde con un nuevo fichero
		Path ficheroTexto = Paths.get(nombreConExtensionTXT);
		boolean nuevoFicheroPDF = Files.exists(ficheroTexto);
		
		if(nuevoFicheroPDF || forzarRegeneracionTexto) {

			//Obtenemos el documento y lo transformamos a texto
			PDDocument documento;

			try {
				
				documento = PDDocument.load(new File(rutaPDF));
				
			} catch (IOException e) {
				
		        System.err.println();
				System.err.println("Formato incorrecto o no se puede acceder al fichero: " + rutaPDF);
				return 0;
				
			}


			try {
				
				PDFTextStripper textStripper = new PDFTextStripper();
				paperText = textStripper.getText(documento);
				
			} catch (IOException e) {
				
		        System.err.println();
				System.err.println("No se puede transformar a texto el fichero: " + rutaPDF);
				return 0;
				
			}
			
			// Marcamos todos los "http". Esto nos va a permitir realizar manipulaciones del
			// texto (por ejemplo, eliminar o añadir texto) conociendo qué URLs hemos procesado
			// y cuáles están pendientes
			paperText = generateNewPaperTextWithMarkedHTTP(paperText);
			
		}
		
		if(nuevoFicheroPDF || forzarRegeneracionTexto || forzarBuscarEnlaces) {

			System.out.println();
			System.out.println("Artículo: " + doi);
			System.out.println("----------------------------------------------------------------------------------");
			System.out.println();
			
		    //El problema con los matches es que vamos a tener muchos falsos negativos por
		    //OCR incorrecto o el formato de los artículos. Tenemos que aplicar varias estrategias
		    //para maximizar los enlaces colrrectos, ya que sacan mucho trabajo
			
			//Utilizamos inicialmente un regex conservador
			//String simbolos = MARKER + "\\b(?:https?|ftp):\\/\\/[-a-zA-Z0-9+&@#\\/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#\\/%=~_|]";
			String regex = MARKER + "(?:http|https|ftp):\\/\\/[-a-zA-Z0-9+&@#\\/%\\?=~_\\|\\!\\.]+[-a-zA-Z0-9+&@#\\/%=~_|]";

			//De momento, solo vamos a aceptar URLS que a las que se puede acceder
			boolean accept200ResponseCodeOnly = true;

			//Obtenermos los enlaces del texto sin procesar y los almacenamos en la BD
			List<Integer> posicionEnlacesIdentificados = getURLsFromPaperText(doi, paperText, accept200ResponseCodeOnly, regex);

			// Desmarcamos los URLs procesados
			paperText = unmarkURLS(paperText, posicionEnlacesIdentificados);

			//Contamos el numero de enlaces extraidos
			numeroDeEnlaces = numeroDeEnlaces + posicionEnlacesIdentificados.size();

			//Eliminamos los CRLF, y volvemos a procesar
			// Esto es necesario porque en muchos artículos las URLs rompen entre páginas
			paperText = paperText.replaceAll("\\n", "");
			posicionEnlacesIdentificados = getURLsFromPaperText(doi, paperText, accept200ResponseCodeOnly, regex);
			paperText = unmarkURLS(paperText, posicionEnlacesIdentificados);
			numeroDeEnlaces = numeroDeEnlaces + posicionEnlacesIdentificados.size();
			
			//Usamos un regex más liberal, que permite espacios pero debe terminar en / o con una extension
			regex = MARKER + "(?:http|https|ftp):\\/\\/[-a-zA-Z0-9+&@#\\/%\\?=~_\\|\\!\\.: ]+(?:\\/|\\.pdf)";
			posicionEnlacesIdentificados = getURLsFromPaperText(doi, paperText, accept200ResponseCodeOnly, regex);
			paperText = unmarkURLS(paperText, posicionEnlacesIdentificados);
			numeroDeEnlaces = numeroDeEnlaces + posicionEnlacesIdentificados.size();
			
			//Aceptamos cualquier cosa que termine con un signo de puntuacion o similar
			regex = MARKER + "(?:http|https|ftp):\\/\\/[-a-zA-Z0-9+&@#\\/%\\?=~_\\|\\!\\.: ]+[^.,;\\\"'”´`\\[\\]]";

			posicionEnlacesIdentificados = getURLsFromPaperText(doi, paperText, accept200ResponseCodeOnly, regex);
			paperText = unmarkURLS(paperText, posicionEnlacesIdentificados);
			numeroDeEnlaces = numeroDeEnlaces + posicionEnlacesIdentificados.size();
			
			

			//Y en general, podemos aplicar más estrategias aquí
			//
			//
			//
			//

			//Ahora hacemos el último procesamiento, admitiendo cualquier responseCode,
			// excepto el cero
			accept200ResponseCodeOnly = false;
			posicionEnlacesIdentificados = getURLsFromPaperText(doi, paperText, accept200ResponseCodeOnly, regex);
			paperText = unmarkURLS(paperText, posicionEnlacesIdentificados);
			numeroDeEnlaces = numeroDeEnlaces + posicionEnlacesIdentificados.size();

			//En este punto, solo quedan las URL con responseCode = 0 en el fichero de texto
			//Chequeamos los http y vemos si difieren del numero de enlaces encontrado
			int numeroHTTP = StringUtils.countMatches(paperText, MARKER + "http");

			if (numeroHTTP > 0) {

				System.err.println();
				System.err.println("Faltan por recuperar " + numeroHTTP + " enlaces en el fichero: " + ficheroTexto);

			}
		}
				
		try {

			if(Files.exists(ficheroTexto)) {

				Files.delete(ficheroTexto);

			}

			Files.createFile(ficheroTexto);
			Files.writeString(ficheroTexto, paperText, StandardCharsets.UTF_8);

		} catch (IOException e) {

			System.err.println();
			System.err.println("No se puede escribir el fichero: " + ficheroTexto);

		}
		
		return numeroDeEnlaces;
		
	}

	private static List<Integer> getURLsFromPaperText(String doi, String contenido, boolean accept200ResponseCodeOnly, String regex) {

		List<Integer> posicionEnlacesIdentificados = new ArrayList<Integer>();

		Pattern p = Pattern.compile(regex);
		Matcher matcher = p.matcher(contenido);

		// Procesamos los matches
		while (matcher.find()) {
			
			boolean enlaceProcesado = false;

			int posicionEnlace = matcher.start();        	

			String enlaceIdentificado = matcher.group();

			// Eliminamos el MARKER
			enlaceIdentificado = enlaceIdentificado.substring(MARKER.length());
			
			//Con regex muy liberales, el enlace puede contener espacios

			// Podemos hacer dos cosas:

			//1. Eliminar los espacios
			String enlaceSinEspacios = enlaceIdentificado.replaceAll("\\s+","");
			if(!enlaceProcesado && !enlaceIdentificado.equals(enlaceSinEspacios)) {

				String finalURL = obtenerEnlaceFinal(enlaceSinEspacios);
				int codigoRespuesta = verificarExistencia(finalURL);
				enlaceProcesado = true;

				if(codigoRespuesta != 0) {

					//Primero vamos a procesar los URL correctos
					if (!accept200ResponseCodeOnly || codigoRespuesta == 200) {

						System.out.println("Link identificado: " + finalURL + " (" + codigoRespuesta + ")");

						//Obtenemos un poco de texto antes y después del elnace, que puede venir bien
						//para el análisis posterior

						posicionEnlacesIdentificados.add(posicionEnlace);
						String contexto = extractURLContext(contenido, posicionEnlace, enlaceSinEspacios);
						ConexionBaseDeDatos.insertNewLinkForPaper(doi, enlaceSinEspacios, finalURL, codigoRespuesta, contexto);

					}
				}
			}


			//2. Sustituir los espacios por underscores
			String enlaceConUnderscores = enlaceIdentificado.replaceAll("\\s+","_");	
			if(!enlaceProcesado && !enlaceIdentificado.equals(enlaceConUnderscores)) {

				String finalURL = obtenerEnlaceFinal(enlaceConUnderscores);
				int codigoRespuesta = verificarExistencia(finalURL);
				enlaceProcesado = true;

				if(codigoRespuesta != 0) {

					//Primero vamos a procesar los URL correctos
					if (!accept200ResponseCodeOnly || codigoRespuesta == 200) {

						System.out.println("Link identificado: " + finalURL + " (" + codigoRespuesta + ")");

						//Obtenemos un poco de texto antes y después del elnace, que puede venir bien
						//para el análisis posterior

						posicionEnlacesIdentificados.add(posicionEnlace);
						String contexto = extractURLContext(contenido, posicionEnlace, enlaceConUnderscores);
						ConexionBaseDeDatos.insertNewLinkForPaper(doi, enlaceConUnderscores, finalURL, codigoRespuesta, contexto);

					}

				} 
			}

			if(!enlaceProcesado) {

				String finalURL = obtenerEnlaceFinal(enlaceIdentificado);
				int codigoRespuesta = verificarExistencia(finalURL);

				if(codigoRespuesta != 0) {

					//Primero vamos a procesar los URL correctos
					if (!accept200ResponseCodeOnly || codigoRespuesta == 200) {

						System.out.println("Link identificado: " + finalURL + " (" + codigoRespuesta + ")");

						//Obtenemos un poco de texto antes y después del elnace, que puede venir bien
						//para el análisis posterior

						posicionEnlacesIdentificados.add(posicionEnlace);
						String contexto = extractURLContext(contenido, posicionEnlace, enlaceIdentificado);
						ConexionBaseDeDatos.insertNewLinkForPaper(doi, enlaceIdentificado, finalURL, codigoRespuesta, contexto);

					}
				}
			}			
		}

		return posicionEnlacesIdentificados;
	}

	private static String unmarkURLS(String paperText, List<Integer> posicionEnlacesIdentificados) {
		
		int markerLength = MARKER.length();
		String newPaperText = "";
		char[] subCadenaArray = new char[markerLength];
		
		for(int i = 0; i < paperText.length() - (markerLength - 1); i++) {
			
			paperText.getChars(i, i + markerLength, subCadenaArray, 0);
				
			String subCadena = String.valueOf(subCadenaArray);
						
			if(subCadena.equals(MARKER) && posicionEnlacesIdentificados.contains(i)) {
											
				// Saltamos MARKER
				i = i + markerLength;
					
			}
			
			newPaperText += paperText.charAt(i);
		}
		
		// Copiamos los últimos (markerSize - 1) caracteres
		if(paperText.length() >= markerLength) {
			
			char[] subCadenaArrayMasCorta = new char[markerLength - 1];
			
			paperText.getChars(paperText.length() - (markerLength - 1), paperText.length(), subCadenaArrayMasCorta, 0);
						
			newPaperText += String.valueOf(subCadenaArrayMasCorta);
			
		}
			
		return newPaperText;
	}

	private static String extractURLContext(String contenido, int posicionEnlace, String enlace) {
		
		String context = "";
		
		if(posicionEnlace >= CONTEXT_SIZE ) {
			
			context = contenido.substring(posicionEnlace - CONTEXT_SIZE, posicionEnlace);
			
		} else {
			
			if (posicionEnlace > 0 ) {
				
		    	context = contenido.substring(0, posicionEnlace);
				            		
			}
		}
		            
		context += enlace;
		
		if(contenido.length() - ( posicionEnlace + enlace.length() ) >= CONTEXT_SIZE ) {
			
			context += contenido.substring(posicionEnlace + enlace.length(), posicionEnlace + enlace.length() + CONTEXT_SIZE);
			
		} else {
				
		    	context += contenido.substring(posicionEnlace + enlace.length(), contenido.length());

		}
		
		return context;
	}
	
	private static String generateNewPaperTextWithMarkedHTTP(String paperText) {
		
		String newPaperText = "";
		char[] subCadenaArray = new char[4];
		
		for(int i = 0; i < paperText.length() - 3; i++) {
			
			paperText.getChars(i, i+4, subCadenaArray, 0);
				
			String subCadena = String.valueOf(subCadenaArray);
						
			if(subCadena.equals("http")) {
													
					newPaperText += MARKER;
					
			}
			
			newPaperText += paperText.charAt(i);
		}
		
		// Copiamos los últimos tres caracteres
		if(paperText.length() >= 4) {
			
			char[] subCadenaArrayMasCorta = new char[3];
			
			paperText.getChars(paperText.length() - 3, paperText.length(), subCadenaArrayMasCorta, 0);
						
			newPaperText += String.valueOf(subCadenaArrayMasCorta);
			
		}
			
		return newPaperText;
    }

	private static int verificarExistencia(String enlace) {
        try {
            Connection.Response respuesta = Jsoup.connect(enlace)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.4.1 Safari/605.1.15")
                    .followRedirects(true)
                    .timeout(5000)
                    .method(Connection.Method.HEAD)
                    .execute();

            return respuesta.statusCode();
        } catch (IOException e) {
            if (e instanceof HttpStatusException) {
                HttpStatusException httpException = (HttpStatusException) e;
                return httpException.getStatusCode();
            } else {
                return 0;
            }
        }
    }
	
	private static String obtenerEnlaceFinal(String enlace) {
        try {
            Connection.Response respuesta = Jsoup.connect(enlace)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.4.1 Safari/605.1.15")
                    .followRedirects(true)
                    .timeout(1000000)
                    .method(Connection.Method.HEAD)
                    .execute();

            return respuesta.url().toExternalForm();
        } catch (IOException e) {
            return enlace;
        }
    }

}
