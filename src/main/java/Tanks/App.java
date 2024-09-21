package Tanks;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.util.*;

/**
 * The main class that represents the game application.
 * Extends the PApplet class from the Processing library.
 */
public class App extends PApplet {

    public static final int CELL_SIZE = 32; //8;
    public static final int CELL_HEIGHT = 32;

    public static final int CELL_AVG = 32;
    public static final int TOPBAR = 0;
    public static int WIDTH = 864; // CELL_SIZE * BOARD_WIDTH;
    public static int HEIGHT = 640; // BOARD_HEIGHT * CELL_SIZE + TOPBAR;
    public static final int BOARD_WIDTH = WIDTH / CELL_SIZE;
    public static final int BOARD_HEIGHT = 20;

    public static final int INITIAL_PARACHUTES = 1;

    public static final int FPS = 30;

    public static final String CONFIG_PATH = "config.json";

    public static Random random = new Random();

    // Feel free to add any additional methods or attributes you want. Please put classes in different files.
    private Tank tank;
    private int numOfTurn;
    private int currentTurn = 0;

    private ArrayList<Character> playerCharacters;
    private HashMap<Character, Tank> players;
    private int currentPlayerIndex = 0;
    private Tank currentPlayer;
    private int totalPlayerNum;
    private int currentPlayerStartTime;

    private Wind wind;
    private PImage parachuteImage;
    private PImage fuelImage;
    private PImage windImageLeft;
    private PImage windImageRight;


    private Level[] levelArray;

    private boolean isGameOver = false;

    private int lastUpdateTime = 0;

    // Class-level variable to keep track of which player's score is currently displayed
    private int displayedPlayerCount = 0;

    /**
     * Constructs a new App instance, initializing game components and settings.
     */
    public App() {
        // Construct objects here
    }

    /**
     * Initialise the setting of the window size.
     */
    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player and map elements.
     */
    @Override
    public void setup() {
        frameRate(FPS); // how many frames per second
        JSONObject configObj = loadJSONObject(CONFIG_PATH);
        Config config = new Config(configObj);
        JSONArray levels = config.getLevels();

        this.numOfTurn = levels.size();
        this.levelArray = new Level[numOfTurn];

        this.parachuteImage = loadImage("src/main/resources/Tanks/parachute.png");

        // Get human tank colours
        config.getPlayerColours();
        TreeMap<String, int[]> tankRGB = config.getHumanPlayerColours();
        // Get AI tank colours
        TreeMap<String, int[]> aiRGB = config.getAIPlayerColours();

        this.parachuteImage = loadImage("src/main/resources/Tanks/parachute.png");
        this.wind = new Wind(this.loadImage("src/main/resources/Tanks/wind-1.png"), this.loadImage("src/main/resources/Tanks/wind.png"));
        this.fuelImage = loadImage("src/main/resources/Tanks/fuel.png");
        this.windImageLeft = loadImage("src/main/resources/Tanks/wind-1.png");
        this.windImageRight = loadImage("src/main/resources/Tanks/wind.png");


        this.players = new HashMap<Character, Tank>();

        for (int i = 0; i < levelArray.length; i++) {
            // Load background image
            String backgroundPath = config.getBackgroundPath(i);
            PImage background = loadImage(backgroundPath);

            // Create characters matrix
            Chara[][] layout = config.getLevelLayout(i);

            int[] foregroundRGB = config.getLevelForegorundRGB(i);

            // Get tree image
            String treePath = config.getTreePath(i);
            PImage tree = null;
            if (treePath != null) {
                tree = loadImage(treePath);
            }


            // Create level
            levelArray[i] = new Level(layout, foregroundRGB, background, tree, this.parachuteImage);
            levelArray[i].createTerrain();
            levelArray[i].smoothTerrain();
            levelArray[i].smoothTerrain();
            levelArray[i].createTree();
            levelArray[i].loadInitialTank(tankRGB, aiRGB);
            levelArray[i].createPlayerList();


            if (i == 0) {
                this.currentPlayerIndex = 0;
                this.players = levelArray[i].getPlayers();
                this.playerCharacters = levelArray[i].getPlayerCharacters();
                this.currentPlayer = levelArray[i].getCurrentPlayer();
                this.totalPlayerNum = playerCharacters.size();
                this.currentPlayerStartTime = millis();
            }

        }

    }

