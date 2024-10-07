import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AnalizadorDatosAbiertos {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Introduce la ruta del archivo a analizar:");
        String rutaArchivo = scanner.nextLine();

        if (rutaArchivo.endsWith(".csv")) {
            List<String[]> datosCSV = parsearCSV(rutaArchivo);
            mostrarResumenCSV(datosCSV);
        } else if (rutaArchivo.endsWith(".json")) {
            JsonElement datosJSON = parsearJSON(rutaArchivo);
            mostrarResumenJSON(datosJSON);
        } else if (rutaArchivo.endsWith(".xml")) {
            Document datosXML = parsearXML(rutaArchivo);
            mostrarResumenXML(datosXML);
        } else {
            System.out.println("Formato de archivo no soportado.");
        }
    }


    public static List<String[]> parsearCSV(String rutaArchivo) {
        List<String[]> registros = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] valores = linea.split(",");
                registros.add(valores);
            }
        } catch (Exception e) {
            System.out.println("Error al leer el archivo CSV: " + e.getMessage());
        }
        return registros;
    }

    public static JsonElement parsearJSON(String rutaArchivo) {
        JsonElement jsonElement = null;
        try (FileReader reader = new FileReader(rutaArchivo)) {
            jsonElement = new Gson().fromJson(reader, JsonElement.class);
        } catch (Exception e) {
            System.out.println("Error al leer el archivo JSON: " + e.getMessage());
        }
        return jsonElement;
    }

    public static Document parsearXML(String rutaArchivo) {
        Document doc = null;
        try {
            File archivo = new File(rutaArchivo);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(archivo);
            doc.getDocumentElement().normalize();
        } catch (Exception e) {
            System.out.println("Error al leer el archivo XML: " + e.getMessage());
        }
        return doc;
    }

    public static void mostrarResumenCSV(List<String[]> datos) {
        if (datos.isEmpty()) {
            System.out.println("No se encontraron datos.");
            return;
        }

        System.out.println("Resumen del archivo CSV:");
        System.out.println("Número total de filas: " + datos.size());
        System.out.println("Número de columnas: " + datos.get(0).length);

        try {
            double total = 0;
            int numCount = 0;
            double min = Double.MAX_VALUE, max = Double.MIN_VALUE;

            for (String[] fila : datos) {
                for (String valor : fila) {
                    try {
                        double numero = Double.parseDouble(valor);
                        total += numero;
                        numCount++;
                        min = Math.min(min, numero);
                        max = Math.max(max, numero);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
            if (numCount > 0) {
                System.out.printf("Suma: %.2f, Promedio: %.2f, Min: %.2f, Max: %.2f%n",
                        total, total / numCount, min, max);
            }
        } catch (Exception e) {
            System.out.println("Error en el análisis de estadísticas numéricas.");
        }

        System.out.println("\nPrimeros 5 registros:");
        for (int i = 0; i < Math.min(5, datos.size()); i++) {
            System.out.println(String.join(" | ", datos.get(i)));
        }
    }

    public static void mostrarResumenJSON(JsonElement datos) {
        if (datos == null) {
            System.out.println("No se encontraron datos JSON.");
            return;
        }

        if (datos.isJsonArray()) {
            JsonArray jsonArray = datos.getAsJsonArray();
            System.out.println("Resumen del archivo JSON:");
            System.out.println("Número total de registros: " + jsonArray.size());

            // Muestra los primeros 5 registros
            System.out.println("\nPrimeros 5 registros:");
            for (int i = 0; i < Math.min(5, jsonArray.size()); i++) {
                System.out.println(jsonArray.get(i).toString());
            }
        } else if (datos.isJsonObject()) {
            // Si es un JsonObject, simplemente muestra el contenido
            System.out.println("Resumen del archivo JSON:");
            System.out.println(datos.toString());
        } else {
            System.out.println("Formato JSON no reconocido.");
        }
    }

    public static void mostrarResumenXML(Document doc) {
        if (doc == null) {
            System.out.println("No se encontraron datos.");
            return;
        }

        System.out.println("Resumen del archivo XML:");
        Element raiz = doc.getDocumentElement();
        System.out.println("Elemento raíz: " + raiz.getNodeName());

        NodeList elementos = raiz.getChildNodes();
        int numElementos = 0;
        for (int i = 0; i < elementos.getLength(); i++) {
            if (elementos.item(i).getNodeType() == Node.ELEMENT_NODE) {
                numElementos++;
            }
        }
        System.out.println("Número total de elementos: " + numElementos);

        System.out.println("\nPrimeros 5 elementos:");
        int contador = 0;
        for (int i = 0; i < elementos.getLength() && contador < 5; i++) {
            Node nodo = elementos.item(i);
            if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                Element elemento = (Element) nodo;
                System.out.println("Elemento: " + elemento.getNodeName());
                NodeList hijos = elemento.getChildNodes();
                for (int j = 0; j < hijos.getLength(); j++) {
                    Node hijo = hijos.item(j);
                    if (hijo.getNodeType() == Node.ELEMENT_NODE) {
                        System.out.println(" " + hijo.getNodeName() + ": " + hijo.getTextContent());
                    }
                }
                contador++;
            }
        }
    }
}
