package game.tile;

import game.Debug;
import game.GamePanel;
import game.graphic.Camera;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Map {
   protected List <TileSet> tileSet;
   protected List <Layer> layer;
   private int count_tileSet = 0;
   private int count_layer = 0;
   private int width;
   private int height;

   public Map() {
       tileSet = new ArrayList<>();
       layer = new ArrayList<>();
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
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void drawMap(Graphics2D g, Camera cam){
        int tileSize = GamePanel.Tile_Size;
        int startX = Math.max(0, (int)(Camera.getWorldX() / tileSize));
        int startY = Math.max(0, (int)(Camera.getWorldY() / tileSize));
        int endX = Math.min(width, startX + (cam.getViewportWidth() / tileSize) +2);
        int endY = Math.min(height, startY + (cam.getViewportHeight() / tileSize) +2);

        for(Layer lay : layer){
            for (int y = startY; y < endY; y++) {
                for (int x = startX; x < endX; x++) {
                    int index = lay.getPos(y, x);
                    if(index > 0){
                        BufferedImage img = getImage(index);
                        if(img != null){
                            int tileX = (x * tileSize) - (int) Camera.getWorldX();
                            int tileY = (y * tileSize) - (int) Camera.getWorldY();
                            g.drawImage(img, tileX, tileY, tileSize, tileSize, null);
                            if(Debug.debugging){
                                g.setColor(Color.GREEN);
                                g.drawRect(tileX, tileY, tileSize, tileSize);
                            }
                        }
                    }
                }
            }
        }
    }

    public BufferedImage getImage(int index) {
       for(int i = count_tileSet -1; i >= 0; i--) {
           if(index >= tileSet.get(i).getFirstGid() && index <= tileSet.get(i).getLastGid()) {
               return tileSet.get(i).getImage(index);
           }
       }
       return null;
    }

    @Override
    public String toString() {
        return "Map{" +
                "count_tileset=" + count_tileSet +
                ", count_layer=" + count_layer +
                '}';
    }
}
