package game.graphic;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class Font {
    private BufferedImage FONTSHEET = null;
    private BufferedImage[][] spriteArray;
    private final int TILE_SIZE = 10;
    public int width;
    public int height;
    private int wFont;
    private int hFont;

    public Font(String file) {
        width = TILE_SIZE;
        height = TILE_SIZE;

        System.out.println("Loading: " + file + "...");
        FONTSHEET = loadFont(file);
        if (FONTSHEET != null) {
            this.wFont = FONTSHEET.getWidth() / width;
            this.hFont = FONTSHEET.getHeight() / height;
            System.out.println("Font loaded successfully with width: " + wFont + " and height: " + hFont);
            loadSpriteArray();
        }else{
            System.err.println("ERROR: could not load file: " + file);
        }
    }

    public Font(String file, int wFont, int hFont) {
        width = TILE_SIZE;
        height = TILE_SIZE;
        this.wFont = wFont;
        this.hFont = hFont;

        System.out.println("Loading: " + file + "...");
        FONTSHEET = loadFont(file);
        if (FONTSHEET != null) {
            this.wFont = FONTSHEET.getWidth() / width;
            this.hFont = FONTSHEET.getHeight() / height;
            System.out.println("Font loaded successfully with width: " + wFont + " and height: " + hFont);
            loadSpriteArray();
        }else{
            System.err.println("ERROR: could not load file: " + file);
        }
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        if (FONTSHEET != null) {
            this.wFont = FONTSHEET.getWidth() / width;
            this.hFont = FONTSHEET.getHeight() / height;
            loadSpriteArray();
        }else{
            System.err.println("ERROR: could not load file: " + FONTSHEET);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private BufferedImage loadFont(String file) {
        BufferedImage sprite = null;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(file)) {
            if (is == null) {
                throw new IOException("Resource not found: " + file);
            }
            sprite = ImageIO.read(is);
        } catch (IOException e) {
            System.err.println("ERROR: could not load file: " + file);
            e.printStackTrace();
        }
        return sprite;
    }

    private void loadSpriteArray() {
        spriteArray = new BufferedImage[hFont][wFont];
        for(int y = 0; y < hFont; y++) {
            for(int x = 0; x < wFont; x++) {
                spriteArray[y][x] = getLetter(x, y);
            }
        }
    }

    public BufferedImage getFONTSHEET() {
        return FONTSHEET;
    }

    private BufferedImage getLetter(int x, int y) {
        return FONTSHEET.getSubimage(x * width , y * height , width, height);
    }

    public BufferedImage getFont(char letter) {
        int value = letter;
        int x = value % wFont;
        int y = value / wFont;
        return getLetter(x, y);
    }

}
