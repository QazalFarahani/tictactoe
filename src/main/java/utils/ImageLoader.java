package utils;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class ImageLoader {

    static BufferedImage loadImage(String path) {
        try {
            File f = new File(path);
            return ImageIO.read(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