    /**
     * Receive key pressed signal from the keyboard.
     */
    @Override
    public void keyPressed(KeyEvent event) {

        int keyPressed = event.getKeyCode();

        switch (keyPressed) {

            case (37):
                // ArrowLeft
                if (this.currentPlayer.getX() > 0) {
                    this.currentPlayer.moveLeft();
                }
                System.out.println("LEFT");
                break;


            case (39):
                // ArrowRight
                if (this.currentPlayer.getX() < WIDTH - 20) {
                    this.currentPlayer.moveRight();
                }
                System.out.println("RIGHT");

                break;
            case (38):
                // ArrowUp
                if (this.currentPlayer.getTurret().getRadian() > -PI / 2) {
                    this.currentPlayer.moveTurretLeft();
                }
                System.out.println("UP");
                break;

            case (40):
                // ArrowDown
                if (this.currentPlayer.getTurret().getRadian() < PI / 2) {
                    this.currentPlayer.moveTurretRight();
                }
                System.out.println("Down");
                break;

            case (32):
                // Space
                this.currentPlayer.stopMove();
                this.wind.updateWind();

                int countAlive=0;
                for (Tank t : players.values()) {
                    if(!t.isExploded()){
                        countAlive++;
                    }
                }
                if (countAlive == 1 && this.currentTurn < this.numOfTurn  && this.levelArray[this.currentTurn].isLevelOver()){
                    this.ToNextLevel(countAlive);
                } else if (countAlive == 1 && this.currentTurn == this.numOfTurn - 1) {
                    System.out.println("Game Over");
                    System.out.println("Winner is: " + this.currentPlayer.getCharacter());
                    break;
                }                

                if (this.currentPlayer.isAlive() && !this.levelArray[this.currentTurn].isLevelOver()){
                    if (this.currentPlayer.getLastProjectile()!=null && this.currentPlayer.getLastProjectile().getExplosionRadius() == 30){
                        this.currentPlayer.addProjectile();
                    } else if (this.currentPlayer.getLastProjectile() == null){
                        this.currentPlayer.addProjectile();
                    } else{
                        this.currentPlayer.updateLatsProjectileLocation();
                    }
                        
                    
                    this.currentPlayer.getLevelToProjectile(this.levelArray[this.currentTurn]);
                    this.currentPlayer.fireProjectile();
    
                    this.currentPlayerIndex = (this.currentPlayerIndex + 1) % this.totalPlayerNum;
    
                    this.levelArray[this.currentTurn].addProjectile(this.currentPlayer.getLastProjectile());
                    
                    if (this.currentPlayer.getLastProjectile().getExplosionRadius() == 60){
                        this.currentPlayer.getLastProjectile().setExplosionRadius(30);
                    }
                }

                // Change windspeed for all players
                float windSpeed = this.wind.getWindSpeedPerFrame();
                players.keySet().forEach(key -> {
                    Tank t = players.get(key);
                    t.changeProjectileWindSpeed(windSpeed);
                });

                break;
            case (87):
                // w
                if (currentPlayer.getPower() < 100 && currentPlayer.getPower() >= 0 && currentPlayer.getPower() < currentPlayer.getHealth()) {
                    currentPlayer.powerIncrease();
                }

                System.out.println("W");
                System.out.println(currentPlayer.getPower());

                break;

            case (83):
                // s
                if (currentPlayer.getPower() > 0) {
                    currentPlayer.powerDecrease();
                }

                System.out.println("S");

                break;

            case (80):
                if (this.currentPlayer.getScore() >= 15) {
                    this.currentPlayer.addParachute();
                }
                break;

            case (88):
                //x
                if (this.currentPlayer.getScore() >= 20 && this.currentPlayer.getLastProjectile().getExplosionRadius() == 30) {
                    this.currentPlayer.costScore(20);
                    this.currentPlayer.addProjectile();
                    this.currentPlayer.getLastProjectile().setExplosionRadius(60);
                }
                break;

            case (82):
                //r
                if (this.isGameOver) {
                    this.currentTurn = 0;
                    this.setup();
                } else {
                    if (this.currentPlayer.getScore() >= 20) {
                        this.currentPlayer.costScore(20);
                        this.currentPlayer.increaseHealth();
                    }
                }

                break;

            case (70):
                //f

                if (this.currentPlayer.getScore() >= 10) {
                    this.currentPlayer.costScore(10);
                    this.currentPlayer.additionalFuel();
                }


                break;

            case (81):
                //q
                System.exit(0);
                break;

            case (84):
                //t
                if (this.currentPlayer.getScore() >= 15) {
                    this.currentPlayer.costScore(15);
                    this.currentPlayer.setTeleport(true);
                }
                break;

            case (72):
                //h
                if (this.currentPlayer.getScore() >= 20) {
                    this.currentPlayer.costScore(20);
                    this.currentPlayer.setShield(true);
                }
                break;

            default:
                break;
        }

        // keyReleased(keyPressed);

    }

