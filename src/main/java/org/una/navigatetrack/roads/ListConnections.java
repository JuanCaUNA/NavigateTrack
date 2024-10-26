package org.una.navigatetrack.roads;

public class ListConnections {
    static int currentID = 0;
    static int currentIDNodes = 0;

    public static int getID() {
        currentID++;
        return currentID;
    }

    public static void incrementID(int h) {


    }

    public static int getIDNodes() {
        currentIDNodes++;
        return currentIDNodes;
    }
}
