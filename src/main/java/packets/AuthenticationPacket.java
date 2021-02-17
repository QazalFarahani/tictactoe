package packets;

import models.Player;

import java.io.Serializable;

public class AuthenticationPacket implements Serializable {
    private static final long serialVersionUID = -1894195431129754665L;
    private Player player;

    public AuthenticationPacket(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
