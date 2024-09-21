package Tanks;

import java.util.ArrayList;

import static java.lang.Math.abs;

import processing.core.PApplet;
import processing.core.PImage;


/**
 * Represents a tank in the game.
 * Inherits from the Character class.
 */
public class Tank extends Chara {

    private int[] RGB;
    private final Boolean isHumanPlayer;
    private final static int BOTTOM_WIDTH = 20;
    private final static int BOTTOM_HEIGHT = 4;
    private final static int TOP_WIDTH = 14;
    private final static int TOP_HEIGHT = 4;

    private ArrayList<TerrainPixelLine> pixelLines;

    private int fuel = 250;
    private int health = 100;
    private int power = 50;
    private int score = 0;

    private float xVel = 0;
    private float yVel = 0;
    private int powerVel = 0;

    private Turret turret;

    private ArrayList<Projectile> projectiles;
    private int numOfProjectiles;

    private ArrayList<Parachute> parachutes;
    private int numOfParachutes = 3;
    private Parachute parachute;
    private PImage parachuteImage;

    private int damageTime;
    private int belowBottomTime;

    private boolean isAlive = true;
    private boolean isBelowBottom = false;
    private boolean onAir = false;
    private Projectile attackingProjectile;
    private boolean isExploded = false;

    private boolean canUseTeleport = false;
    private boolean isShieldUp = false;

    /**
     * Creates a tank with the specified x and y coordinates, character, RGB color values, and human player status.
     *
     * @param xCor          the x-coordinate of the tank
     * @param yCor          the y-coordinate of the tank
     * @param chara         the character of the tank
     * @param RGB           the RGB color values of the tank
     * @param isHumanPlayer a boolean value indicating whether the tank is controlled by a human player
     */
    public Tank(int xCor, int yCor, Character chara, int[] RGB, Boolean isHumanPlayer) {
        super(xCor, yCor, chara);
        this.RGB = RGB;
        this.isHumanPlayer = isHumanPlayer;
        this.turret = new Turret();
        this.turret.setX(this.x + BOTTOM_WIDTH / 2 - this.turret.getWidth() / 2);
        this.turret.setY(this.y - 32);
        this.pixelLines = new ArrayList<TerrainPixelLine>();
        this.projectiles = new ArrayList<Projectile>();
        this.parachutes = new ArrayList<Parachute>();
        this.attackingProjectile = null;
    }

    /**
     * Sets the shield status of the tank.
     *
     * @param isShieldUp a boolean value indicating whether the shield is up or not
     */
    public void setShield(boolean isShieldUp) {
        this.isShieldUp = isShieldUp;
    }


    /**
     * Sets the ability of the tank to use teleportation.
     *
     * @param canUseTeleport true if the tank can use teleportation, false otherwise
     */
    public void setTeleport(boolean canUseTeleport) {
        this.canUseTeleport = canUseTeleport;
    }


    /**
     * Returns a boolean value indicating whether the tank can use teleport.
     *
     * @return true if the tank can use teleport, false otherwise.
     */
    public boolean canUseTeleport() {
        return this.canUseTeleport;
    }

    /**
     * Teleports the tank to the specified position.
     *
     * @param x the x-coordinate of the teleport position
     * @param y the y-coordinate of the teleport position
     */
    public void teleport(float x, float y) {
        this.x = x;
        this.y = y;
        this.turret.setX(this.x + BOTTOM_WIDTH / 2 - this.turret.getWidth() / 2);
        this.turret.setY(this.y - BOTTOM_HEIGHT - 15);
        this.canUseTeleport = false;
    }

    /**
     * Increases the tank's score by the specified amount.
     *
     * @param score the amount by which to increase the score
     */
    public void gainScore(int score) {
        this.score += score;
    }


    /**
     * Returns whether the tank is controlled by a human player.
     *
     * @return true if the tank is controlled by a human player, false otherwise.
     */
    public boolean isHumanPlayer() {
        return this.isHumanPlayer;
    }

    /**
     * Checks for damage to the tank from the specified projectile.
     *
     * @param projectile the projectile to check for damage
     * @param app        the PApplet object
     */
    public void checkDamage(Projectile projectile, PApplet app) {
        if (projectile.isExploded()) {
            return;
        }

        if (projectile.isCollided() && this.x > projectile.getX() - 30 && this.x < projectile.getX() + 30 && this.y < projectile.getY()) {
            this.onAir = true;
            this.attackingProjectile = projectile;
        }

        float distance = PApplet.dist(projectile.getX(), projectile.getY(), this.x, this.y);
        if (distance <= 30 && projectile.isCollided()) {
            this.onAir = true;
            this.attackingProjectile = projectile;

            if (this.isShieldUp) {
                this.isShieldUp = false;
                return;
            }
            int damage = (int) ((30 - distance) / 30 * 60); // Corrected damage calculation
            System.out.println("Damage: " + damage);
            if (this.health > damage) {
                this.health -= damage;
                if (projectile.getTank() != this) {
                    projectile.getTank().gainScore(damage);
                }
            } else {
                if (projectile.getTank() != this) {
                    projectile.getTank().gainScore(this.health);
                }
                this.health = 0;
                this.isAlive = false;
                this.damageTime = app.millis();
            }
        }
        if (this.power > this.health) {
            this.power = this.health;
        }
    }

