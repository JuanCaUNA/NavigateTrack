module org.una.navigatetrack {
    // Requiere módulos de JavaFX
    requires javafx.controls;  // Para controles básicos de JavaFX
    requires javafx.fxml;      // Para trabajar con FXML
    requires javafx.graphics;  // Para trabajar con gráficos en JavaFX

    // Requiere librerías adicionales
    requires org.controlsfx.controls;  // Para controles adicionales (ControlsFX)
    requires com.dlsc.formsfx;         // Para formularios avanzados (FormsFX)
    requires org.kordamp.ikonli.javafx;  // Para íconos en JavaFX
    requires org.kordamp.bootstrapfx.core;  // Para estilos de BootstrapFX
    requires static lombok;           // Para usar Lombok (solo en compilación)
    requires com.fasterxml.jackson.databind;  // Para manipulación de JSON con Jackson
    requires com.jfoenix;
    requires java.net.http;  // Para el diseño de Material Design en JavaFX

    // Abre los paquetes necesarios para la reflexión (JavaFX y otros)
    opens org.una.navigatetrack to javafx.fxml;  // Para la reflexión en FXML
    opens org.una.navigatetrack.roads to javafx.fxml;  // Necesario para FXML en las rutas
    opens org.una.navigatetrack.controller.fxml to javafx.fxml;  // Para controladores FXML
    opens org.una.navigatetrack.manager to javafx.fxml;  // Para FXML en la gestión
    opens org.una.navigatetrack.utils to javafx.fxml;  // Si usas FXML con utilidades
    opens org.una.navigatetrack.list to javafx.fxml;  // Para FXML con listas
    opens org.una.navigatetrack.dto to javafx.fxml;  // Si usas FXML con DTOs

    // Exporta los paquetes necesarios para que otros módulos puedan usarlos
    exports org.una.navigatetrack;  // Paquete principal
    exports org.una.navigatetrack.roads;  // Paquete de rutas
    exports org.una.navigatetrack.controller.fxml;  // Controladores FXML
    exports org.una.navigatetrack.manager;  // Gestión
    exports org.una.navigatetrack.utils;  // Utilidades
    exports org.una.navigatetrack.list;  // Listas
    exports org.una.navigatetrack.dto;  // DTOs

}
