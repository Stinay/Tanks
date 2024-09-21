package Tanks;

import processing.core.PApplet;


/**
 * Represents a single line of terrain pixels.
 */
public class TerrainPixelLine {

    private int x;
    private float y;
    private final static int COL_WIDTH = 1;
    private float colHeight;
    private int[] terrainRGB;
    private Tree tree;

    /**
     * Constructs a TerrainPixelLine object.
     *
     * @param x          the x-coordinate of the line
     * @param colHeight  the height of the line
     * @param terrainRGB the RGB values of the line's color
     */
    public TerrainPixelLine(int x, float colHeight, int[] terrainRGB) {
        this.x = x;
        this.colHeight = colHeight;
        this.y = 640 - colHeight;
        this.terrainRGB = terrainRGB;
    }

    /**
     * Sets the height of the line.
     *
     * @param height the new height of the line
     */
    public void setColHeight(float height) {
        this.colHeight = height;
    }

    /**
     * Sets the y-coordinate of the line.
     *
     * @param y the new y-coordinate of the line
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Returns the height of the line.
     *
     * @return the height of the line
     */
    public float getColHeight() {
        return this.colHeight;
    }

    /**
     * Returns the y-coordinate of the line.
     *
     * @return the y-coordinate of the line
     */
    public float getY() {
        return this.y;
    }

    /**
     * Returns the x-coordinate of the line.
     *
     * @return the x-coordinate of the line
     */
    public int getX() {
        return this.x;
    }

    /**
     * Sets the tree associated with the line.
     *
     * @param tree the tree object to be associated with the line
     */
    public void setTree(Tree tree) {
        this.tree = tree;
        this.tree.setX(this.x - 16);
        this.tree.setY(this.y - 29);
    }

    /**
     * Returns the tree associated with the line.
     *
     * @return the tree associated with the line
     */
    public Tree getTree() {
        return this.tree;
    }

    /**
     * Draws the terrain line on the screen.
     *
     * @param app the PApplet object used for drawing
     */
    public void draw(PApplet app) {
        app.noStroke();
        if (terrainRGB != null) {
            app.fill(terrainRGB[0], terrainRGB[1], terrainRGB[2]);
        }
        app.rect(x, y, COL_WIDTH, colHeight);
    }

    /**
     * Draws the tree associated with the line on the screen.
     *
     * @param app the PApplet object used for drawing
     */
    public void drawTree(PApplet app) {
        if (this.tree != null) {
            app.image(this.tree.getTreeImage(), x - 16, y - 29);
        }
    }

}