    /**
     * Checks the position of the tank.
     *
     * @param app the PApplet object
     */
    public void checkPosition(PApplet app) {
        if (this.y > 640) {
            this.isBelowBottom = true;
            this.belowBottomTime = app.millis();
        }
    }

    /**
     * Initializes the parachute with the specified image.
     *
     * @param parachuteImage the image to use for the parachute
     */
    public void initializeParachute(PImage parachuteImage) {
        this.parachuteImage = parachuteImage;
        this.parachute = new Parachute(this.x, this.y, this.parachuteImage);

        for (int i = 0; i < numOfParachutes; i++) {
            this.parachutes.add(parachute);
        }
    }

    /**
     * Adds a parachute to the tank.
     */
    public void addParachute() {
        if (this.score >= 15) {
            parachutes.add(parachute);
            this.numOfParachutes++;
            this.score -= 15;
        }

    }

    /**
     * Updates the location of the last projectile.
     */
    public void updateLatsProjectileLocation() {
        float pX = x + (BOTTOM_WIDTH) / 2;
        float pY = (y - BOTTOM_HEIGHT) - BOTTOM_HEIGHT;
        this.projectiles.get(numOfProjectiles - 1).setX(pX);
        this.projectiles.get(numOfProjectiles - 1).setY(pY);
    }

    /**
     * Adds a projectile to the tank.
     */
    public void addProjectile() {
        float pX = x + (BOTTOM_WIDTH) / 2;
        float pY = (y - BOTTOM_HEIGHT) - BOTTOM_HEIGHT;
        Projectile p = new Projectile(pX, pY, this.RGB, this);
        this.projectiles.add(p);
        this.numOfProjectiles++;
    }

    /**
     * Fires a projectile.
     */
    public void fireProjectile() {
        float radianAngle = this.turret.getRadian();
        float velocity = 60 + ((540 - 60) / 100) * this.power;

        // Calculate x and y components of the velocity
        float xVel = (float) (velocity * Math.sin(radianAngle)) / 30;
        float yVel = (float) (velocity * Math.cos(radianAngle)) / 30;

        // Fire the projectile
        this.projectiles.get(numOfProjectiles - 1).fire(xVel, yVel);
    }


    /**
     * Returns the last projectile.
     *
     * @return the last projectile
     */
    public Projectile getLastProjectile() {
        if (this.numOfProjectiles == 0) {
            return null;
        }
        return this.projectiles.get(numOfProjectiles - 1);
    }

    /**
     * Sets the level for the last projectile.
     *
     * @param level the level to set for the projectile
     */
    public void getLevelToProjectile(Level level) {
        if (this.numOfProjectiles > 0) {
            this.projectiles.get(numOfProjectiles - 1).setLevel(level);
        }
    }

    /**
     * Sets the wind speed for the last projectile.
     *
     * @param windSpeed the wind speed to set for the projectile
     */
    public void changeProjectileWindSpeed(float windSpeed) {
        if (this.numOfProjectiles > 0) {
            this.projectiles.get(numOfProjectiles - 1).updateWindSpeed(windSpeed);
        }
    }

    /**
     * Increases the tank's health.
     */
    public void increaseHealth() {
        int health = 20;
        if (this.health <= 80) {
            this.health += health;
        } else {
            this.health = 100;
        }
    }

    /**
     * Increases the tank's fuel.
     */
    public void additionalFuel() {
        this.fuel += 200;
    }


    /**
     * Decrease the tank's score by the specified amount.
     *
     * @param score the amount by which to increase the score
     */
    public void costScore(int score) {
        this.score -= score;
    }

    /**
     * Sets the tank's score to the specified value.
     *
     * @param score the value to set the score to
     */
    public void setScore(int score) {
        this.score = score;
    }


    /**
     * Draws the shield around the tank if it is up.
     *
     * @param app The PApplet object used for drawing.
     */
    public void drawShield(PApplet app) {
        if (this.isShieldUp) {
            app.fill(173, 120, 255, 100);
            app.ellipse(this.x + BOTTOM_WIDTH / 2, this.y - BOTTOM_HEIGHT * 2, 60, 60);
        }
    }


    /**
     * Checks if the tank is alive.
     *
     * @return true if the tank is alive, false otherwise.
     */
    public boolean isAlive() {
        return isAlive;
    }

