package Tanks;

import processing.core.*;

import java.util.*;


/**
 * Represents a projectile in the Tanks game.
 */
public class Projectile {
    private float x;
    private float y;
    private float xVel;
    private float yVel;
    private int[] RGB;
    private ArrayList<TerrainPixelLine> pixelLines;
    private boolean isCollided;
    private boolean isExploded;
    private Level level;
    private final static float GRAVITY = 7.2f / 30;
    private float windSpeed = 0.0f;
    private final static int WIDTH = 8;
    private final static int RADIUS = 4;
    private Tank tank;

    private int explosionRadius = 30;

    private int collisionTime;

    /**
     * Constructs a Projectile object.
     *
     * @param x    the x-coordinate of the projectile
     * @param y    the y-coordinate of the projectile
     * @param RGB  the RGB color values of the projectile
     * @param tank the tank that fired the projectile
     */
    public Projectile(float x, float y, int[] RGB, Tank tank) {
        this.x = x;
        this.y = y;
        this.RGB = RGB;
        this.xVel = 0;
        this.yVel = 0;
        this.isCollided = false;
        this.isExploded = false;
        this.tank = tank;
    }

    /**
     * Sets the level for the projectile.
     *
     * @param level the level object
     */
    public void setLevel(Level level) {
        this.level = level;
        this.pixelLines = level.getPixelLines();
    }

    /**
     * Fires the projectile with the given velocities.
     *
     * @param xVel the x-velocity of the projectile
     * @param yVel the y-velocity of the projectile
     */
    public void fire(float xVel, float yVel) {
        this.xVel = xVel;
        this.yVel = yVel;
    }

    /**
     * Checks if the projectile has collided with any terrain or gone out of bounds.
     *
     * @param app the PApplet object
     * @return true if the projectile has collided, false otherwise
     */
    public boolean checkCollided(PApplet app) {
        // Check if the projectile is out of the valid area first
        if (this.x < -32 || this.x > 896 || this.y > 672) {
            this.isCollided = true;
            this.collisionTime = app.millis();
            System.out.println("Projectile is out of bounds!");
            return isCollided;
        }

        for (TerrainPixelLine pixelLine : pixelLines) {
            float px = pixelLine.getX();
            float py = pixelLine.getY();

            float distance1 = (float) Math.sqrt((px - this.x) * (px - this.x) + (py - this.y) * (py - this.y));
            float distance2 = (float) Math.sqrt((px - this.x) * (px - this.x) + (py - this.y) * (py - this.y) - 16);

            float tolerance1 = 0.2f;

            if (distance1 <= RADIUS + tolerance1) {
                this.isCollided = true;
                this.x = px;
                this.y = py;
                this.collisionTime = app.millis();
                return isCollided;
            } else if (distance2 <= RADIUS + tolerance1 && Math.abs(this.y - py) >= 4 && Math.abs(this.y - py) < 6) {
                this.isCollided = true;
                this.x = px;
                this.y = py;
                this.collisionTime = app.millis();
                return isCollided;
            }
        }

        return isCollided;
    }

    /**
     * Explodes the projectile, modifying the terrain if necessary.
     */
    public void explode() {
        if (!this.isCollided) {
            return;
        } else if (this.isExploded) {
            return;
        }

        int outerLeftX = (int) this.x - explosionRadius;
        int outerRightX = (int) this.x + explosionRadius;
        float outerLeftY = 0.0f;
        float outerRightY = 0.0f;

        int touchLeftX = 0;
        int touchRightX = 0;
        float touchLeftY = 0.0f;
        float touchRightY = 0.0f;

        int tolerance1 = 1;
        int tolerance2 = 3;

        // Find touching pixels and set outer boundary Y values
        for (TerrainPixelLine pixelLine : pixelLines) {
            int px = pixelLine.getX();
            float py = pixelLine.getY();
            double distance = Math.sqrt((px - this.x) * (px - this.x) + (py - this.y) * (py - this.y));

            if (px == outerLeftX) {
                outerLeftY = py;
            } else if (px == outerRightX) {
                outerRightY = py;
            }

            if ((distance >= explosionRadius - tolerance1 && distance <= explosionRadius + tolerance1
                    && Math.abs(this.y - py) <= tolerance1 * 2)
                    || (distance >= explosionRadius - tolerance2 && distance <= explosionRadius + tolerance2
                    && Math.abs(this.y - py) > tolerance1 * 2)) {
                if (px < this.x) {
                    touchLeftX = px;
                    touchLeftY = py;
                } else if (px > this.x) {
                    touchRightX = px;
                    touchRightY = py;
                }
            } 
            // if (touchLeftX !=0 && touchRightX != 0) {
            //     break;
            // }

        }

        for (TerrainPixelLine pixelLine : pixelLines) {
            int px = pixelLine.getX();
            float py = pixelLine.getY();

            if ((px >= outerLeftX && px <= touchLeftX && outerLeftY <= this.y && Math.abs(this.x - px) <= explosionRadius)
                    || (px >= touchRightX && px <= outerRightX && outerRightY <= this.y
                    && Math.abs(this.x - px) <= explosionRadius)) {
                float distanceDiff = (float) Math.sqrt(explosionRadius * explosionRadius - (this.x - px) * (this.x - px));
                float newHeight = py + distanceDiff * 2;
                pixelLine.setY(newHeight);
                pixelLine.setColHeight(640 - newHeight);
            } else if (px >= touchLeftX && px <= touchRightX && Math.abs(this.x - px) <= explosionRadius) {
                float newHeight = this.y + (float) Math.sqrt((float)explosionRadius * explosionRadius - (float)(this.x - px) * (this.x - px));
                pixelLine.setY(newHeight);
                pixelLine.setColHeight(640 - newHeight);
            }
        }

        this.isExploded = true;
    }

