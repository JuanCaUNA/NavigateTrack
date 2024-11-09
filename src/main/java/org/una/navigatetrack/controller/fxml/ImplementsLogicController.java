package org.una.navigatetrack.controller.fxml;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.una.navigatetrack.configs.Config;
import org.una.navigatetrack.manager.NodeGraphFacade;
import org.una.navigatetrack.manager.NodesDrawerManagers;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 * Controlador que maneja la lógica para la interfaz gráfica.
 *
 * @author juanc
 */
@SuppressWarnings("All")
public class ImplementsLogicController implements Initializable {

    private NodesDrawerManagers manager;
    private NodeGraphFacade nodeGraphFacade;

    @FXML
    private CheckBox blockCBox;
    @FXML
    private TextArea textArea;
    @FXML
    private Pane mapPane, paintPane;
    @FXML
    private Button startB, finishB, changeImageB, infoB, pauseB;
    @FXML
    private Label labelDestino, labelTitle, labelPartida, labelTime;
    @FXML
    private RadioButton initRadioB, endingRadioB, radioBNode, radioBConnection, radioBDijkstra, radioBFloydWarshall;

    private boolean change = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        nodeGraphFacade = new NodeGraphFacade(paintPane);
        setupUI();
        setupToggleGroups();
        setupEventHandlers();

        blockCBox.setDisable(true);
    }

    private void setupUI() {
        paintPane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.0);");
        loadImageMap("/images/map2.png");
    }

    private void setupToggleGroups() {
        ToggleGroup selections = new ToggleGroup();
        initRadioB.setToggleGroup(selections);
        endingRadioB.setToggleGroup(selections);
        radioBConnection.setToggleGroup(selections);
        radioBNode.setToggleGroup(selections);

        initRadioB.setSelected(true);

        ToggleGroup mode = new ToggleGroup();
        radioBDijkstra.setToggleGroup(mode);
        radioBFloydWarshall.setToggleGroup(mode);
        radioBDijkstra.setSelected(true);
    }

    private void changeImage() {
        loadImageMap(change ? "/images/map2.png" : "/images/map0.png");
        change = !change;
    }

    private void loadImageMap(String path) {
        var image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
        var imageView = new ImageView(image);
        var ratio = Math.min(670 / image.getWidth(), 950 / image.getHeight());
        imageView.setFitWidth(image.getWidth() * ratio);
        imageView.setFitHeight(image.getHeight() * ratio);
        imageView.setPreserveRatio(true);
        mapPane.getChildren().add(imageView);
    }

    private void setupEventHandlers() {
        paintPane.setOnMouseClicked(event -> select(new double[]{event.getX(), event.getY()}));

        infoB.setOnAction(actionEvent -> textArea.setText(Config.instructions));
        startB.setOnAction(event -> {
            System.out.println("Iniciando viaje...");
            nodeGraphFacade.setDijkstra(radioBDijkstra.isSelected());
            nodeGraphFacade.initTravel();
        });
        finishB.setOnAction(event -> {
            System.out.println("Finalizando viaje...");
            nodeGraphFacade.endTravel();
        });
        changeImageB.setOnAction(event -> changeImage());

        blockCBox.setOnAction(event -> System.out.println("bloquear este camino"));
    }

    private void select(double[] location) {
        if (initRadioB.isSelected()) {
            nodeGraphFacade.setStartNode(location);
            labelPartida.setText("Punto de partida: " + location[0] + ", " + location[1]);
        } else if (endingRadioB.isSelected()) {
            nodeGraphFacade.setEndNode(location);
            labelDestino.setText("Punto de destino: " + location[0] + ", " + location[1]);
        } else if (radioBNode.isSelected()) {
            System.out.println("Seleccionaste un nodo en: " + location[0] + ", " + location[1]);
        } else if (radioBConnection.isSelected()) {
            blockCBox.setDisable(false);
            System.out.println("Seleccionaste una conexión en: " + location[0] + ", " + location[1]);
        }
    }
}
