import GameState.ClientGame;

import java.util.Scanner;

import static utils.Assets.loadImages;

public class ClientMain {
    public static void main(String[] args) {
        loadImages();
        new ClientGame();
    }
}
