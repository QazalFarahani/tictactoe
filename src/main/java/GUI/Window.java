package GUI;

import GameState.ClientGame;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Window extends JFrame {

    private ClientGame game;

    public Window(ClientGame game) {
        super("TicTacToe");
        this.game = game;
        setSize(900, 600 + 30);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        addWindowListener(new Listener());
    }

    class Listener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            game.close();
        }
    }
}
