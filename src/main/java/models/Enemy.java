package models;

import java.io.Serializable;

public class Enemy implements Serializable {
    private static final long serialVersionUID = -8608406446492026966L;

    private String name;
    private String ClientId;

    public Enemy(String name, String ClientId) {
        this.name = name;
        this.ClientId = ClientId;
    }

    public String getClientId() {
        return ClientId;
    }

    public String getName() {
        return name;
    }
}
