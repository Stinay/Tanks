package Tanks;

import processing.data.JSONArray;
import processing.data.JSONObject;

import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.TreeMap;


/**
 * The Config class represents the configuration settings for the game.
 * It provides methods to retrieve player colors, levels, level layouts, and other game settings.
 */
public class Config {

    private JSONObject config;
    private JSONArray levels;
    private JSONObject playerCol;
    private TreeMap<String, int[]> humanPlayerColours = new TreeMap<String, int[]>();
    private TreeMap<String, int[]> aiPlayerColours = new TreeMap<String, int[]>();
    private int numberOfHumanPlayers;
    private int numberOfAIPlayers;

    public static Random random = new Random();

    /**
     * Constructs a Config object with the given configuration.
     *
     * @param config the JSON object representing the game configuration
     */
    public Config(JSONObject config) {
        this.config = config;
        this.levels = (JSONArray) config.get("levels");
        this.playerCol = (JSONObject) config.get("player_colours");
    }

    /**
     * Retrieves the player colors from the configuration and stores them in the appropriate data structures.
     */
    public void getPlayerColours() {
        for (Object key : this.playerCol.keys()) {
            String keyStr = (String) key;
            String RGB = this.playerCol.getString(keyStr);
            int[] RGBInt = new int[3];
            if (!"random".equals(RGB)) {
                String[] RGBArray = RGB.split(",");
                for (int i = 0; i < RGBArray.length; i++) {
                    RGBInt[i] = Integer.parseInt(RGBArray[i]);
                }
            } else {
                for (int i = 0; i < RGBInt.length; i++) {
                    int randColor = random.nextInt(256);
                    RGBInt[i] = randColor;
                }
            }

            if (keyStr.matches(".*[a-iA-I].*")) {
                this.humanPlayerColours.put(keyStr, RGBInt);
                this.numberOfHumanPlayers++;
            } else if (keyStr.matches("\\d+")) {
                this.aiPlayerColours.put(keyStr, RGBInt);
                this.numberOfAIPlayers++;
            }
        }
    }

    /**
     * Retrieves the player colors for human players.
     *
     * @return a TreeMap containing the player colors for human players
     */
    public TreeMap<String, int[]> getHumanPlayerColours() {
        return this.humanPlayerColours;
    }

    /**
     * Retrieves the player colors for AI players.
     *
     * @return a TreeMap containing the player colors for AI players
     */
    public TreeMap<String, int[]> getAIPlayerColours() {
        return this.aiPlayerColours;
    }

    /**
     * Retrieves the levels from the configuration.
     *
     * @return a JSONArray containing the levels
     */
    public JSONArray getLevels() {
        return this.levels;
    }

    /**
     * Retrieves the configuration for a specific level.
     *
     * @param level the level number
     * @return the JSONObject representing the configuration for the specified level
     */
    public JSONObject eachLevel(int level) {
        return (JSONObject) this.levels.get(level);
    }

    /**
     * Retrieves the foreground color for a specific level.
     *
     * @param level the level number
     * @return an array of integers representing the RGB values of the foreground color
     */
    public int[] getLevelForegorundRGB(int level) {
        String RGB = this.eachLevel(level).getString("foreground-colour");
        int[] RGBInt = new int[3];
        if (!"random".equals(RGB)) {
            String[] RGBArray = RGB.split(",");
            for (int i = 0; i < RGBArray.length; i++) {
                RGBInt[i] = Integer.parseInt(RGBArray[i]);
            }
        } else {
            for (int i = 0; i < RGBInt.length; i++) {
                int randColor = random.nextInt(256);
                RGBInt[i] = randColor;
            }
        }
        return RGBInt;
    }

    /**
     * Retrieves the layout for a specific level.
     *
     * @param level the level number
     * @return a 2D array of Chara objects representing the level layout
     */
    public Chara[][] getLevelLayout(int level) {
        String layoutPath = this.eachLevel(level).getString("layout");

        Chara[][] layout = new Chara[20][28];
        try {
            File f = new File(layoutPath);
            Scanner scan = new Scanner(f);
            for (int i = 0; i < 20; i++) {
                if (!scan.hasNextLine()) {
                    break;
                }
                char[] line = scan.nextLine().toCharArray();
                for (int j = 0; j < line.length && j < 28; j++) {
                    layout[i][j] = new Chara(i, j, line[j]);
                }
            }
            return layout;
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            return null;
        }
    }

    /**
     * Retrieves the path to the background image for a specific level.
     *
     * @param level the level number
     * @return the path to the background image
     */
    public String getBackgroundPath(int level) {
        String backgroundPath = this.eachLevel(level).getString("background");
        return "src/main/resources/Tanks/" + backgroundPath;
    }

    /**
     * Retrieves the path to the tree image for a specific level.
     *
     * @param level the level number
     * @return the path to the tree image, or null if no tree image is specified
     */
    public String getTreePath(int level) {
        if (this.eachLevel(level).getString("trees") != null) {
            String treePath = this.eachLevel(level).getString("trees");
            return "src/main/resources/Tanks/" + treePath;
        } else {
            return null;
        }
    }
}


