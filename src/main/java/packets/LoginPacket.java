package packets;

import java.io.Serializable;

public class LoginPacket implements Serializable {
    private static final long serialVersionUID = -8939236787884497474L;

    private String name;
    private String password;
    private boolean newAcc;
    private String clientId;

    public LoginPacket(String clientId, String name, String password, boolean newAcc) {
        this.clientId = clientId;
        this.name = name;
        this.password = password;
        this.newAcc = newAcc;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public boolean isNewAcc() {
        return newAcc;
    }

    public String getClientId() {
        return clientId;
    }
}
