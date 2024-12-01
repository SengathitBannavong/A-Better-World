package game.tile;

import game.Debug;
import game.GamePanel;
import game.graphic.Camera;
import game.object.GridCell;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Map {
    protected List <TileSet> tileSet;
    protected List <Layer> layer;
    private int count_tileSet = 0;
    private int count_layer = 0;
    private static int width;
    private static int height;
    private static int scale;

    private float lastStartX = -1.0f;
    private float lastStartY = -1.0f;
    private BufferedImage cachedMapImage = null;

    public Map() {
        tileSet = new ArrayList<>();
        layer = new ArrayList<>();
        scale = 1;
    }

    public void addTileSet(TileSet tile) {
         tileSet.add(tile);
         count_tileSet++;
    }

    public void addLayer(Layer lay) {
        layer.add(lay);
        count_layer++;
    }

    public void setWidth(int width) {
        Map.width = width;
    }

    public void setHeight(int height) {
        Map.height = height;
    }

    public void setSize(int width, int height) {
        Map.width = width;
        Map.height = height;
    }

    public void setScale(int scale) {
        Map.scale = scale;
    }

    public static int getScale() {
        return scale;
    }

    public void drawMap(Graphics2D g, Camera cam) {
        int tileSize = GamePanel.Tile_Size * GamePanel.Zoom * scale;
        float worldX = Camera.getWorldX();
        float worldY = Camera.getWorldY();

        float startX = Math.max(0, worldX / tileSize);
        float startY = Math.max(0, worldY / tileSize);
        float endX = Math.min(width, startX + ((float) cam.getViewportWidth() / tileSize) + 2);
        float endY = Math.min(height, startY + ((float) cam.getViewportHeight() / tileSize) + 2);

        // Check if we can reuse the cached map image
        if (cachedMapImage != null && Math.abs(startX - lastStartX) < 0.01 && Math.abs(startY - lastStartY) < 0.01) {
            g.drawImage(cachedMapImage, 0, 0, null);
            return;
        }

        // Update start positions and clear previous cached image
        lastStartX = startX;
        lastStartY = startY;
        cachedMapImage = new BufferedImage(cam.getViewportWidth(), cam.getViewportHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D gCached = cachedMapImage.createGraphics();

        // Draw each layer within the viewport
        int index = 0;
        int tileX = 0;
        int tileY = 0;
        for (Layer lay : layer) {
            for (int y = (int) startY; y < endY; y++) {
                for (int x = (int) startX; x < endX; x++) {
                    index = lay.getPos(y, x);
                    if (index <= 0) continue;

                    BufferedImage img = getImage(index);
                    if (img == null) {continue;}

                     tileX = (int) ((x * tileSize) - worldX);
                     tileY = (int) ((y * tileSize) - worldY);
                    gCached.drawImage(img, tileX, tileY, tileSize, tileSize, null);

                    if (Debug.debugging) {
                        gCached.setColor(Color.GREEN);
                        gCached.drawRect(tileX, tileY, tileSize, tileSize);
                    }
                }
            }
        }

        gCached.dispose(); // Clean up graphics context
        g.drawImage(cachedMapImage, 0, 0, null); // Draw the cached image
    }

    public BufferedImage getImage(int index) {
        for (int i = count_tileSet - 1; i >= 0; i--) {
            TileSet tiles = tileSet.get(i);
            if (index >= tiles.getFirstGid() && index <= tiles.getLastGid()) {
                return tiles.getImage(index);
            }
        }
        return null;
    }

    static public int getWidth() {
        return width;
    }

    static public int getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return "Map{" +
                "count_tileSet=" + count_tileSet +
                ", count_layer=" + count_layer +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