    /**
     * Returns the tank's health.
     *
     * @return the tank's health
     */
    public int getHealth() {
        return this.health;
    }

    /**
     * Returns the tank's score.
     *
     * @return the tank's score
     */
    public int getScore() {
        return this.score;
    }

    /**
     * Returns the tank's RGB color values.
     *
     * @return the tank's RGB color values
     */
    public int[] getRGB() {
        return this.RGB;
    }


    /**
     * Sets the pixel lines for the tank.
     *
     * @param pixelLines the ArrayList of TerrainPixelLine objects representing the pixel lines
     */
    public void setPixelLines(ArrayList<TerrainPixelLine> pixelLines) {
        this.pixelLines = pixelLines;
    }

    /**
     * Returns the tank's power.
     *
     * @return the tank's power
     */
    public int getPower() {
        return this.power;
    }

    /**
     * Increases the tank's power.
     */
    public void powerIncrease() {
        if (this.power < this.health) {
            this.powerVel = 36 / 30;
        } else {
            this.powerVel = 0;
        }
    }

    /**
     * Decreases the tank's power.
     */
    public void powerDecrease() {
        if (this.power > 0) {
            this.powerVel = -36 / 30;
        } else {
            this.powerVel = 0;
        }
    }

    /**
     * Stops the tank's power.
     */
    public void stopPower() {
        this.powerVel = 0;
    }


    /**
     * Moves the tank left.
     **/
    public void moveLeft() {
        if (this.x >= 0) {
            this.xVel = -60 / 30;
        } else {
            this.xVel = 0;
        }
    }

    /**
     * Moves the tank right.
     */
    public void moveRight() {
        if (this.x <= 864) {
            this.xVel = 60 / 30;
        } else {
            this.xVel = 0;
        }

    }

    /**
     * Stops the tank's movement.
     */
    public void stopMove() {
        this.xVel = 0;
    }

    public void moveTurretLeft() {
        if (this.turret.getRadian() >= -Math.PI / 2) {
            this.turret.rotateLeft();
        }
    }

    /**
     * Moves the tank's turret right.
     */
    public void moveTurretRight() {
        if (this.turret.getRadian() <= Math.PI / 2) {
            this.turret.rotateRight();
        }
    }

    /**
     * Returns the tank's turret.
     *
     * @return the tank's turret
     */
    public Turret getTurret() {
        return this.turret;
    }

    /**
     * Returns the tank's fuel.
     *
     * @return the tank's fuel
     */
    public int getFuel() {
        return this.fuel;
    }


    /**
     * Returns the number of parachutes of the tank.
     *
     * @return the number of parachutes
     */
    public int getNumOfParachutes() {
        return this.numOfParachutes;
    }

    /**
     * Sets the tank's x velocity.
     */
    @Override
    public void setX(float x) {
        this.x = x;
        this.turret.setX(this.x + BOTTOM_WIDTH / 2 - this.turret.getWidth() / 2);
    }

    /**
     * Sets the tank's y velocity.
     */
    @Override
    public void setY(float y) {
        this.y = y;
        this.turret.setY(this.y - BOTTOM_HEIGHT - 15);
    }


