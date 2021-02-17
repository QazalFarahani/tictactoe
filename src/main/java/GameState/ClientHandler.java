package GameState;

import packets.ClientPlayPacket;
import packets.LoginPacket;
import packets.PlayRequestPacket;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import static GameState.ServerGame.generateNewToken;

public class ClientHandler extends Thread {
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private ServerGame serverGame;
    private boolean running;
    private String clientId;

    public ClientHandler(ServerGame serverGame, Socket socket) {
        this.serverGame = serverGame;
        clientId = generateNewToken();
        running = true;
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (running) {
            try {
                Object object = inputStream.readObject();
                if (object instanceof ClientPlayPacket) {
                    ClientPlayPacket packet = (ClientPlayPacket) object;
                    serverGame.sendPacket(packet.getClientId(), packet.getEnemyId(), packet.getCurrentPlayer(), packet.getFields(), packet.getSpot());
                } else if (object instanceof LoginPacket) {
                    serverGame.handleLoginRequest((LoginPacket) object);
                } else if (object instanceof PlayRequestPacket) {
                    serverGame.setUpAGame((PlayRequestPacket) object);
                }
            } catch (SocketException | EOFException e) {
                System.out.println("client with id : " + clientId + " signed out");
                running = false;
                serverGame.removePlayer(clientId);
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(Object object) {
        try {
            outputStream.reset();
            outputStream.writeObject(object);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getClientId() {
        return clientId;
    }
}
