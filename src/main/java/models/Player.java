package models;

import java.io.Serializable;

public class Player implements Serializable {
    private static final long serialVersionUID = -3595318236547641733L;
    private String name;
    private String password;
    private int wins;
    private int loses;
    private int score;

    public Player(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public int getLoses() {
        return loses;
    }

    public int getScore() {
        return score;
    }

    public int getWins() {
        return wins;
    }

    public void setLoses(int loses) {
        this.loses = loses;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }
}