    /**
     * Receive key released signal from the keyboard.
     */
    @Override
    public void keyReleased() {
        // multiple KeyReleased for specific function

    }

    /**
     * Handles key release events for game control. This method stops specific actions
     * of the current player based on the key released.
     * <p>
     * - Arrow keys up (38) and down (40): Stop the turret rotation.
     * - Arrow keys left (37) and right (39): Stop the player movement.
     * - W (87) and S (83) keys: Stop adjusting the power level.
     * - Spacebar (32): Records the current time to track the duration of key press.
     *
     * @param event the KeyEvent that contains details about the key that was released.
     */
    @Override
    public void keyReleased(KeyEvent event) {
        // to do something
        int keyCode = event.getKeyCode();
        if (keyCode == 38 || keyCode == 40) {
            this.currentPlayer.getTurret().stopRotation();
        } else if (keyCode == 37 || keyCode == 39) {
            this.currentPlayer.stopMove();
        } else if (keyCode == 87 || keyCode == 83) {
            this.currentPlayer.stopPower();
        } else if (keyCode == 32) {
            this.currentPlayerStartTime = millis();
        }

    }


    /**
     * Handles mouse press events to activate player power-ups, such as teleportation.
     * If the current player is alive and able to use teleport, this method will teleport
     * the player to the clicked location on the screen.
     * <p>
     * - Teleportation is performed by setting the player's position to the coordinates
     * of the mouse click, provided the player has the ability to teleport at that time.
     *
     * @param e the MouseEvent that contains details about the mouse button pressed and the
     *          coordinates of the mouse cursor at the time of the press.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        //TODO - powerups, like repair and extra fuel and teleport        
        if (this.currentPlayer.isAlive() && this.currentPlayer.canUseTeleport()) {
            float x = e.getX();
            float y = e.getY();
            this.currentPlayer.teleport(x, y);
        }
    }


    /**
     * Handles mouse release events to stop player power-ups.
     */
    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * Draw the HUD bar that displays the health and power of the current player.
     */
    public void drawHUDbar() {
        this.textSize(16);
        int[] tankRGB = this.currentPlayer.getRGB();


        int health = this.currentPlayer.getHealth();
        int power = this.currentPlayer.getPower();

        if (!this.currentPlayer.isAlive()) {
            health = 0;
            power = 0;
        }

        float healthWidth = 150 * (health / 100f);
        this.strokeWeight(2);
        this.stroke(0);
        this.fill(tankRGB[0], tankRGB[1], tankRGB[2]);
        this.rect(440, 10, healthWidth, 20);

        float powerWidth = 150 * (power / 100f);
        this.strokeWeight(4);
        this.stroke(127.5f);
        this.fill(tankRGB[0], tankRGB[1], tankRGB[2]);
        this.rect(440, 10, powerWidth, 20);


        this.strokeWeight(1);
        this.stroke(255, 0, 0);
        this.line(440 + powerWidth, 5, 440 + powerWidth, 35);


        this.fill(0, 0, 0);
        this.text("Health: ", 380, 25);
        this.text(health, 595, 25);
        this.text("Power: " + power, 380, 50);
    }

