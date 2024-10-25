package org.una.navigatetrack.dto;

import lombok.Getter;
import lombok.Setter;
import org.una.navigatetrack.roads.Connection;
import org.una.navigatetrack.roads.Node;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class NodeDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final int MAX_CONNECTIONS = 4;
    private List<ConnectionDTO> connectionsDTO = new ArrayList<>();
    private double[] location;
    private double[] id; // ID como un array

    public NodeDTO(Node node) {
        this.location = node.getLocation();
        this.id = node.getLocation();

        if (node.getConnections() != null) {
            for (int i = 0; i < Math.min(node.getConnections().length, MAX_CONNECTIONS); i++) {
                if (node.getConnections()[i] != null) {
                    connectionsDTO.add(new ConnectionDTO(node.getConnections()[i]));
                }
            }
        }

    }

    public Node toNode() {
        Node node = new Node();
        node.setLocation(this.location);
//        node.setId(this.id); // Asignar el ID

        Connection[] connections = connectionsDTO.stream()
                .map(ConnectionDTO::toConnection)
                .toArray(Connection[]::new);
        node.setConnections(connections);
        return node;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("YourClassName{") // Reemplaza 'YourClassName' con el nombre real de la clase
                .append("MAX_CONNECTIONS=").append(MAX_CONNECTIONS)
                .append(", connectionsDTO=").append(connectionsDTO)
                .append(", location=").append(Arrays.toString(location))
                .append(", id=").append(Arrays.toString(id))
                .append('}');
        return sb.toString();
    }

}



