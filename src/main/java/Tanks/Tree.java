package Tanks;

import processing.core.PImage;

import java.util.Random;
import java.util.ArrayList;


/**
 * Represents a tree character in the game.
 */
public class Tree extends Chara {

    private PImage treeImage;
    private ArrayList<TerrainPixelLine> terrainPixelLines;
    private TerrainPixelLine treePixelLine;
    public static Random random = new Random();

    /**
     * Constructs a Tree object with the specified coordinates, character, and tree image.
     *
     * @param xCor      The x-coordinate of the tree.
     * @param yCor      The y-coordinate of the tree.
     * @param chara     The character associated with the tree.
     * @param treeImage The image representing the tree.
     */
    public Tree(int xCor, int yCor, Character chara, PImage treeImage) {
        super(xCor, yCor, chara);
        this.treeImage = treeImage;
        this.terrainPixelLines = new ArrayList<TerrainPixelLine>();
    }

    /**
     * Adds a terrain pixel line to the tree's range.
     *
     * @param tpl The terrain pixel line to add.
     */
    public void addTreeRange(TerrainPixelLine tpl) {
        this.terrainPixelLines.add(tpl);
    }

    /**
     * Sets a random position for the tree based on the available terrain pixel lines.
     */
    public void setRandomPosition() {
        if (this.terrainPixelLines.size() != 0) {
            int randomBound = this.terrainPixelLines.size();
            int randomIndex = random.nextInt(randomBound);
            this.treePixelLine = this.terrainPixelLines.get(randomIndex);
            this.x = treePixelLine.getX() - 16;
            this.y = treePixelLine.getY() - 29;
            this.treePixelLine.setTree(this);
        }
    }

    /**
     * Gets the image representing the tree.
     *
     * @return The tree image.
     */
    public PImage getTreeImage() {
        return this.treeImage;
    }

}