    /**
     * Draw the player's character and fuel level on the screen.
     */
    public void drawPlayerAndFuel() {
        // Arrow
        if (!this.currentPlayer.isAlive()) {
            return;
        }
        int duration = 2000;
        if (millis() - this.currentPlayerStartTime <= duration) {
            float arrowX = this.currentPlayer.getX() + 10;
            float arrowY = this.currentPlayer.getY() - 120;
            this.stroke(0);
            this.strokeWeight(2);
            this.line(arrowX, arrowY, arrowX, arrowY + 60);

            this.line(arrowX - 10, arrowY + 40, arrowX, arrowY + 60);
            this.line(arrowX + 10, arrowY + 40, arrowX, arrowY + 60);

        }


        char character = this.currentPlayer.getCharacter();
        this.fill(0, 0, 0);
        this.textSize(16);
        this.text("Player " + character + "'s turn", 20, 25);

        int fuel = this.currentPlayer.getFuel();
        this.image(fuelImage, 160, 5, 25, 25);
        this.fill(0, 0, 0);
        this.textSize(16);
        this.text(fuel, 185, 25);

        int numOfParachutes = this.currentPlayer.getNumOfParachutes();
        this.image(parachuteImage, 160, 35, 25, 25);
        this.fill(0, 0, 0);
        this.textSize(16);
        this.text(numOfParachutes, 187, 54);
    }

    /**
     * Draw the scoreboard that displays the scores of all players in the game.
     */
    public void drawScoreboard() {
        this.textSize(15.5f);

        this.strokeWeight(4);
        this.stroke(0);
        this.fill(0, 0, 0, 0);
        this.rect(720, 50, 135, 20);
        this.fill(0, 0, 0);
        this.text("Scores", 725, 66.5f);

        float scoreBoardHeight = 20f * (float) this.totalPlayerNum;
        this.fill(0, 0, 0, 0);
        this.rect(720, 70, 135, scoreBoardHeight);
        this.textSize(15);
        for (int i = 0; i < this.totalPlayerNum; i++) {
            char character = playerCharacters.get(i);
            Tank player = players.get(character);
            int[] tankRGB = player.getRGB();
            this.fill(tankRGB[0], tankRGB[1], tankRGB[2]);
            this.text("Player " + character, 725, 85 + 20 * i);
            this.fill(0, 0, 0);
            this.text(player.getScore(), 825, 85 + 20 * i);
        }
    }

    /**
     * Draw the larger projectile that is available for the current player to use.
     */
    public void drawLargerProjectile() {
        if (this.currentPlayer.getLastProjectile() != null && this.currentPlayer.isAlive()) {
            if (this.currentPlayer.getLastProjectile().getExplosionRadius() == 60) {
                this.strokeWeight(0);
                this.fill(255, 0, 0);
                this.ellipse(172, 75, 10 * 2, 10 * 2);

                this.fill(255, 165, 0);
                this.ellipse(172, 75, 5 * 2, 5 * 2);

                this.fill(255, 255, 0);
                this.ellipse(172, 75, 2 * 2, 2 * 2);


                this.fill(0, 0, 0);
                this.textSize(16);
                this.text("Larger projectile next", 187, 81);

            }
        }

    }


    /**
     * Transition to the next level of the game.
     *
     * @param countAlive the number of players that are still alive in the current level.
     */
    public void ToNextLevel(int countAlive) {
        if (countAlive == 1 && this.currentTurn < this.numOfTurn) {
            this.currentTurn++;
            this.currentPlayerIndex = 0;
            System.out.println("Level Over");
            if (this.currentTurn < this.levelArray.length) {
                HashMap<Character, Tank> newPlayers = this.levelArray[this.currentTurn].getPlayers();
                for (Tank t : newPlayers.values()) {
                    for (Tank p : players.values()) {
                        if (t.getCharacter().equals(p.getCharacter())) {
                            t.setScore(p.getScore());
                        }
                    }
                    t.initializeParachute(this.parachuteImage);
                }
                players = newPlayers;
                playerCharacters = new ArrayList<Character>(players.keySet());
                this.totalPlayerNum = playerCharacters.size();
            }
        }
    }

