package t.menu;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.una.navigatetrack.controller.fxml.BaseMenuController;

import java.net.URL;
import java.util.ResourceBundle;

public class CreationNodesMenuPaneController extends BaseMenuController {
    @FXML
    private Label nodoActualLabel;
    @FXML
    private TextArea nodoInfoTextArea;
    @FXML
    private Button saveButton, deleteNodoButton, deleteConectionButton, changeImageB, switchToUseMenuButton;
    @FXML
    private RadioButton izRadioB, derRadioB, adelanteRadioB, contrarioRadioB, seleccionarRadioB;
    @FXML
    private RadioButton editRadioB, addRadioB;


    public void initialize(URL url, ResourceBundle rb) {
        setupToggleGroups();
        //setupEventHandlersOfMenuPanel();
    }

    @Override
    public void setupMenu() {
        setupSwitchButton();
    }

    private void setupSwitchButton() {
        switchToUseMenuButton.setOnAction(event -> {
            // Cambiar a UseMenuPane
            baseController.loadMenuPane("/fxml/UseMenuPane.fxml");
        });
    }

    protected void setupEventHandlers() {
        // Lógica específica para CreationNodesMenuPaneController
    }

    private void setupToggleGroups() {
        ToggleGroup modoToggleGroup = new ToggleGroup();
        addRadioB.setToggleGroup(modoToggleGroup);
        editRadioB.setToggleGroup(modoToggleGroup);
        editRadioB.setSelected(true);

        ToggleGroup directionToggleGroup = new ToggleGroup();
        izRadioB.setToggleGroup(directionToggleGroup);
        derRadioB.setToggleGroup(directionToggleGroup);
        adelanteRadioB.setToggleGroup(directionToggleGroup);
        contrarioRadioB.setToggleGroup(directionToggleGroup);
        seleccionarRadioB.setToggleGroup(directionToggleGroup);
        seleccionarRadioB.setSelected(true);
    }

    // ... (Mantén el resto de métodos sin cambios, ya que parecen correctos)
}
