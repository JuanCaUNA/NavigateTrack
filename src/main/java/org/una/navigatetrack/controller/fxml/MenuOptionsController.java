/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.una.navigatetrack.controller.fxml;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.una.navigatetrack.utils.Drawer;
import org.una.navigatetrack.manager.NodesDrawerManager;
import org.una.navigatetrack.manager.NodesManager;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author juanc
 */
public class MenuOptionsController implements Initializable {


    @FXML    private AnchorPane mainAnchorPane;
    @FXML    private Pane mapPane, paintPane, menuPane;
    @FXML    private Button bt1, bt2;
    @FXML    private RadioButton rb2, rb1;
    @FXML    private TextArea info;
    @FXML    private Label label2, label1, label3, label4;
    @FXML    private RadioButton rb3, rb6, rb5, rb4;

    NodesDrawerManager manager;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        manager = new NodesDrawerManager(new NodesManager(), new Drawer(paintPane));
        setupUI();
    }

    private void setupUI() {
        paintPane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.0);");
        loadImageMap("/images/map2.png");
//        setupToggleGroups();
    }

    private boolean change = false;

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
