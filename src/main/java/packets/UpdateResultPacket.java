package packets;

import java.io.Serializable;

public class UpdateResultPacket implements Serializable {

    private static final long serialVersionUID = 555442049407771265L;
    private String wins;
    private String loses;
    private String score;
    private String result;

    public UpdateResultPacket(String wins, String loses, String score, String result) {
        this.wins = wins;
        this.loses = loses;
        this.score = score;
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public String getWins() {
        return wins;
    }

    public String getScore() {
        return score;
    }

    public String getLoses() {
        return loses;
    }
}
