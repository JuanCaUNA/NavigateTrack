module org.una.navigatetrack {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires static lombok;
    requires java.base;

    opens org.una.navigatetrack to javafx.fxml;
    exports org.una.navigatetrack;
    exports org.una.navigatetrack.roads;
    opens org.una.navigatetrack.roads to javafx.fxml;
    exports org.una.navigatetrack.controller.fxml;
    opens org.una.navigatetrack.controller.fxml to javafx.fxml;
    exports org.una.navigatetrack.manager;
    opens org.una.navigatetrack.manager to javafx.fxml;
    exports org.una.navigatetrack.controller;
    opens org.una.navigatetrack.controller to javafx.fxml;
}