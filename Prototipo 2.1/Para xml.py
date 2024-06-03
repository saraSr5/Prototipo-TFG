import requests

# Definir la URL de la API de DBLP para obtener artículos de conferencias en formato XML
dblp_api_url = "https://dblp.org/search/publ/api?q=*&format=xml"

# Realizar la solicitud a la API de DBLP
response = requests.get(dblp_api_url)

# Verificar si la solicitud fue exitosa
if response.status_code == 200:
    # Obtener los primeros 4 artículos del XML
    xml_content = response.content
    first_four_articles = xml_content[:xml_content.find(b'</article>')+len(b'</article>')*4]

    # Imprimir los primeros 4 artículos
    print(first_four_articles.decode('utf-8'))
else:
    print("Error al obtener datos de DBLP:", response.status_code)
