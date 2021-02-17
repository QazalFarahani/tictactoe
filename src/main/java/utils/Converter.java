package utils;

import com.google.gson.Gson;
import models.Player;

import java.io.*;
import java.util.ArrayList;

public class Converter {

    public static Player playerFromJson(String name) {
        Player p = null;
        try {
            String jsonString = "";
            String line;
            BufferedReader reader = new BufferedReader(new FileReader("json/player/" + name + ".json"));
            while ((line = reader.readLine()) != null) {
                jsonString += line + '\n';
            }
            p = new Gson().fromJson(jsonString, Player.class);
        } catch (FileNotFoundException ex) {
            System.out.println("File not found : " + name);
        } catch (IOException ex) {
            System.out.println("Error while reading file : " + name);
        }
        return p;
    }

    public static ArrayList<String> directoryFileList(String dir) {
        File[] dirList = new File(dir).listFiles();
        ArrayList<String> fileNameList = new ArrayList<>();
        for (File file : dirList) {
            if (file.isFile()) {
                fileNameList.add(file.getName());
            }
        }
        return fileNameList;
    }

    public static void playerToJson(Player player) {
        String jsonString = new Gson().toJson(player);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("json/player/" + player.getName() + ".json"));
            writer.write(jsonString);
            writer.close();
        } catch (IOException ex) {
            System.out.println("Unable to open file : " + player.getName());
        }
    }
}
