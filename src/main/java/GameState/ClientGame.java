package GameState;

import GUI.GamePanel;
import GUI.Window;
import models.Enemy;
import models.Player;
import models.Spot;
import packets.*;
import utils.configs.GameConfig;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import static utils.configs.GameConfig.getInstance;

public class ClientGame {
    private Socket socket;
    private Connection connection;
    private Window window;
    private GamePanel gamePanel;
    private Player player;
    private String ClientId;
    private int id;
    private int[][] fields = new int[7][7];
    private int currentPlayer;
    private Enemy enemy;
    private GameRecorder recorder;

    private ArrayList<String> topPlayersName;
    private ArrayList<String> topPlayersScore;
    private ArrayList<String> onlinePlayers;

    private GameConfig config = getInstance("src/main/resources/configs/Game_Config.properties");


    public ClientGame() {
        init();
        try {
            socket = new Socket("localhost", config.getPort());
            connection = new Connection(this, socket);
            onlinePlayers = new ArrayList<>();
            topPlayersName = new ArrayList<>();
            topPlayersScore = new ArrayList<>();
            recorder = new GameRecorder(this);
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendLoginRequest(String name, String password, boolean newAcc) {
        connection.sendPacket(new LoginPacket(ClientId, name, password, newAcc));
    }

    public void sendPlayRequest() {
        if (ClientId == null)
            return;
        connection.sendPacket(new PlayRequestPacket(ClientId));
    }

    private void init() {
        window = new Window(this);
        gamePanel = new GamePanel(this);
        window.setContentPane(gamePanel);
        window.setVisible(true);
    }

    public void inputReceived(int x, int y) {
        if (isMyTurn()) {
            Spot spot = updateField(x, y);
            if (enemy == null || spot == null)
                return;
            connection.sendPacket(new ClientPlayPacket(ClientId, enemy.getClientId(), fields, currentPlayer, spot));
        }
        gamePanel.repaint();
    }

    public void close() {
        try {
            connection.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Spot updateField(int x, int y) {
        if (x <= 6 && y <= 6)
            if (fields[x][y] == 0)
                return new Spot(x, y);
        return null;
    }

    void paintFields(Spot spot, int id) {
        fields[spot.getX()][spot.getY()] = id;
        gamePanel.repaint();
    }

    private boolean isMyTurn() {
        return currentPlayer == id;
    }

    void packetReceived(Object object) {
        if (object instanceof UpdatePacket) {
            receiveUpdatePacket(object);
        } else if (object instanceof AuthenticationPacket) {
            receiveAuthPacket(object);
        } else if (object instanceof IdPacket) {
            ClientId = ((IdPacket) object).getId();
            System.out.println("idPacket with id : " + ((IdPacket) object).getId() + "received");
        } else if (object instanceof StartGamePacket) {
            receiveStartGamePacket(object);
        } else if (object instanceof UpdateResultPacket) {
            receiveResultPacket(object);
        } else if (object instanceof UpdateStatus) {
            receiveStatusPacket(object);
        }
        gamePanel.repaint();
    }

    private void receiveStatusPacket(Object object) {
        topPlayersName = ((UpdateStatus) object).getTopPlayersName();
        topPlayersScore = ((UpdateStatus) object).getTopPlayersScore();
        onlinePlayers = ((UpdateStatus) object).getOnlinePlayers();
    }

    private void receiveResultPacket(Object object) {
        player.setWins(Integer.parseInt(((UpdateResultPacket) object).getWins()));
        player.setLoses(Integer.parseInt(((UpdateResultPacket) object).getLoses()));
        player.setScore(Integer.parseInt(((UpdateResultPacket) object).getScore()));
        gamePanel.setResult(((UpdateResultPacket) object).getResult());
        id = 0;
        currentPlayer = 0;
        recorder.setFields(fields);
    }

    private void receiveStartGamePacket(Object object) {
        StartGamePacket packet = (StartGamePacket) object;
        enemy = packet.getEnemy();
        id = packet.getId();
        gamePanel.setResult(null);
        recorder.setFields(null);
        recorder.clearSpots();
        stop();
        currentPlayer = ((StartGamePacket) object).getCurrentPlayer();
        if (((StartGamePacket) object).getFields() != null)
            fields = ((StartGamePacket) object).getFields();
    }

    private void receiveAuthPacket(Object object) {
        this.player = ((AuthenticationPacket) object).getPlayer();
        gamePanel.loggedIn();
    }

    private void receiveUpdatePacket(Object object) {
        UpdatePacket packet = (UpdatePacket) object;
        fields = packet.getFields();
        currentPlayer = packet.getCurrentPLayer();
        recorder.addSpot(packet.getSpot());
    }

    public int getId() {
        return id;
    }

    public int[][] getFields() {
        return fields;
    }

    public Player getPlayer() {
        return player;
    }

    public Enemy getEnemy() {
        return enemy;
    }

    public ArrayList<String> getTopPlayersScore() {
        return topPlayersScore;
    }

    public ArrayList<String> getTopPlayersName() {
        return topPlayersName;
    }

    public ArrayList<String> getOnlinePlayers() {
        return onlinePlayers;
    }

    public void play() {
        if (recorder.getFields() != null) {
            fields = new int[7][7];
            recorder.setRunning(true);
            recorder.interrupt();
        }
    }

    public void stop() {
        recorder.setCurrentSpot(0);
        recorder.setRunning(false);
    }

    public GameRecorder getRecorder() {
        return recorder;
    }
}
