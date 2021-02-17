package packets;

import models.Enemy;

import java.io.Serializable;

public class StartGamePacket implements Serializable {

    private static final long serialVersionUID = -980568814088429226L;

    private Enemy enemy;
    private int id;
    private int currentPlayer;
    private int[][] fields;

    public StartGamePacket(int id, int currentPlayer, Enemy enemy, int[][] fields) {
        this.enemy = enemy;
        this.id = id;
        this.currentPlayer = currentPlayer;
        this.fields = fields;
    }

    public int[][] getFields() {
        return fields;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public Enemy getEnemy() {
        return enemy;
    }

    public int getId() {
        return id;
    }
}
