module org.una.navigatetrack {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires static lombok;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;

    opens org.una.navigatetrack to javafx.fxml;
    exports org.una.navigatetrack;
    exports org.una.navigatetrack.roads;
    opens org.una.navigatetrack.roads to javafx.fxml;
    exports org.una.navigatetrack.controller.fxml;
    opens org.una.navigatetrack.controller.fxml to javafx.fxml;
    exports org.una.navigatetrack.manager;
    opens org.una.navigatetrack.manager to javafx.fxml;
    exports org.una.navigatetrack.utils;
    opens org.una.navigatetrack.utils to javafx.fxml;
    exports org.una.navigatetrack.list;
    opens org.una.navigatetrack.list to javafx.fxml;
    exports org.una.navigatetrack.dto;
    opens org.una.navigatetrack.dto to javafx.fxml;
}