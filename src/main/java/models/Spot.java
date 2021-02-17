package models;

import java.io.Serializable;

public class Spot implements Serializable {
    private static final long serialVersionUID = 5269631529857424135L;

    int x;
    int y;

    public Spot(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
