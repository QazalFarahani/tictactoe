package utils;

import java.awt.image.BufferedImage;

import static utils.ImageLoader.loadImage;

public class Assets {
    public static BufferedImage[] xo = new BufferedImage[2];

    public static void loadImages() {
        xo[0] = loadImage("src/main/resources/o.png");
        xo[1] = loadImage("src/main/resources/x.png");
    }
}
