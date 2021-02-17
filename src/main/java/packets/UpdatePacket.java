package packets;

import models.Spot;

import java.io.Serializable;

public class UpdatePacket implements Serializable {

    private static final long serialVersionUID = -689800932325375072L;

    private int[][] fields;
    private int currentPLayer;
    private Spot spot;

    public UpdatePacket(int[][] fields, int currentPLayer, Spot spot) {
        this.fields = fields;
        this.currentPLayer = currentPLayer;
        this.spot = spot;
    }

    public int[][] getFields() {
        return fields;
    }

    public int getCurrentPLayer() {
        return currentPLayer;
    }

    public Spot getSpot() {
        return spot;
    }
}
