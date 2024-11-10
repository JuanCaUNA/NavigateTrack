package org.una.navigatetrack.utils;

import javafx.stage.Stage;
import lombok.Getter;
import org.una.navigatetrack.manager.NodesManager;
import org.una.navigatetrack.manager.ScreenManager;

public class AppContext {
    private static AppContext instance;
    private NodesManager nodesManager;

    @Getter
    private ScreenManager screenManager;

    private AppContext() {
    }

    public static synchronized AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
        }
        return instance;
    }

    public NodesManager getNodesManager() {
        if (nodesManager == null) {
            nodesManager = new NodesManager();
        }
        return nodesManager;
    }

    public void loadScreen(String fxmlFileName) {
        screenManager.loadScreen(fxmlFileName, "Travel APP");
    }

    @SuppressWarnings("exports")
    public void defineScreen(Stage stage, String fxmlFileName) {
        screenManager = new ScreenManager(stage);
        loadScreen(fxmlFileName);
    }

    public void createNotification(String title, String message) {
        screenManager.createNotification(title, message);
    }
}

