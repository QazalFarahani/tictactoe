package utils.configs;

import java.util.Properties;

public class Config extends Properties {
    public int readInteger(String name){
        try {
            return Integer.parseInt(this.getProperty(name));
        } catch (NumberFormatException ignore) {

        }
        return 0;
    }

}
