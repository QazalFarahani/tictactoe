package GameState;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class Connection implements Runnable{
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private boolean running;
    private ClientGame game;

    Connection(ClientGame game, Socket socket) {
        this.game = game;
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(this).start();
    }

    public void sendPacket(Object object) {
        try {
            outputStream.reset();
            outputStream.writeObject(object);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            try {
                Object object = inputStream.readObject();
                game.packetReceived(object);

            } catch (EOFException | SocketException e) {
                running = false;
            }
            catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        running = false;
        try {
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
