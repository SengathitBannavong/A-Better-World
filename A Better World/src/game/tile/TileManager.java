package game.tile;

import game.GamePanel;
import game.entity.Player;
import game.graphic.Camera;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class TileManager {
    public Tile[] tiles;
    protected int[][] mapTileNum;
    protected int rowMap;
    protected int colMap;
    protected String source;

    public TileManager(String source) {
        this.source = source;
        tiles = new Tile[10];
        mapTileNum = new int[GamePanel.width][GamePanel.height];
        getTileImage();
        loadMap();
    }

    public int getMapTileNum(int i, int j) {
        return mapTileNum[i][j];
    }

    public void loadMap() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(source)) {
            if(is == null) {
                System.out.println("File not found");
                System.exit(1);
            }
            assert is != null;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line;
                int row = 0;
                while ((line = br.readLine()) != null) {
                    String[] split = line.split(" ");
                    colMap = split.length;
                    for (int i = 0; i < split.length; i++) {
                        Double num = Double.parseDouble(split[i]);
                        int number = num.intValue();
                        mapTileNum[row][i] = number;
                    }
                    row++;
                }
                rowMap = row;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getTileImage() {
        try {
            tiles[0] = new Tile();
            tiles[0].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/tiles/grass.png")));

            tiles[1] = new Tile();
            tiles[1].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/tiles/wall.png")));
            tiles[1].collision = true;

            tiles[2] = new Tile();
            tiles[2].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/tiles/water.png")));
            tiles[2].collision = true;

            tiles[3] = new Tile();
            tiles[3].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/tiles/earth.png")));

            tiles[4] = new Tile();
            tiles[4].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/tiles/tree.png")));
            tiles[4].collision = true;

            tiles[5] = new Tile();
            tiles[5].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/tiles/sand.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void render(Graphics2D g){
        draw(g, Player.getCamera());
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   public void draw(Graphics2D g2d, Camera cam) {
    int tileSize = 32;
    int startX = Math.max(0, (int)(Camera.getWorldX() / tileSize));
    int startY = Math.max(0, (int)(Camera.getWorldY() / tileSize));

    int endX = Math.min(colMap, startX + (cam.getViewportWidth() / tileSize) +2);
    int endY = Math.min(rowMap, startY + (cam.getViewportHeight() / tileSize) +2);
        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                // Calculate the render position of each tile
                int tileX = (x * tileSize) - (int) Camera.getWorldX();
                int tileY = (y * tileSize) - (int) Camera.getWorldY();
                int tileNum = mapTileNum[y][x];
                // Render each tile at the adjusted position
                g2d.drawImage(tiles[tileNum].image, tileX, tileY, tileSize, tileSize, null);
            }
        }
    }
}

