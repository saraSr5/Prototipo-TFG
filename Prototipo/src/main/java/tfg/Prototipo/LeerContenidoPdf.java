package tfg.Prototipo;

import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class LeerContenidoPdf {

    public static void main(String[] args) {
        String rutaPDF = "C:/Users/Admin/Desktop/Ejemplo para descargar pdf/Frameworks_Generate_Domain-Specific_Languages_A_Case_Study_in_the_Multimedia_Domain.pdf";

        try (PDDocument documento = PDDocument.load(new File(rutaPDF))) {//cargo el documento
            PDFTextStripper extraer = new PDFTextStripper();//creo instancia de PDFTextStripper
            String contenido = extraer.getText(documento);//extraigo en la variable contenido el texto del documento
            System.out.println(contenido);//muestro por pantalla el contenido del documento
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}