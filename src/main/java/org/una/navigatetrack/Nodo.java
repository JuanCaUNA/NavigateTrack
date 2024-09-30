package org.una.navigatetrack;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class Nodo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; // Agrega un serialVersionUID
    private Nodo left, from, right;
    private int[] location;

    public Nodo() {
        left = from = right = null;
        location = new int[2];
    }
}

