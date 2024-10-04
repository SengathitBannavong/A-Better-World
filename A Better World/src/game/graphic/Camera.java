package game.graphic;

import game.physic.Vector2D;

public class Camera {
    private Vector2D playerPosition;
    private final float offsetX;
    private final float offsetY;
    private final int viewportWidth;   // Width of the visible area (in pixels)
    private final int viewportHeight;  // Height of the visible area (in pixels)

    public Camera(Vector2D playerPosition, float offsetX, float offsetY, int viewportWidth, int viewportHeight) {
        this.playerPosition = playerPosition;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
    }

    public void update() {
        float camX = playerPosition.x - offsetX;
        float camY = playerPosition.y - offsetY;
        Vector2D.setWorldVar(camX, camY);
    }

    public void setPlayerPosition(Vector2D playerPosition) {
        this.playerPosition = playerPosition;
    }

    public static float getWorldX() {
        return Vector2D.worldX;
    }

    public static float getWorldY() {
        return Vector2D.worldY;
    }

    public int getViewportWidth() {
        return viewportWidth;
    }

    public int getViewportHeight() {
        return viewportHeight;
    }
}
