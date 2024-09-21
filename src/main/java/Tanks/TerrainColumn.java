package Tanks;


import java.util.ArrayList;


/**
 * Represents a terrain column in the game.
 */
public class TerrainColumn extends Chara {

    private int[] terrainRGB;
    private ArrayList<TerrainPixelLine> lines;

    /**
     * Constructs a TerrainColumn object.
     *
     * @param xCor  The x-coordinate of the terrain column.
     * @param yCor  The y-coordinate of the terrain column.
     * @param chara The character associated with the terrain column.
     * @param RGB   The RGB values for the terrain column.
     */
    public TerrainColumn(int xCor, int yCor, Character chara, int[] RGB) {
        super(xCor, yCor, chara);
        float pcolHeight = 640 - y;
        this.terrainRGB = RGB;

        this.lines = new ArrayList<TerrainPixelLine>();

        for (int i = 0; i < 32; i++) {
            lines.add(new TerrainPixelLine(xCor * 32 + i, pcolHeight, terrainRGB));
        }
    }


    /**
     * Gets the terrain pixel lines associated with the terrain column.
     *
     * @return The terrain pixel lines.
     */
    public ArrayList<TerrainPixelLine> getTerrainPixelLines() {
        return this.lines;
    }


}
