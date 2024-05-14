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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

public class MostrarYComprobarEnlaces {
	
	static int CONTEXT_SIZE = 100;
		
	public static void main(String[] args) {
		
		System.err.println();
		System.err.println();
		System.err.println();
		System.err.println("Checking URLs");
		System.err.println("---------------------------------------------------------");
		
		if (args.length == 0) {
			System.err.println("No se ha proporcionado ninguna ruta");
			System.exit(1);
		}

		int primeraRuta = 0;
		boolean forzarRevision = false;
		
	    if (args.length %2 !=  0) {
	    	
	    	if(args[0].equals("-f")) {
	    		primeraRuta = 1;
	    		forzarRevision = true;
	            System.err.println("Se descargan de nuevo los archivos a la cache");
	    	}
	    }
		
		java.util.logging.Logger.getLogger("org.apache.pdfbox").setLevel(java.util.logging.Level.SEVERE);
		
		int numeroTotalEnlaces = 0;

		for (int i = primeraRuta; i < args.length; i += 2) {
			String rutaPDF = args[i]; //Cojo la ruta del PDF
			String doi = args[i + 1]; //Cojo el DOI del artículo 
			
			numeroTotalEnlaces += extraeEnlacesDeArticulo(doi, rutaPDF, forzarRevision);
		}
		
		System.err.println();
		System.err.println(numeroTotalEnlaces + " links found");

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
			numeroTotalEnlaces += extraeEnlacesDeArticulo(doi, rutaPDF, false);
		}
	    
		return numeroTotalEnlaces;
	}

	private static int extraeEnlacesDeArticulo(String doi, String rutaPDF, boolean forzarRevision) {
		//Comprobamos si la version TXT del documento ya existe en la cache
		String nombreSinExtension = rutaPDF.substring(0, rutaPDF.length() - 3);
		String nombreConExtensionTXT = nombreSinExtension + "txt";

		Path ficheroTexto = Paths.get(nombreConExtensionTXT);
		if(!Files.exists(ficheroTexto) || forzarRevision) {

			//Obtenemos el documento y lo transformamos a texto
			PDDocument documento;
			String contenido;

			try {
				documento = PDDocument.load(new File(rutaPDF));
			} catch (IOException e) {
				System.err.println("Formato incorrecto o no se puede acceder al fichero: " + rutaPDF);
				return 0;
			}


			try {
				PDFTextStripper textStripper = new PDFTextStripper();
				contenido = textStripper.getText(documento);
			} catch (IOException e) {
				System.err.println("No se puede transformar a texto el fichero: " + rutaPDF);
				return 0;
			}


			try {
				if(Files.exists(ficheroTexto)) {
					
					Files.delete(ficheroTexto);

				}
				
				Files.createFile(ficheroTexto);
				
				Files.writeString(ficheroTexto, contenido, StandardCharsets.UTF_8);

			} catch (IOException e) {
				System.err.println("No se puede escribir el fichero: " + ficheroTexto);
				return 0;
			}

			ConexionBaseDeDatos.EliminarEnlacesBD(doi);

		}

		try {
			String contenido = Files.readString(ficheroTexto);
			
			//Guardo la verificación de la URL en la base de datos
			List<Integer> enlaces = guardarVerificacionEnBaseDeDatos(contenido, doi);
			
			//Chequeamos los http y vemos si difieren del numero de enlaces encontrado
			int numeroHTTP = StringUtils.countMatches(contenido, "http");
			
			int numeroEnlaces = enlaces.size();
							
			if (numeroEnlaces < numeroHTTP) {
				
				System.err.println("Faltan por recuperar " + (numeroHTTP - numeroEnlaces) + " enlaces en el fichero: " + ficheroTexto);
														
				Files.delete(ficheroTexto);
				Files.createFile(ficheroTexto);
				Files.writeString(ficheroTexto, generarTextoConHTTPMarcado(contenido, enlaces), StandardCharsets.UTF_8);

			}
			
			return numeroEnlaces;
			
		} catch (IOException e) {
			System.err.println("No se pueden identificar los links del fichero: " + ficheroTexto);
			return 0;
		}
	}

	private static List<Integer> guardarVerificacionEnBaseDeDatos(String texto, String doi) {
        String simbolos = "\\b(?:https?|ftp):\\/\\/[-a-zA-Z0-9+&@#\\/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#\\/%=~_|]";
        Pattern p = Pattern.compile(simbolos);
        Matcher matcher = p.matcher(texto);
        List<Integer> enlaces = new ArrayList<Integer>();

        while (matcher.find()) {
        	
            int posicion = matcher.start();        	
        	enlaces.add(posicion);
        	
            String enlace = matcher.group();
        	
            String contexto = "";
            if(posicion >= CONTEXT_SIZE ) {
            	
            	contexto = texto.substring(posicion - CONTEXT_SIZE, posicion);
            	
            } else {
            	
            	if (posicion > 0 ) {
            		
                	contexto = texto.substring(0, posicion);
            		            		
            	}
            }
                        
            contexto += enlace;
            
            if(texto.length() - ( posicion + enlace.length() ) >= CONTEXT_SIZE ) {
            	
            	contexto += texto.substring(posicion + enlace.length(), posicion + enlace.length() + CONTEXT_SIZE);
            	
            } else {
            		
                	contexto += texto.substring(posicion + enlace.length(), texto.length());

            }
        	            
            int codigoRespuesta = verificarExistencia(enlace);
            
            System.err.println("Link identificado: " + enlace + " (" + codigoRespuesta + ")");
            
			ConexionBaseDeDatos.insertPDF(doi, enlace, codigoRespuesta, contexto);
            
        }
        
        return enlaces;
    }
	
	private static String generarTextoConHTTPMarcado(String contenido, List<Integer> matchedOcurrences) {
		
		String nuevoContenido = "";
		char[] subCadenaArray = new char[4];
		
		for(int i = 0; i < contenido.length() - 3; i++) {
			
			contenido.getChars(i, i+4, subCadenaArray, 0);
				
			String subCadena = String.valueOf(subCadenaArray);
						
			if(subCadena.equals("http")) {
				
				if(!matchedOcurrences.contains(i)) {
					
					nuevoContenido += ">>>>>>> ";
					
				}
			}
			
			nuevoContenido += contenido.charAt(i);
		}
		
		// Copiamos los últimos tres caracteres
		if(contenido.length() >= 4) {
			
			char[] subCadenaArrayMasCorta = new char[3];
			
			contenido.getChars(contenido.length() - 3, contenido.length(), subCadenaArrayMasCorta, 0);
						
			nuevoContenido += String.valueOf(subCadenaArrayMasCorta);
			
		}
			
		return nuevoContenido;
    }

    private static int verificarExistencia(String enlace) {
        try {
            Connection.Response respuesta = Jsoup.connect(enlace)
                    .followRedirects(true)
                    .timeout(1000000)
                    .method(Connection.Method.HEAD)
                    .execute();

            return respuesta.statusCode();
        } catch (IOException e) {
            if (e instanceof HttpStatusException) {
                HttpStatusException httpException = (HttpStatusException) e;
                return httpException.getStatusCode();
            } else {
                System.err.println(e.getMessage() + "\"" + enlace + "\"");
                return 0;
            }
        }
    }
}
