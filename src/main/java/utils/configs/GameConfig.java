package utils.configs;

import java.io.File;
import java.io.FileReader;

public class GameConfig extends Config{
    private int port;
    private static GameConfig gameConfig;

    private GameConfig(String adrs) {
        loadConfigs(adrs);
    }

    public static GameConfig getInstance(String adrs) {
        if (gameConfig == null)
            gameConfig = new GameConfig(adrs);
        return gameConfig;
    }

    private void loadConfigs(String adrs) {
        Config property = new Config();
        try {
            File test = new File(adrs);
            FileReader reader = new FileReader(test);
            property.load(reader);
            readProperties(property);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readProperties(Config property) {
        port = property.readInteger("port");
        if (port == 0)
            port = 8000;
    }

    public int getPort() {
        return port;
    }
}
