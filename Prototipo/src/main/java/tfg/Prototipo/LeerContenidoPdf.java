package tfg.Prototipo;

import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class LeerContenidoPdf {

    public static void main(String[] args) {
        String rutaPDF = "C:/Users/Admin/Desktop/Ejemplo para descargar pdf/Frameworks_Generate_Domain-Specific_Languages_A_Case_Study_in_the_Multimedia_Domain.pdf";

        try (PDDocument document = PDDocument.load(new File(rutaPDF))) {
            PDFTextStripper textStripper = new PDFTextStripper();
            String contenido = textStripper.getText(document);
            System.out.println(contenido);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}