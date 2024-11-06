package org.una.navigatetrack.list;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class Json {

    private Json() {
        // Constructor privado para evitar instanciación
    }

    static ObjectMapper mapper = new ObjectMapper();

    /**
     * Lee un archivo JSON y lo deserializa en una lista de objetos.
     *
     * @param path      La ruta del archivo JSON.
     * @param reference La referencia al tipo de lista a deserializar.
     * @param <T>       El tipo de los elementos de la lista.
     * @return Un Optional que contiene la lista deserializada si tiene éxito, o vacío si hubo un error.
     */
    static <T> Optional<List<T>> leer(String path, TypeReference<List<T>> reference) {
        try {
            File nodeListFile = new File(path);
            if (nodeListFile.exists()) {
                List<T> deserializedNodeList = mapper.readValue(nodeListFile, reference);
                System.out.println("Lista de nodos leída desde: " + path);
                return Optional.of(deserializedNodeList);
            } else {
                System.err.println("El archivo " + path + " no existe.");
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo JSON: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty(); // Devolver Optional vacío si hubo un error
    }

    /**
     * Guarda una lista de objetos en un archivo JSON.
     *
     * @param list La lista de objetos a guardar.
     * @param path La ruta del archivo donde se guardará la lista.
     */
    static void save(List<?> list, String path) {
        try {
            File file = new File(path);
            // Asegurarse de que los directorios existen
//            file.getParentFile().mkdirs();  // Descomentar esta línea si es necesario

            mapper.writerWithDefaultPrettyPrinter().writeValue(file, list);
            System.out.println("Lista de nodos guardada en: " + path);
        } catch (IOException e) {
            System.err.println("Error al guardar el archivo JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
