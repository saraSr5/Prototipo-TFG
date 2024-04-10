Descargar el Prototipo 2.0

Tener instalado Maven en el ordenador donde se va a ejecutar el código.

Ejecutar la clase "LeerXml.java" con el argumento:
https://dblp.org/search/publ/api?q=toc%3Adb/journals/tse/tse37.bht%3A&h=1000&format=xml
Resultado: los títulos de los artículos que se encuentran en la url proporcionada como argumento.

Ejecutar la clase "ObtenerPdfConSerpApi.java" con el argumentos:
Un titulo obtenido de la clase anterior(de momento uno por uno, no admite los 5 titulos a la vez):
Por ejemplo:
1. Titulo: Frameworks Generate Domain-Specific Languages: A Case Study in the Multimedia Domain.
2. Titulo: FlowTalk: Language Support for Long-Latency Operations in Embedded Devices.
3. Titulo: Toward a Formalism for Conservative Claims about the Dependability of Software-Based Systems.
4. Titulo: Dynamic QoS Management and Optimization in Service-Based Systems.
5. Titulo: Semi-Proving: An Integrated Method for Program Proving, Testing, and Debugging.
Resultado: los enlaces de descarga del pdf obtenidos del JSON junto con el nombre del artículo.

Ejecutar la clase "DescargarPdfCache.java" con los argumentos(ambos obtenidos de la ejecución de la clase anterior). Además en esta clase se debe cambiar el apartado de DIR_CACHE = "C:\\Users\\Admin\\Desktop\\PDF descargado\\" por el destino donde se quieren descargar los pdfs en local:
"url de descarga de pdf" "Titulo del articulo(con el punto final incluido"
Resultado: Se descarga el pdf mostrando su ruta de descarga y se añade a la tabla de nombre_pdfs de la base de datos si no se ha descargado antes. Si se ha descargado antes muestra un mensaje indicándolo.

Ejecutar la clase "MostrarYComprobarEnlaces.java" con los argumentos:
"ruta de descarga del pdf" "titulo del pdf"
Resultado: se añaden las urls del pdf a la base de datos en la tabla dowloaded_pdfs e indica si son o no accesibles:
Resultado esperado en la base de datos (hay que acceder a través de la aplicación DB Browser (SQLite):
- Frameworks Generate Domain-Specific Languages: A Case Study in the Multimedia Domain.
	-1 enlace: existe.
-  FlowTalk: Language Support for Long-Latency Operations in Embedded Devices.
	-1º enlace: No.
	-2º enlace: existe.
	-3º enlace: No.
	-4º enlace: existe.
	-5º enlace:existe.
- Toward a Formalism for Conservative Claims about the Dependability of Software-Based Systems.
	-1º enlace: existe.
	-2º enlace: existe.
	-3º enlace: existe.
- Dynamic QoS Management and Optimization in Service-Based Systems.
	-1 enlace: existe.
- Semi-Proving: An Integrated Method for Program Proving, Testing, and Debugging.
	-1º enlace: No.
	-2º enlace: No.
