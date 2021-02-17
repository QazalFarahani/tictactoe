package packets;

import java.io.Serializable;

public class PlayRequestPacket implements Serializable {

    private static final long serialVersionUID = -3035908825596167515L;

    private String clientId;

    public PlayRequestPacket(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }

}
