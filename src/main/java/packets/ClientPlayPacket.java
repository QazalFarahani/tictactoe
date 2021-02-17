package packets;

import models.Spot;

import java.io.Serializable;

public class ClientPlayPacket implements Serializable {
    private static final long serialVersionUID = -2139241657738740883L;
    private int currentPlayer;
    private String clientId;
    private int[][] fields;
    private String enemyId;
    private Spot spot;

    public ClientPlayPacket(String clientId, String enemyId, int[][] fields, int currentPlayer, Spot spot) {
        this.clientId = clientId;
        this.enemyId = enemyId;
        this.fields = fields;
        this.currentPlayer = currentPlayer;
        this.spot = spot;
    }

    public String getClientId() {
        return clientId;
    }

    public int[][] getFields() {
        return fields;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public String getEnemyId() {
        return enemyId;
    }

    public Spot getSpot() {
        return spot;
    }
}
