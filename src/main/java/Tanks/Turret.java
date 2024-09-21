package Tanks;

import processing.core.PApplet;
import static java.lang.Math.PI;


/**
 * The Turret class represents a turret in a tank game.
 */
public class Turret {
    private final static int TURRET_WIDTH = 4;
    private final static int TURRET_HEIGHT = 15;
    private final static int[] RGB = new int[]{0, 0, 0};
    private float x;
    private float y;
    private float radian = (float) 0;
    private float radianVel = 0;

    /**
     * Sets the x-coordinate of the turret.
     *
     * @param x the x-coordinate to set
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Sets the y-coordinate of the turret.
     *
     * @param y the y-coordinate to set
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Gets the x-coordinate of the turret.
     *
     * @return the x-coordinate
     */
    public float getX() {
        return this.x;
    }

    /**
     * Gets the y-coordinate of the turret.
     *
     * @return the y-coordinate
     */
    public float getY() {
        return this.y;
    }

    /**
     * Gets the width of the turret.
     *
     * @return the width
     */
    public int getWidth() {
        return TURRET_WIDTH;
    }

    /**
     * Gets the height of the turret.
     *
     * @return the height
     */
    public int getHeight() {
        return TURRET_HEIGHT;
    }

    /**
     * Rotates the turret to the left.
     */
    public void rotateLeft() {
        this.radianVel = -3;
    }

    /**
     * Rotates the turret to the right.
     */
    public void rotateRight() {
        this.radianVel = 3;
    }

    /**
     * Stops the rotation of the turret.
     */
    public void stopRotation() {
        this.radianVel = 0;
    }

    /**
     * Gets the current radian value of the turret.
     *
     * @return the radian value
     */
    public float getRadian() {
        return this.radian;
    }

    /**
     * Updates the turret's rotation based on the radian velocity.
     */
    public void tick() {
        if (this.radian < -PI / 2) {
            this.radian = (float) -PI / 2;
        } else if (this.radian > PI / 2) {
            this.radian = (float) PI / 2;
        }
        this.radian += radianVel / 30;
    }

    /**
     * Draws the turret on the given PApplet.
     *
     * @param app the PApplet to draw on
     */
    public void draw(PApplet app) {
        app.pushMatrix(); // Save the current transformation matrix

        app.translate(x + (float) TURRET_WIDTH / 2, y + 15); // Move the origin to the center of the turret
        app.rotate(this.radian);

        app.fill(RGB[0], RGB[1], RGB[2]);
        app.rect(-(float) TURRET_WIDTH / 2, -(float) TURRET_HEIGHT / 2 - 8, TURRET_WIDTH, TURRET_HEIGHT); // Draw the turret centered at the new origin

        app.popMatrix(); // Restore the previous transformation matrix
    }
}