    /**
     * Draw the final scoreboard that displays the final scores of all players in the game.
     */
    public void drawFinalScoreboard() {
        this.textSize(26);

        this.strokeWeight(4);
        this.stroke(0);
        this.fill(255, 192, 203, 200);
        this.rect(242, 150, 380, 40);
        this.fill(0);
        this.text("Final Scores", 260, 180);

        float scoreBoardHeight = 35f * (float) this.totalPlayerNum;
        this.fill(255, 192, 203, 200);
        this.rect(242, 190, 380, scoreBoardHeight);
        this.textSize(26);

        // TreeMap to store scores in descending order and their corresponding players
        TreeMap<Integer, ArrayList<Character>> scoreMap = new TreeMap<>(Collections.reverseOrder());
        for (Tank t : players.values()) {
            scoreMap.computeIfAbsent(t.getScore(), k -> new ArrayList<>()).add(t.getCharacter());
        }


        int displayIndex = 0;
        for (Integer score : scoreMap.keySet()) {
            for (Character character : scoreMap.get(score)) {
                if (displayIndex >= displayedPlayerCount) {
                    break;
                }
                Tank player = players.get(character);
                int[] tankRGB = player.getRGB();
                this.fill(tankRGB[0], tankRGB[1], tankRGB[2]);

                if (displayIndex == 0) {
                    this.text("Player " + character + " wins!", 260, 130);
                }

                this.text("Player " + character, 260, 220 + 30 * displayIndex);
                this.fill(0, 0, 0);
                this.text(score, 550, 220 + 30 * displayIndex);
                displayIndex++;
                if (displayedPlayerCount == totalPlayerNum) {
                    this.isGameOver = true;
                    System.out.println("Game Over: " + isGameOver);
                }
            }
            if (displayIndex >= displayedPlayerCount) {
                break;
            }
        }

        // Update display count based on time delay
        if (millis() - lastUpdateTime > 700 && displayedPlayerCount < totalPlayerNum) {
            displayedPlayerCount++;
            lastUpdateTime = millis();
        }

    }


    /**
     * Draw all elements in the game by current frame.
     */
    @Override
    public void draw() {

        this.currentPlayer = players.get(playerCharacters.get(currentPlayerIndex));


        this.levelArray[this.currentTurn].draw(this);


        //----------------------------------
        //display HUD:
        //----------------------------------
        //TODO
        this.drawHUDbar();
        this.drawPlayerAndFuel();
        this.wind.draw(this);
        this.drawLargerProjectile();

        //----------------------------------
        //display scoreboard:
        //----------------------------------
        //TODO
        this.drawScoreboard();


        //----------------------------------
        //----------------------------------

        //TODO: Check user action
        if (!this.currentPlayer.isAlive()) {
            this.currentPlayerIndex = (this.currentPlayerIndex + 1) % this.totalPlayerNum;
            char currentPlayerChara = playerCharacters.get(currentPlayerIndex);
            this.currentPlayer = players.get(currentPlayerChara);
        }

        int countAlive = 0;
        for (Tank t : players.values()) {
            if (!t.isExploded()) {
                countAlive++;
            }
        }

        if (countAlive == 2 && this.currentTurn == this.numOfTurn - 1) {
            this.lastUpdateTime = millis();
        }


        if (countAlive == 1 && this.currentTurn < this.numOfTurn - 1 && this.levelArray[this.currentTurn].isLevelOver()) {
            //delay 1 second
            delay(1000);
            this.ToNextLevel(countAlive);

        } else if (countAlive <= 1 && this.currentTurn == this.numOfTurn - 1 && this.levelArray[this.currentTurn].isLevelOver()) {
            this.drawFinalScoreboard();
            System.out.println("Game Over");
            System.out.println("Winner is: " + this.currentPlayer.getCharacter());
        }

    }

    /**
     * Main method that runs the game application.
     *
     * @param args the command line arguments provided to the application.
     */
    public static void main(String[] args) {
        PApplet.main("Tanks.App");
    }

}
