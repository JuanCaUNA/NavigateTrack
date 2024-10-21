package org.una.navigatetrack.utils;

import lombok.Getter;
import lombok.Setter;
import org.una.navigatetrack.manager.NodesManager;
import org.una.navigatetrack.manager.ScreenManager;

public class Singleton {
    private static Singleton instance;
    private NodesManager nodesManager;

    @Getter
    @Setter
    private ScreenManager screenManager;

    private Singleton() {
    }

    public static synchronized Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }

    public NodesManager getNodesManager() {
        if (nodesManager == null) {
            nodesManager = new NodesManager();
        }
        return nodesManager;
    }
}

