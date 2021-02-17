package packets;

import java.io.Serializable;
import java.util.ArrayList;

public class UpdateStatus implements Serializable {
    private static final long serialVersionUID = 5552946854077571516L;

    private ArrayList<String> topPlayersName;
    private ArrayList<String> topPlayersScore;
    private ArrayList<String> onlinePlayers;


    public UpdateStatus(ArrayList<String> topPlayersName, ArrayList<String> topPlayersScore, ArrayList<String> onlinePlayers) {
        this.topPlayersName = topPlayersName;
        this.topPlayersScore = topPlayersScore;
        this.onlinePlayers = onlinePlayers;
    }

    public ArrayList<String> getOnlinePlayers() {
        return onlinePlayers;
    }

    public ArrayList<String> getTopPlayersName() {
        return topPlayersName;
    }

    public ArrayList<String> getTopPlayersScore() {
        return topPlayersScore;
    }
}
