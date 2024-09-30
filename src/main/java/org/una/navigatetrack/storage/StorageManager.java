package org.una.navigatetrack.storage;

import javax.swing.*;
import java.io.*;

public class StorageManager<T> {
    private final String filePath;

    public StorageManager(String Path, String fileName) {
        this.filePath = Path + fileName; // Se asume que filePath incluye el nombre y la extensión
        createDirectoryIfNotExists(new File(filePath).getParent());
    }

    public void save(T obj) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(obj);
            //showInfoDialog("Archivo guardado exitosamente.");
        } catch (IOException e) {
            showErrorDialog("Error al guardar el archivo '" + filePath + "': " + e.getMessage());
            e.printStackTrace(); // Imprime la pila de errores
        }
    }

    @SuppressWarnings("unchecked")
    public T read() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            showErrorDialog("Error al leer el archivo: " + " - " + e.getMessage());
            return null;
        }
    }

    public boolean delete() {
        File file = new File(filePath);
        boolean deleted = file.exists() && file.delete();
        if (deleted) {
            showInfoDialog("Archivo eliminado exitosamente.");
        } else {
            showErrorDialog("Error al eliminar el archivo. Ubicado: " + file.getAbsolutePath());
        }
        return deleted;
    }

    private void createDirectoryIfNotExists(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists() && !directory.mkdirs()) {
            showErrorDialog("No se pudo crear el directorio: " + directoryPath);
        }
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfoDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
}
