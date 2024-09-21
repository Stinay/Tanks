package Tanks;

import processing.core.PImage;
import processing.core.PApplet;

/**
 * The Parachute class represents a parachute object in the game.
 */
public class Parachute {

    private PImage parachuteImage;
    private final static int PARACHUTE_WIDTH = 64;
    private final static int PARACHUTE_HEIGHT = 64;
    private float x;
    private float y;

    /**
     * Constructs a Parachute object with the specified coordinates and image.
     *
     * @param x              the x-coordinate of the parachute
     * @param y              the y-coordinate of the parachute
     * @param parachuteImage the image of the parachute
     */
    public Parachute(float x, float y, PImage parachuteImage) {
        this.parachuteImage = parachuteImage;
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the x-coordinate of the parachute.
     *
     * @return the x-coordinate of the parachute
     */
    public float getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of the parachute.
     *
     * @return the y-coordinate of the parachute
     */
    public float getY() {
        return y;
    }

    /**
     * Sets the x-coordinate of the parachute.
     *
     * @param x the new x-coordinate of the parachute
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Sets the y-coordinate of the parachute.
     *
     * @param y the new y-coordinate of the parachute
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Draws the parachute on the screen.
     *
     * @param app the PApplet object used for drawing
     */
    public void draw(PApplet app) {
        app.image(parachuteImage, x - PARACHUTE_WIDTH / 2 + 10, y - PARACHUTE_HEIGHT - 4, PARACHUTE_WIDTH, PARACHUTE_HEIGHT);
    }
}
