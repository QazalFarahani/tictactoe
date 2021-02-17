package GameState;

import models.Enemy;
import models.Player;
import models.Spot;
import packets.*;
import utils.configs.GameConfig;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

import static utils.Converter.*;
import static utils.configs.GameConfig.getInstance;

public class ServerGame extends Thread {
    private ServerSocket serverSocket;
    private HashMap<String, ClientHandler> clients;
    private HashMap<String, Player> onlinePlayers;
    private ClientHandler playerToWait;
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();
    private ArrayList<String> topPlayersName;
    private ArrayList<String> topPlayersScore;

    private GameConfig config = getInstance("src/main/resources/configs/Game_Config.properties");


    public ServerGame() {
        try {
            serverSocket = new ServerSocket(config.getPort());
            clients = new HashMap<>();
            onlinePlayers = new HashMap<>();
            topPlayersName = new ArrayList<>();
            topPlayersScore = new ArrayList<>();
            createTopPlayers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(this, socket);
                clients.put(clientHandler.getClientId(), clientHandler);
                System.out.println("client with id  : " + clientHandler.getClientId() + " added");
                clientHandler.start();
                clientHandler.send(new IdPacket(clientHandler.getClientId()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void sendPacket(String clientId, String enemyId, int currentPlayer, int[][] fields, Spot spot) {
        if (!onlinePlayers.containsKey(enemyId))
            return;
        fields[spot.getX()][spot.getY()] = currentPlayer;
        if (checkWin(currentPlayer, fields)) {
            updateClientsScores(clientId, enemyId, fields, currentPlayer, spot);
            updateOnlinePlayersScore(clientId, enemyId);
            updateTopPlayers(clientId, enemyId);

            resetPlayersSituation(onlinePlayers.get(clientId));
            playerToJson(onlinePlayers.get(clientId));
            playerToJson(onlinePlayers.get(enemyId));
            sendTopPlayers();
        } else {
            currentPlayer = changePlayer(currentPlayer);
            clients.get(clientId).send(new UpdatePacket(fields, currentPlayer, spot));
            clients.get(enemyId).send(new UpdatePacket(fields, currentPlayer, spot));
        }
    }

    private void updateTopPlayers(String clientId, String enemyId) {
        int index = topPlayersName.indexOf(onlinePlayers.get(clientId).getName());
        topPlayersScore.set(index, String.valueOf(Integer.parseInt(topPlayersScore.get(index)) + 1));
        int index1 = topPlayersName.indexOf(onlinePlayers.get(enemyId).getName());
        topPlayersScore.set(index1, String.valueOf(Integer.parseInt(topPlayersScore.get(index1)) - 1));
    }

    private void updateOnlinePlayersScore(String clientId, String enemyId) {
        onlinePlayers.get(clientId).setScore(onlinePlayers.get(clientId).getScore() + 1);
        onlinePlayers.get(clientId).setWins(onlinePlayers.get(clientId).getWins() + 1);
        onlinePlayers.get(enemyId).setScore(onlinePlayers.get(enemyId).getScore() - 1);
        onlinePlayers.get(enemyId).setLoses(onlinePlayers.get(enemyId).getLoses() + 1);
    }

    private void updateClientsScores(String clientId, String enemyId, int[][] fields, int currentPlayer, Spot spot) {
        clients.get(clientId).send(new UpdatePacket(fields, currentPlayer, spot));
        clients.get(enemyId).send(new UpdatePacket(fields, currentPlayer, spot));
        clients.get(clientId).send(new UpdateResultPacket(String.valueOf(onlinePlayers.get(clientId).getWins() + 1),
                String.valueOf(onlinePlayers.get(clientId).getLoses()), String.valueOf(onlinePlayers.get(clientId).getScore() + 1), "YOU WON!"));
        clients.get(enemyId).send(new UpdateResultPacket(String.valueOf(onlinePlayers.get(enemyId).getWins()),
                String.valueOf(onlinePlayers.get(enemyId).getLoses() + 1), String.valueOf(onlinePlayers.get(enemyId).getScore() - 1), "YOU LOST!"));
    }

    private int changePlayer(int currentPlayer) {
        return (currentPlayer == 1) ? 2 : 1;
    }

    static String generateNewToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    void handleLoginRequest(LoginPacket loginPacket) {
        System.out.println("handling login request in server for : " + loginPacket.getClientId());
        Player player = new Player(loginPacket.getName(), loginPacket.getPassword());
        if (!loginPacket.isNewAcc()) {
            if (isValidName(loginPacket.getName())) {
                creatNewAcc(loginPacket, player);
                System.out.println("new player with id : " + loginPacket.getClientId() + " added");
            } else
                System.out.println("this name is taken");
        } else {
            if (isVerified(loginPacket.getName(), loginPacket.getPassword())) {
                for (String string : onlinePlayers.keySet()) {
                    if (onlinePlayers.get(string).getName().equals(loginPacket.getName()))
                        return;
                }
                signIn(loginPacket, player);
                System.out.println("player with id : " + loginPacket.getClientId() + " signed in");
            } else
                System.out.println("wrong user or password!");
        }
    }

    private void signIn(LoginPacket loginPacket, Player player) {
        player = playerFromJson(loginPacket.getName());
        addPlayer(loginPacket, player);
        sendTopPlayers();
    }

    private void creatNewAcc(LoginPacket loginPacket, Player player) {
        addPlayer(loginPacket, player);
        addPlayerToTop(player);
        sendTopPlayers();
    }

    private void resetPlayersSituation(Player player) {
        for (int i = 0; i < topPlayersScore.size(); i++) {
            if (player.getScore() > Integer.parseInt(topPlayersScore.get(i))) {
                int index = topPlayersName.indexOf(player.getName());
                topPlayersScore.add(i, String.valueOf(player.getScore()));
                topPlayersName.add(i, String.valueOf(player.getName()));
                topPlayersScore.remove(index + 1);
                topPlayersName.remove(index + 1);
            }
        }
    }

    void removePlayer(String clientId) {
        System.out.println("player with id : " + clientId + " removed");
        clients.remove(clientId);
        if (playerToWait != null && playerToWait.getClientId().equals(clientId))
            playerToWait = null;
        onlinePlayers.remove(clientId);
        sendTopPlayers();
    }

    private void addPlayerToTop(Player player) {
        topPlayersName.add(player.getName());
        topPlayersScore.add(String.valueOf(player.getScore()));
        sendTopPlayers();
    }

    private void addPlayer(LoginPacket loginPacket, Player player) {
        AuthenticationPacket packet = new AuthenticationPacket(player);
        onlinePlayers.put(loginPacket.getClientId(), player);
        playerToJson(player);
        clients.get(loginPacket.getClientId()).send(packet);
    }

    private void createTopPlayers() {
        ArrayList<String> players = directoryFileList("json/player");
        ArrayList<Player> all = new ArrayList<>();
        Player p;
        for (String string : players) {
            string = string.substring(0, string.length() - 5);
            p = playerFromJson(string);
            all.add(p);
        }
        for (int i = 0; i < all.size() - 1; i++) {
            for (int j = i + 1; j < all.size(); j++) {
                if (all.get(j).getScore() > all.get(i).getScore()) {
                    Player player = all.get(j);
                    all.add(i, player);
                    all.remove(j + 1);
                }
            }
        }
        all.forEach(player -> topPlayersName.add(String.valueOf(player.getName())));
        all.forEach(player -> topPlayersScore.add(String.valueOf(player.getScore())));
    }

    private void sendTopPlayers() {
        ArrayList<String> online = new ArrayList<>();
        onlinePlayers.keySet().forEach(id -> online.add(onlinePlayers.get(id).getName()));

        for (String string : onlinePlayers.keySet()) {
            clients.get(string).send(new UpdateStatus(topPlayersName, topPlayersScore, online));
        }
    }

    void setUpAGame(PlayRequestPacket packet) {
        if (playerToWait != null) {
            if (playerToWait.getClientId().equals(packet.getClientId())) {
                playerToWait = null;
                return;
            }
            Enemy enemy = new Enemy(onlinePlayers.get(playerToWait.getClientId()).getName(), playerToWait.getClientId());
            clients.get(packet.getClientId()).send(new StartGamePacket(2, 1, enemy, new int[7][7]));
            Enemy enemy1 = new Enemy(onlinePlayers.get(packet.getClientId()).getName(), packet.getClientId());
            clients.get(playerToWait.getClientId()).send(new StartGamePacket(1, 1, enemy1, new int[7][7]));
            System.out.println("game has started for clients with id : " + packet.getClientId() + " " + playerToWait.getClientId());
            playerToWait = null;
        } else {
            playerToWait = clients.get(packet.getClientId());
            System.out.println("client with id : " + packet.getClientId() + " added to playersToWait");
        }
    }

    private boolean isVerified(String name, String password) {
        if (!isValidName(name)) {
            Player player = playerFromJson(name);
            return player.getPassword().equals(password);
        } else
            return false;
    }

    private boolean isValidName(String name) {
        String playersName = name + ".json";
        ArrayList<String> players = directoryFileList("json/player");
        for (String string : players) {
            if (string.equals(playersName))
                return false;
        }
        return true;
    }

    private boolean checkWin(int currentPlayer, int[][] fields) {
        int playerCount;
        for (int x = 0; x < 7; x++) {
            for (int k = 0; k < 4; k++) {
                playerCount = 0;
                for (int y = k; y < k + 4; y++) {
                    if (fields[x][y] == currentPlayer)
                        playerCount++;
                }
                if (playerCount == 4)
                    return true;
            }
        }
        for (int y = 0; y < 7; y++) {
            for (int k = 0; k < 4; k++) {
                playerCount = 0;
                for (int x = k; x < k + 4; x++) {
                    if (fields[x][y] == currentPlayer)
                        playerCount++;
                }
                if (playerCount == 4)
                    return true;
            }
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4 - i; j++) {
                playerCount = 0;
                for (int k = 0; k < 4; k++) {
                    if (fields[i + j + k][k + j] == currentPlayer)
                        playerCount++;
                }
                if (playerCount == 4) {
                    System.out.println(1);
                    return true;
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4 - i; j++) {
                playerCount = 0;
                for (int k = 0; k < 4; k++) {
                    if (fields[6 - i - j - k][6 - k - j] == currentPlayer)
                        playerCount++;
                }
                if (playerCount == 4) {
                    System.out.println(2);
                    return true;
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4 - i; j++) {
                playerCount = 0;
                for (int k = 0; k < 4; k++) {
                    if (fields[6 - i - j - k][k + j] == currentPlayer)
                        playerCount++;
                }
                if (playerCount == 4) {
                    System.out.println(3);
                    return true;
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4 - i; j++) {
                playerCount = 0;
                for (int k = 0; k < 4; k++) {
                    if (fields[i + j + k][6 - k - j] == currentPlayer)
                        playerCount++;
                }
                if (playerCount == 4) {
                    System.out.println(1);
                    return true;
                }
            }
        }
        return false;
    }
}
