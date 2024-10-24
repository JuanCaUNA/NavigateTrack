package org.una.navigatetrack.controller.fxml;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.una.navigatetrack.manager.DrawerManager;
import org.una.navigatetrack.manager.NodesDrawerManagers;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author juanc
 */
public class ImplementsLogicController implements Initializable {


    NodesDrawerManagers manager;
    @FXML
    private AnchorPane mainAnchorPane;
    @FXML
    private Pane mapPane, paintPane, menuPane;
    private boolean change = false;
    @FXML
    private Button buttonStart, buttonEnd, changeImageB;
    @FXML
    private RadioButton radioBPartida, radioBDestino, radioBNode, radioBConnection;
    @FXML
    private TextArea textArea;
    @FXML
    private Label labelDestino, LabelTitle, labelPartida, labelTime;
    @FXML
    private RadioButton radioBDijkstra, radioBFloydWarshall;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        manager = new NodesDrawerManagers(new DrawerManager(paintPane));
        setupUI();
    }

    private void setupUI() {
        paintPane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.0);");
        loadImageMap("/images/map2.png");
        setupToggleGroups();
    }

    private void setupToggleGroups() {
        ToggleGroup modoToggleGroup = new ToggleGroup();
        radioBPartida.setToggleGroup(modoToggleGroup);
        radioBDestino.setToggleGroup(modoToggleGroup);
        radioBConnection.setToggleGroup(modoToggleGroup);
        radioBNode.setToggleGroup(modoToggleGroup);

        ToggleGroup modoToggleGroup2 = new ToggleGroup();
        radioBDijkstra.setToggleGroup(modoToggleGroup2);
        radioBFloydWarshall.setToggleGroup(modoToggleGroup2);
    }

    private void toggleImage() {
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
}
