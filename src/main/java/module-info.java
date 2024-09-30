module org.una.navigatetrack {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires static lombok;

    opens org.una.navigatetrack to javafx.fxml;
    exports org.una.navigatetrack;
}