    /**
     * Updates the pixel lines in the level.
     */
    public void updatePixelLines() {
        this.level.setPixelLines(this.pixelLines);
    }

    /**
     * Updates the state of the projectile.
     *
     * @param app the PApplet object
     */
    public void tick(PApplet app) {
        if (!this.isCollided) {
            this.x += this.xVel;
            this.y -= this.yVel;
            this.yVel -= GRAVITY;
            this.xVel += windSpeed;
            this.checkCollided(app);
        } else {
            this.xVel = 0;
            this.yVel = 0;
        }
    }

    /**
     * Checks if the projectile has collided with terrain.
     *
     * @return true if the projectile has collided, false otherwise
     */
    public boolean isCollided() {
        return this.isCollided;
    }

    /**
     * Checks if the projectile has exploded.
     *
     * @return true if the projectile has exploded, false otherwise
     */
    public boolean isExploded() {
        return this.isExploded;
    }

    /**
     * Updates the wind speed.
     *
     * @param windSpeed the new wind speed
     */
    public void updateWindSpeed(float windSpeed) {
        this.windSpeed = windSpeed;
    }

    /**
     * Sets the x-coordinate of the projectile.
     *
     * @param x the new x-coordinate
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Sets the y-coordinate of the projectile.
     *
     * @param y the new y-coordinate
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Gets the x-coordinate of the projectile.
     *
     * @return the x-coordinate
     */
    public float getX() {
        return this.x;
    }

    /**
     * Gets the y-coordinate of the projectile.
     *
     * @return the y-coordinate
     */
    public float getY() {
        return this.y;
    }

    /**
     * Gets the tank that fired the projectile.
     *
     * @return the tank object
     */
    public Tank getTank() {
        return this.tank;
    }

    /**
     * Draws the explosion of the projectile.
     *
     * @param app the PApplet object
     */
    public void drawExplosion(PApplet app) {
        if (this.isCollided) {
            int duration = 200;
            float maxRedRadius = explosionRadius;
            float maxOrangeRadius = explosionRadius * 0.5f;
            float maxYellowRadius = explosionRadius * 0.2f;

            int elapsedTime = app.millis() - this.collisionTime;

            if (elapsedTime <= duration) {
                float redRadius = PApplet.map(elapsedTime, 0, duration, 0, maxRedRadius);
                float orangeRadius = PApplet.map(elapsedTime, 0, duration, 0, maxOrangeRadius);
                float yellowRadius = PApplet.map(elapsedTime, 0, duration, 0, maxYellowRadius);

                app.fill(255, 0, 0);
                app.ellipse(this.x, this.y, redRadius * 2, redRadius * 2);

                app.fill(255, 165, 0);
                app.ellipse(this.x, this.y, orangeRadius * 2, orangeRadius * 2);

                app.fill(255, 255, 0);
                app.ellipse(this.x, this.y, yellowRadius * 2, yellowRadius * 2);
            }
        }
    }

    /**
     * Sets the explosion radius of the projectile.
     *
     * @param explosionRadius the new explosion radius
     */
    public void setExplosionRadius(int explosionRadius) {
        this.explosionRadius = explosionRadius;
    }

    /**
     * Gets the explosion radius of the projectile.
     *
     * @return the explosion radius
     */
    public int getExplosionRadius() {
        return this.explosionRadius;
    }

    /**
     * Draws the projectile on the screen.
     *
     * @param app the PApplet object
     */
    public void draw(PApplet app) {
        app.fill(RGB[0], RGB[1], RGB[2]);
        app.ellipse(this.x, this.y, WIDTH, WIDTH);

        app.fill(0, 0, 0);
        app.ellipse(this.x, this.y, 2, 2);
    }
}
