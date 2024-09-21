package Tanks;

import processing.core.PApplet;
import processing.core.PImage;

import java.util.Random;


public class Wind {
    private PImage windImageLeft;
    private PImage windImageRight;
    private final static int WIND_WIDTH = 50;
    private final static int WIND_HEIGHT = 50;
    private int windSpeed;
    private Random random;

    /**
     * Constructs a Wind object with the given wind images.
     *
     * @param windImageLeft  The image for left wind direction.
     * @param windImageRight The image for right wind direction.
     */
    public Wind(PImage windImageLeft, PImage windImageRight) {
        random = new Random();
        windSpeed = random.nextInt(71) - 35;
        this.windImageLeft = windImageLeft;
        this.windImageRight = windImageRight;
    }

    /**
     * Updates the wind speed.
     */
    public void updateWind() {
        windSpeed += random.nextInt(11) - 5;
    }

    /**
     * Gets the wind speed per frame.
     *
     * @return The wind speed per frame.
     */
    public int getWindSpeedPerFrame() {
        return (int) (windSpeed * 0.03 / 30);
    }

    /**
     * Draws the wind on the screen.
     *
     * @param app The PApplet object to draw on.
     */
    public void draw(PApplet app) {
        app.textSize(18);

        if (windSpeed < 0) {
            app.image(windImageLeft, app.width - 100, 0, WIND_WIDTH, WIND_HEIGHT);
            app.fill(0, 0, 0);
            app.text(-(int) windSpeed, app.width - 43, 31);
        } else {
            app.image(windImageRight, app.width - 100, 0, WIND_WIDTH, WIND_HEIGHT);
            app.fill(0, 0, 0);
            app.text(windSpeed, app.width - 43, 31);
        }
    }
}