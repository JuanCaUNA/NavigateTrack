package org.una.navigatetrack.roads;

public class ListConnections {
    static int currentID = 0;

    public static int getID() {
        return currentID;
    }
    public static void incrementID() {
        currentID++;
    }
}
