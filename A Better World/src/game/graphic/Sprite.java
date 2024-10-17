package game.graphic;

import game.physic.Vector2D;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Sprite {

    private BufferedImage SPRITE_SHEET = null;
    private BufferedImage[][] spriteArray;
    private final int BASE_TILE_SIZE = 16;
    public int width;
    public int height;
    private int wSprite;
    private int hSprite;

    private String source;

    // Constructor for default TILE_SIZE
    public Sprite(String file) {
        source = file;
        this.width = BASE_TILE_SIZE;
        this.height = BASE_TILE_SIZE;

        System.out.println("Loading: " + file + "...");
        SPRITE_SHEET = loadSprite(file);

        if (SPRITE_SHEET != null) {
            this.wSprite = SPRITE_SHEET.getWidth() / width;
            this.hSprite = SPRITE_SHEET.getHeight() / height;
            System.out.println(this.hSprite + " " + this.wSprite + "Size");
            loadSpriteArray(); // Only load if sprite sheet is valid
        } else {
            System.err.println("Error: Sprite sheet could not be loaded.");
        }
    }

    // Constructor for custom sprite sizes
    public Sprite(String file, int wSprite, int hSprite) {
        source = file;
        this.width = wSprite;
        this.height = hSprite;

        System.out.println("Loading: " + file + "...");
        SPRITE_SHEET = loadSprite(file);

        if (SPRITE_SHEET != null) {
            this.wSprite = SPRITE_SHEET.getWidth() / width;
            this.hSprite = SPRITE_SHEET.getHeight() / height;
            System.out.println(this.hSprite + " " + this.wSprite + "Size");
            loadSpriteArray(); // Only load if sprite sheet is valid
        } else {
            System.err.println("Error: Sprite sheet could not be loaded.");
        }

    }

    private BufferedImage loadSprite(String file) {
        BufferedImage sprite = null;
        try {
            sprite = ImageIO.read(getClass().getClassLoader().getResourceAsStream(file));
        } catch (Exception e) {
            System.err.println("Error: Could not load sprite.");
        }
        return sprite;
    }

    public void loadSpriteArray() {
        spriteArray = new BufferedImage[hSprite][wSprite];

        for (int y = 0; y < hSprite; y++) {
            for (int x = 0; x < wSprite; x++) {
                spriteArray[y][x] = getSprite(x, y);
            }
        }
        System.out.println("Sprite array loaded successfully by " + source);
    }

    public BufferedImage getSpriteSheet() {
        return SPRITE_SHEET;
    }

    public BufferedImage getSprite(int x, int y) {
        return SPRITE_SHEET.getSubimage(x * width, y * height, width, height);
    }

    public BufferedImage[] getSpriteArray(int i) {
        return spriteArray[i];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        if (SPRITE_SHEET != null) {
            wSprite = SPRITE_SHEET.getWidth() / width;
            hSprite = SPRITE_SHEET.getHeight() / height;
            loadSpriteArray();
        } else {
            System.err.println("Error: Sprite sheet is not loaded.");
        }
    }

    // Draw a string of characters using a font and offset positions
    public static void drawArray(Graphics2D g, Font f, String word, Vector2D pos, int width, int height, int xOffset, int yOffset) {
        final char SPACE_CHAR = 32;
        float x = pos.x;
        float y = pos.y;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (c != SPACE_CHAR) {
                g.drawImage(f.getFont(c), (int) x, (int) y, width, height, null);
            }
            x += xOffset;
            y += yOffset;
        }
    }
}
