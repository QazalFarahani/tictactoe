package GameState;

import models.Spot;

import java.util.ArrayList;

public class GameRecorder extends Thread {
    private ArrayList<Spot> spots;
    private int[][] fields;
    private ClientGame game;
    private int currentSpot;
    private boolean running;

    public GameRecorder(ClientGame game) {
        this.game = game;
        spots = new ArrayList<>();
        currentSpot = 0;
        running = false;
    }

    @Override
    public void run() {
        Spot spot;
        int x;
        int y;
        while (true) {
            while (!running) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            spot = spots.get(currentSpot);
            x = spot.getX();
            y = spot.getY();
            game.paintFields(spot, fields[x][y]);
            currentSpot++;
            if (currentSpot == spots.size()) {
                running = false;
                game.stop();
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void clearSpots() {
        spots.clear();
    }

    public void setCurrentSpot(int currentSpot) {
        this.currentSpot = currentSpot;
    }

    public void setFields(int[][] fields) {
        this.fields = fields;
    }

    public void addSpot(Spot spot) {
        spots.add(spot);
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public int[][] getFields() {
        return fields;
    }

    public boolean isRunning() {
        return running;
    }
}
