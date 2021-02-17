package packets;

import java.io.Serializable;

public class IdPacket implements Serializable {
    private static final long serialVersionUID = 3115204498853565571L;
    private String id;

    public IdPacket(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