    /**
     * Updates the state of the tank for each game tick.
     * This method handles the logic for tank movement, fuel consumption, collision detection, and health management.
     */
    public void tick() {
        // tick handle logic

        if (this.x < 0) {
            if (this.fuel > 0) {
                this.x = 0;
            } else {
                this.xVel = 0;
                System.out.println("Out of fuel");
            }
        } else if (this.x > 864 - 20) {
            if (this.fuel > 0) {
            this.x = 864 - 20;
            }else {
                this.xVel = 0;
                System.out.println("Out of fuel");
            }
        } else {
            if (this.onAir == false){
                if (this.fuel > 0) {
                    this.x += this.xVel;
                    this.fuel -= abs(xVel);
                    this.turret.setX(x + BOTTOM_WIDTH / 2 - this.turret.getWidth() / 2);
                    float plX = 0;
                    float plY = 0;
                    for (int i = 0; i < this.pixelLines.size(); i++) {
                        plX = (float) pixelLines.get(i).getX();
                        plY = (float) pixelLines.get(i).getY();
                        if (plX - 10 == x) {
                            this.y = plY;
                            break;
                        }
                    }
                    this.turret.setY(this.y - BOTTOM_HEIGHT - 15);
                } else {
                    this.xVel = 0;
                    System.out.println("Out of fuel");
                }
            } else {
                float descendY = 0;
                float plX = 0;
                float plY = 0;
                for (int i = 0; i < this.pixelLines.size(); i++) {
                    plX = (float) pixelLines.get(i).getX();
                    plY = (float) pixelLines.get(i).getY();
                    if (plX - 10 == this.x) {
                        descendY = plY;
                        break;
                    }
                }
                        
                // With parachute
                if (this.numOfParachutes > 0) {
                    int descendSpeed = 60/30;
                    if (descendY - this.y > descendSpeed && this.y < descendY) {
                        this.y += descendSpeed;
                    }else if (descendY - this.y <= descendSpeed && this.y < descendY) {
                        this.y = descendY;
                        this.onAir = false;
                        this.parachutes.remove(numOfParachutes - 1);
                        this.numOfParachutes--;
                    } else{
                        this.onAir = false;
                    }
                    if (this.health <= 0) {
                        this.health = 0;
                        this.isAlive = false;
                        this.damageTime = 0;
                    }
                } else {
                    // Without parachute
                    int descendSpeed = 120/30;
                    if (descendY - this.y > descendSpeed) {
                        this.y += descendSpeed;
                        this.health -= descendSpeed;
                        this.attackingProjectile.getTank().gainScore(descendSpeed);                            
                    }else {
                        int scoreGain = (int)(descendY - this.y);  // Calculate score based on the distance before changing y
                        this.y = descendY;
                        this.attackingProjectile.getTank().gainScore(scoreGain);
                        this.onAir = false;
                    }
                    if (this.health <= 0) {
                        this.health = 0;
                        this.isAlive = false;
                        this.damageTime = 0;
                    }
                }

                    
                this.turret.setX(x + BOTTOM_WIDTH / 2 - this.turret.getWidth() / 2);
                this.turret.setY(this.y - BOTTOM_HEIGHT - 15);
            }
        }


        if (this.power <= this.health && this.power >= 0) {
            this.power += this.powerVel;
        } else if (this.power > this.health) {
            this.power = this.health;
        } else if (this.power < 0) {
            this.power = 0;
        }

    }

    /**
     * Returns a boolean value indicating whether the tank has exploded.
     *
     * @return true if the tank has exploded, false otherwise.
     */
    public boolean isExploded() {
        return this.isExploded;
    }

    /**
     * Draws the tank explosion.
     *
     * @param app the PApplet object
     */
    public void drawTankExplosion(PApplet app) {
        int multiplier = 1;
        int time = this.damageTime;
        if (this.isBelowBottom) {
            multiplier = 2;
            time = this.belowBottomTime;
        }


        if (!this.isAlive || this.isBelowBottom) {
            int duration = 200;
            float maxRedRadius = 15 * multiplier;
            float maxOrangeRadius = 15 * 0.5f * multiplier;
            float maxYellowRadius = 6 * 0.2f * multiplier;

            int elapsedTime = app.millis() - time;

            if (elapsedTime <= duration) {
                float redRadius = PApplet.map(elapsedTime, 0, duration, 0, maxRedRadius);
                float orangeRadius = PApplet.map(elapsedTime, 0, duration, 0, maxOrangeRadius);
                float yellowRadius = PApplet.map(elapsedTime, 0, duration, 0, maxYellowRadius);

                app.fill(255, 0, 0);
                app.ellipse(this.x + BOTTOM_WIDTH / 2, this.y - 4, redRadius * 2, redRadius * 2);

                app.fill(255, 165, 0);
                app.ellipse(this.x + BOTTOM_WIDTH / 2, this.y - 4, orangeRadius * 2, orangeRadius * 2);

                app.fill(255, 255, 0);
                app.ellipse(this.x + BOTTOM_WIDTH / 2, this.y - 4, yellowRadius * 2, yellowRadius * 2);

            } else {
                this.isExploded = true;
            }
        }
    }

    /**
     * Draws the tank body.
     *
     * @param app the PApplet object
     */
    public void TankBody(PApplet app) {
        app.fill(this.RGB[0], this.RGB[1], this.RGB[2]);
        app.rect(x, y - BOTTOM_HEIGHT, BOTTOM_WIDTH, BOTTOM_HEIGHT);
        app.rect(x + (BOTTOM_WIDTH - TOP_WIDTH) / 2, y - BOTTOM_HEIGHT * 2, TOP_WIDTH, TOP_HEIGHT);
    }


    /**
     * Draws the tank, turret, shield, and parachutes.
     *
     * @param app the PApplet object
     */
    public void draw(PApplet app) {
        // Handle graphics

        if (this.isAlive) {
            if (this.onAir && this.numOfParachutes > 0) {
                this.parachutes.get(this.numOfParachutes - 1).setX(x);
                this.parachutes.get(this.numOfParachutes - 1).setY(y);
                this.parachutes.get(this.numOfParachutes - 1).draw(app);
            }

            this.turret.tick();
            this.turret.draw(app);

            this.TankBody(app);
            this.drawShield(app);
        }


    }
}
    


