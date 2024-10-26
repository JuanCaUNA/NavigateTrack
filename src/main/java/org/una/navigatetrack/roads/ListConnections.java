package org.una.navigatetrack.roads;

public class ListConnections {
    static int currentID = -1;
    static int currentIDNodes = -1;

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
