package org.una.navigatetrack.utils;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class Locate implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    double x, y;

    public Locate(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
