#!/bin/sh -x

#java -cp checkurl-0.0.1-SNAPSHOT-jar-with-dependencies.jar es.upm.grise.checkurl.LeerXml $1 > titleList.txt 2> errorTitleList.txt

#cat titleList.txt | xargs java -cp checkurl-0.0.1-SNAPSHOT-jar-with-dependencies.jar es.upm.grise.checkurl.ObtenerPdfConSerpApi > urlList.txt 2> errorUrlList.txt

cat urlList.txt | xargs java -cp checkurl-0.0.1-SNAPSHOT-jar-with-dependencies.jar es.upm.grise.checkurl.DescargarPdfCache --force-download > fileList.txt 2> errorFileList.txt

cat fileList.txt | xargs java -cp checkurl-0.0.1-SNAPSHOT-jar-with-dependencies.jar es.upm.grise.checkurl.MostrarYComprobarEnlaces --search-using-pdf > links.txt 2>errorLinks.txt

java -cp checkurl-0.0.1-SNAPSHOT-jar-with-dependencies.jar es.upm.grise.checkurl.ClasificarURL
