package t;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.una.navigatetrack.manager.NodesDrawerManager;
import org.una.navigatetrack.manager.NodesManager;
import org.una.navigatetrack.utils.Drawer;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public abstract class BaseController implements Initializable {
    @FXML
    private AnchorPane mainAnchorPane;
    @FXML
    private Pane mapPane, paintPane, menuPane;

    protected NodesDrawerManager manager;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        manager = new NodesDrawerManager(new NodesManager(), new Drawer(paintPane));
        setupUI();
        loadMenuPane("/fxml/UseMenuPane.fxml"); // Cargar el menú inicial
    }

    protected abstract void setupEventHandlers();

    protected void setupUI() {
        paintPane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.0);");
        loadImageMap("/images/map2.png");
    }

    protected boolean change = false;

    protected void loadImageMap(String path) {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
        ImageView imageView = new ImageView(image);
        double ratio = Math.min(670 / image.getWidth(), 950 / image.getHeight());
        imageView.setFitWidth(image.getWidth() * ratio);
        imageView.setFitHeight(image.getHeight() * ratio);
        imageView.setPreserveRatio(true);
        mapPane.getChildren().add(imageView);
        change = !change;
    }

    public void loadMenuPane(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Pane newMenuPane = loader.load();
            menuPane.getChildren().clear(); // Limpiar el menú actual
            menuPane.getChildren().add(newMenuPane); // Añadir el nuevo menú

            // Obtener el controlador del nuevo menú y llamar a su método de configuración
            BaseMenuController menuController = loader.getController();
            if (menuController != null) {
                menuController.setBaseController(this); // Pasar referencia al BaseController
                menuController.setupMenu(); // Llamar a configuración adicional si es necesario
            }
        } catch (IOException e ) {

        }
    }
}




