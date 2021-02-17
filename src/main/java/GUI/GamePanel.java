package GUI;

import GameState.ClientGame;
import utils.Assets;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GamePanel extends JPanel {

    private ClientGame game;

    private JTextField name;
    private JTextField password;
    private JLabel nameLabel;
    private JLabel passwordLabel;
    private JButton login;
    private JButton play;
    private Checkbox hasLoggedIn;
    private String result;
    private JButton showRecorded;


    public GamePanel(ClientGame game) {
        this.game = game;
        addMouseListener(new Input());
        setSize(new Dimension(800, 800));
        init();
    }

    private void init() {
        setLayout(null);
        login = new JButton("LOGIN");
        play = new JButton("PLAY");
        showRecorded = new JButton("SHOW");
        showRecorded.setBounds(750, 60, 100, 30);
        login.setBounds(650, 80, 90, 30);
        play.setBounds(750, 20, 100, 30);
        login.addActionListener(e ->
        {
            if (name.getText().isEmpty() || password.getText().isEmpty())
                return;
            game.sendLoginRequest(name.getText(), password.getText(), hasLoggedIn.getState());
        });
        showRecorded.addActionListener(e ->
        {
            game.play();

        });
        play.addActionListener(e -> game.sendPlayRequest());
        name = new JTextField();
        name.setBounds(750, 20, 100, 20);
        password = new JTextField();
        password.setBounds(750, 40, 100, 20);
        nameLabel = new JLabel("NAME : ");
        nameLabel.setBounds(650, 20, 60, 20);
        passwordLabel = new JLabel("PASSWORD : ");
        passwordLabel.setBounds(650, 40, 100, 20);
        hasLoggedIn = new Checkbox("already have an account?");
        hasLoggedIn.setBounds(650, 60, 200, 20);
        add(login);
        add(name);
        add(password);
        add(nameLabel);
        add(passwordLabel);
        add(hasLoggedIn);
    }

    private void paintPlayerName(Graphics g) {
        if (game.getPlayer() != null)
            g.drawString(game.getPlayer().getName(), 620, 40);
    }

    private void paintEnemy(Graphics g) {
        if (game.getEnemy() != null)
            g.drawString(game.getEnemy().getName(), 620, 60);
    }

    private void paintImage(Graphics g) {
        if (game.getId() != 0)
            g.drawImage(Assets.xo[game.getId() - 1], 680, 30, 20, 20, null);
    }

    private void paintResult(Graphics g) {
        if (result == null)
            return;
        g.setColor(Color.RED);
        g.drawString(result, 300, 30);
        g.setColor(Color.BLACK);
    }

    private void paintScores(Graphics g) {
        if (game.getPlayer() == null)
            return;
        g.drawString("SCORE: " + game.getPlayer().getScore(), 620, 80);
        g.drawString("WINS: " + game.getPlayer().getWins(), 620, 100);
        g.drawString("LOSES: " + game.getPlayer().getLoses(), 620, 120);
    }

    private void paintOnlineNTopPlayers(Graphics g) {
        if (game.getOnlinePlayers() != null && game.getOnlinePlayers().size() != 0) {
            g.drawString("OnlinePlayers : ", 710, 160);
            for (int i = 0; i < game.getOnlinePlayers().size(); i++) {
                g.drawString(game.getOnlinePlayers().get(i), 710, 180 + 20 * i);
            }
        }
        if (game.getTopPlayersName() != null && game.getTopPlayersName().size() != 0) {
            g.drawString("TopPlayers : ", 620, 160);
            for (int i = 0; i < game.getTopPlayersName().size(); i++) {
                g.drawString(game.getTopPlayersName().get(i), 620, 180 + 20 * i);
            }
        }
        if (game.getTopPlayersScore() != null && game.getTopPlayersScore().size() != 0) {
            for (int i = 0; i < game.getTopPlayersScore().size(); i++) {
                g.drawString(game.getTopPlayersScore().get(i), 650, 180 + 20 * i);
            }
        }
    }

    public void loggedIn() {
        remove(login);
        remove(name);
        remove(password);
        remove(nameLabel);
        remove(passwordLabel);
        remove(hasLoggedIn);
        add(play);
        add(showRecorded);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        paintPlayerName(g);
        paintImage(g);
        paintEnemy(g);
        paintScores(g);
        paintOnlineNTopPlayers(g);
        paintPlayerName(g);
        Graphics2D graphics2D = (Graphics2D) g;
        ((Graphics2D) g).setStroke(new BasicStroke(10));
        for (int i = 85; i <= 595; i += 85) {
            graphics2D.drawLine(i, 0, i, 600);
        }
        for (int j = 85; j <= 595; j += 85) {
            graphics2D.drawLine(0, j, 595, j);
        }

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                int index = game.getFields()[i][j];
                if (index != 0)
                    graphics2D.drawImage(Assets.xo[index - 1], i * 85, j * 85, 85, 85, null);
            }
        }
        paintResult(g);
    }

    class Input extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1)
                game.inputReceived(e.getX() / 85, e.getY() / 85);
        }
    }

    public void setResult(String result) {
        this.result = result;
    }
}
