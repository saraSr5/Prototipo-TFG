java -cp checkurl-0.0.1-SNAPSHOT-jar-with-dependencies.jar es.upm.grise.checkurl.LeerXml $1 > titleList.txt

cat titleList.txt | xargs java -cp checkurl-0.0.1-SNAPSHOT-jar-with-dependencies.jar es.upm.grise.checkurl.ObtenerPdfConSerpApi > urlList.txt

cat urlList.txt | xargs java -cp checkurl-0.0.1-SNAPSHOT-jar-with-dependencies.jar es.upm.grise.checkurl.DescargarPdfCache > fileList.txt

cat fileList.txt | xargs java -cp checkurl-0.0.1-SNAPSHOT-jar-with-dependencies.jar es.upm.grise.checkurl.MostrarYComprobarEnlaces
