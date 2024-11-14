package org.una.navigatetrack.dto;

import lombok.Getter;
import lombok.Setter;
import org.una.navigatetrack.roads.Edge;

@Getter
@Setter
public class EdgeDTO {
    int edgeID;
    double weight;
    double[] pointStart;

    public EdgeDTO(Edge edge) {
        edgeID = edge.getID();
        weight = edge.getWeight();
        pointStart = edge.getStartingNode().getLocation();
    }
}
