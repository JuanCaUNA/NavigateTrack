package t.menu;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.una.navigatetrack.controller.fxml.BaseMenuController;

import java.net.URL;
import java.util.ResourceBundle;

public class UseMenuPaneController extends BaseMenuController {
    @FXML
    private Button switchToCreationNodesButton;

    // Inicialización
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupSwitchButton();
    }

    private void setupSwitchButton() {
        switchToCreationNodesButton.setOnAction(event -> {
            // Cambiar a CreationNodesMenuPane
            baseController.loadMenuPane("/fxml/CreationNodesMenuPane.fxml");
        });
    }

    @Override
    public void setupMenu() {
        setupSwitchButton(); // Ya configurado en initialize
    }
}
