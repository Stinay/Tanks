package Tanks;

/**
 * The Chara class represents a character in the game.
 */
public class Chara {
    protected int xCor;
    protected int yCor;
    protected Character chara;
    protected float x;
    protected float y;

    /**
     * Constructs a Chara object with the specified coordinates and character.
     *
     * @param xCor  the x-coordinate of the character
     * @param yCor  the y-coordinate of the character
     * @param chara the character representation
     */
    public Chara(int xCor, int yCor, Character chara) {
        this.xCor = xCor;
        this.yCor = yCor;
        this.chara = chara;
        this.x = xCor * 32;
        this.y = (float) yCor * 32;
    }

    /**
     * Returns the character representation.
     *
     * @return the character representation
     */
    public Character getCharacter() {
        return this.chara;
    }

    /**
     * Returns the x-coordinate of the character.
     *
     * @return the x-coordinate of the character
     */
    public float getX() {
        return this.x;
    }

    /**
     * Returns the y-coordinate of the character.
     *
     * @return the y-coordinate of the character
     */
    public float getY() {
        return this.y;
    }

    /**
     * Sets the x-coordinate of the character.
     *
     * @param x the new x-coordinate of the character
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Sets the y-coordinate of the character.
     *
     * @param y the new y-coordinate of the character
     */
    public void setY(float y) {
        this.y = y;
    }
}
