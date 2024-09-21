package Tanks;

import processing.core.PImage;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;
import java.util.HashMap;


/**
 * Represents a level in the Tanks game.
 */
public class Level {

    private Chara[][] layout;
    private int[] foregroundColor;
    private PImage backgroundImage;
    private PImage treeImage;
    private final static int BACKGROUND_WIDTH = 864;
    private final static int BACKGROUND_HEIGHT = 640;
    private final static int CHARA_WIDTH = 32;
    private final static int CHARA_HEIGHT = 32;
    private TerrainColumn[] terrain;
    private ArrayList<TerrainPixelLine> pixelLines;
    private ArrayList<Tree> treesList;
    private TreeMap<String, Tank> humanPlayers;
    private TreeMap<String, Tank> aiPlayers;
    private ArrayList<Projectile> projectiles;
    private int numOfProjectiles = 0;

    private ArrayList<Character> playerCharacters;
    private HashMap<Character, Tank> players;
    private int currentPlayerIndex = 0;
    private Tank currentPlayer;
    private int totalPlayerNum;
    private PImage parachuteImage;
    private boolean isLevelOver = false;
    private int levelEndCounter = 0;


    /**
     * Constructs a Level object.
     *
     * @param layout          the layout of the level
     * @param foregroundColor the foreground color of the level
     * @param backgroundImage the background image of the level
     * @param treeImage       the tree image of the level
     * @param parachuteImage  the parachute image of the level
     */
    public Level(Chara[][] layout, int[] foregroundColor, PImage backgroundImage, PImage treeImage, PImage parachuteImage) {
        this.layout = layout;
        this.foregroundColor = foregroundColor;
        this.backgroundImage = backgroundImage;
        this.backgroundImage.resize(BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        this.treeImage = treeImage;
        if (treeImage != null) {
            this.treeImage.resize(CHARA_WIDTH, CHARA_HEIGHT);
        }
        this.terrain = new TerrainColumn[28];
        this.pixelLines = new ArrayList<TerrainPixelLine>();
        this.treesList = new ArrayList<Tree>();
        this.humanPlayers = new TreeMap<String, Tank>();
        this.aiPlayers = new TreeMap<String, Tank>();
        this.projectiles = new ArrayList<Projectile>();
        this.players = new HashMap<Character, Tank>();
        this.playerCharacters = new ArrayList<Character>();
        this.parachuteImage = parachuteImage;
    }

    /**
     * Prints the layout of the level.
     */
    public void getLayout() {
        for (int i = 0; i < layout.length; i++) {
            for (int j = 0; j < layout[i].length; j++) {
                if (layout[i][j] != null) {
                    System.out.print(layout[i][j].getCharacter());
                }
            }
            System.out.println();
        }
    }

    /**
     * Creates the terrain of the level.
     */
    public void createTerrain() {
        for (int i = 0; i < layout.length; i++) {
            for (int j = 0; j < layout[i].length; j++) {
                if (layout[i][j] != null) {
                    Character chara = layout[i][j].getCharacter();
                    if (chara == 'X') {
                        terrain[j] = new TerrainColumn(j, i, 'X', this.foregroundColor);
                    }
                }
            }
        }
        for (int i = 0; i < terrain.length; i++) {
            if (this.pixelLines != null && terrain[i].getTerrainPixelLines() != null) {
                this.pixelLines.addAll(terrain[i].getTerrainPixelLines());
            }
        }
    }

    /**
     * Smooths the terrain of the level.
     *
     * @return true if the terrain was smoothed, false otherwise
     */
    public boolean smoothTerrain() {
        if (pixelLines != null) {
            for (int i = 0; i < pixelLines.size() - 32; i++) {
                float heightSum = 0;
                for (int j = i + 1; j < i + 1 + 32 && j < pixelLines.size(); j++) {
                    heightSum = heightSum + pixelLines.get(j).getColHeight();
                }
                float movingAverage = heightSum / 32;
                pixelLines.get(i).setColHeight(movingAverage);
                pixelLines.get(i).setY(640 - movingAverage);
            }
            return true;
        }
        return false;
    }

    /**
     * Returns the pixel lines of the level.
     *
     * @return the pixel lines
     */
    public ArrayList<TerrainPixelLine> getPixelLines() {
        return this.pixelLines;
    }

    /**
     * Sets the pixel lines of the level.
     *
     * @param pixelLines the pixel lines to set
     */
    public void setPixelLines(ArrayList<TerrainPixelLine> pixelLines) {
        this.pixelLines = pixelLines;
    }

    /**
     * Creates the trees in the level.
     */
    public void createTree() {
        for (int i = 0; i < layout.length; i++) {
            for (int j = 0; j < layout[i].length; j++) {
                if (layout[i][j] != null) {
                    Character chara = layout[i][j].getCharacter();
                    if (chara == 'T' && this.treeImage != null) {
                        Tree tree = new Tree(j, i, 'T', this.treeImage);
                        resetTreePosition(tree);
                        setTreePixelRange(tree);
                        tree.setRandomPosition();
                        this.treesList.add(tree);
                    }
                }
            }
        }
    }

    /**
     * Resets the position of a tree in the level.
     *
     * @param tree the tree to reset the position of
     */
    public void resetTreePosition(Tree tree) {
        if (tree != null) {
            float plX = 0;
            float plY = 0;
            for (int i = 0; i < this.pixelLines.size(); i++) {
                plX = (float) pixelLines.get(i).getX();
                plY = pixelLines.get(i).getY();
                if (plX == tree.getX()) {
                    tree.setY(plY);
                    break;
                }
            }
        }
    }

    /**
     * Sets the pixel range of a tree in the level.
     *
     * @param tree the tree to set the pixel range of
     */
    public void setTreePixelRange(Tree tree) {
        if (this.pixelLines == null || tree == null) {
            return;
        } else {
            float treeX = tree.getX();
            float treeY = tree.getY();
            for (int i = 0; i < this.pixelLines.size() - 32; i++) {
                float plX = this.pixelLines.get(i).getX();
                float plY = this.pixelLines.get(i).getY();
                float distance = (float) Math.sqrt((plX - treeX) * (plX - treeX) + (plY - treeY) * (plY - treeY));
                if (distance <= 15) {
                    tree.addTreeRange(this.pixelLines.get(i));
                }
            }
        }
    }

    /**
     * Resets the position of a tank in the level.
     *
     * @param tank the tank to reset the position of
     */
    public void resetTankPosition(Tank tank) {
        if (tank != null) {
            float plX = 0;
            float plY = 0;
            for (int i = 0; i < this.pixelLines.size(); i++) {
                plX = (float) pixelLines.get(i).getX();
                plY = pixelLines.get(i).getY();
                if (plX == tank.getX()) {
                    tank.setX(plX - 10);
                    tank.setY(plY);
                    break;
                }
            }
        }
    }

    /**
     * Loads the initial tanks in the level.
     *
     * @param humanPlayerColours the colors of the human players
     * @param aiPlayerColours    the colors of the AI players
     */
    public void loadInitialTank(TreeMap<String, int[]> humanPlayerColours, TreeMap<String, int[]> aiPlayerColours) {
        for (int i = 0; i < layout.length; i++) {
            for (int j = 0; j < layout[i].length; j++) {
                if (layout[i][j] != null) {
                    Character chara = layout[i][j].getCharacter();
                    if (String.valueOf(chara).matches(".*[a-iA-I].*")) {
                        int[] RGB = humanPlayerColours.get(String.valueOf(chara));
                        Tank humanTank = new Tank(j, i, chara, RGB, true);
                        this.resetTankPosition(humanTank);
                        humanTank.setPixelLines(this.pixelLines);
                        humanPlayers.put(String.valueOf(chara), humanTank);
                    } else if (String.valueOf(chara).matches("\\d+")) {
                        int[] RGB = aiPlayerColours.get(String.valueOf(chara));
                        Tank aiTank = new Tank(j, i, chara, RGB, false);
                        this.resetTankPosition(aiTank);
                        aiTank.setPixelLines(this.pixelLines);
                        aiPlayers.put(String.valueOf(chara), aiTank);
                    }
                }
            }
        }
    }

    /**
     * Creates the player list for the level.
     */
    public void createPlayerList() {
        this.currentPlayerIndex = 0;
        playerCharacters = new ArrayList<Character>();
        humanPlayers.keySet().forEach(key -> {
            Tank t = humanPlayers.get(key);
            t.initializeParachute(this.parachuteImage);
            players.put(t.getCharacter(), t);
            playerCharacters.add(t.getCharacter());
        });
        aiPlayers.keySet().forEach(key -> {
            Tank t = aiPlayers.get(key);
            t.initializeParachute(parachuteImage);
            players.put(t.getCharacter(), t);
            playerCharacters.add(t.getCharacter());
        });
        char currentPlayerChara = playerCharacters.get(currentPlayerIndex);
        this.currentPlayer = players.get(currentPlayerChara);
        this.totalPlayerNum = playerCharacters.size();
    }

    /**
     * Returns the index of the current player.
     *
     * @return the current player index
     */
    public int getCurrentPlayerIndex() {
        return this.currentPlayerIndex;
    }

    /**
     * Returns the current player.
     *
     * @return the current player
     */
    public Tank getCurrentPlayer() {
        return this.currentPlayer;
    }

    /**
     * Returns the list of player characters.
     *
     * @return the player characters
     */
    public ArrayList<Character> getPlayerCharacters() {
        return this.playerCharacters;
    }

    /**
     * Returns the map of players.
     *
     * @return the players
     */
    public HashMap<Character, Tank> getPlayers() {
        return this.players;
    }

    /**
     * Returns the map of human players.
     *
     * @return the human players
     */
    public TreeMap<String, Tank> getHumanPlayers() {
        return this.humanPlayers;
    }

    /**
     * Returns the map of AI players.
     *
     * @return the AI players
     */
    public TreeMap<String, Tank> getAIPlayers() {
        return this.aiPlayers;
    }

    /**
     * Adds a projectile to the level.
     *
     * @param projectile the projectile to add
     */
    public void addProjectile(Projectile projectile) {
        this.projectiles.add(projectile);
        this.numOfProjectiles++;
    }

    /**
     * Checks if the level is over.
     *
     * @return true if the level is over, false otherwise
     */
    public boolean isLevelOver() {
        return this.isLevelOver;
    }

    /**
     * Draws the level.
     *
     * @param app the PApplet object to draw on
     */
    public void draw(PApplet app) {
        // Draw background
        app.image(this.backgroundImage, 0, 0);

        // Draw terrain
        for (int i = 0; i < pixelLines.size(); i++) {
            this.pixelLines.get(i).draw(app);
        }

        // Draw Tree on terrain
        for (int i = 0; i < pixelLines.size(); i++) {
            this.pixelLines.get(i).drawTree(app);
        }

        int countDie = 0;
        for (Tank t : players.values()) {
            if (t.isAlive()) {
                t.tick();
                t.draw(app);
                t.checkPosition(app);
            } else {
                t.drawTankExplosion(app);
                if (t.isExploded()) {
                    countDie++;
                }
            }
        }

        if (countDie == totalPlayerNum - 1) {
            levelEndCounter++;
            if (levelEndCounter == 2) {
                this.isLevelOver = true;
            }
        }

        if (this.isLevelOver == false){
        // Draw projectiles
            for (int i = 0; i < this.numOfProjectiles; i++) {
                Projectile p = this.projectiles.get(i);
                if (!p.isExploded()) {
                    p.tick(app);
                    if (p.isCollided() && !p.isExploded()) {
                        for (String key : humanPlayers.keySet()) {
                            humanPlayers.get(key).checkDamage(p, app);
                        }
                        for (String key : aiPlayers.keySet()) {
                            aiPlayers.get(key).checkDamage(p, app);
                        }
                        p.explode();
                    }
                    p.updatePixelLines();
                    if (p.getTank().isAlive()) {
                        p.draw(app);
                    }
                }
                p.drawExplosion(app);
            }
        }
    }
}
