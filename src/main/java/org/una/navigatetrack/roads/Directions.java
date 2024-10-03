package org.una.navigatetrack.roads;

public enum Directions {
    IZQUIERDA, DERECHA, ADELANTE, CONTRARIO;

    public Directions getOpposite() {
        return switch (this) {
            case IZQUIERDA -> DERECHA;
            case DERECHA -> IZQUIERDA;
            case ADELANTE -> CONTRARIO;
            case CONTRARIO -> ADELANTE;
            default -> throw new IllegalArgumentException("Dirección no válida");
        };
    }
}