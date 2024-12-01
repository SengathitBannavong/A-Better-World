package game.physic;

import game.Debug;
import game.entity.Entity;

import java.awt.*;

public class AABB {
    private Vector2D position; // Center position of the AABB
    private float width; // Width of the AABB
    private float height; // Height of the AABB
    private float radius; // Radius of the AABB
    private int size;// Size of the AABB

    private float xOffset = 0; // Offset of the AABB on the X axis
    private float yOffset = 0; // Offset of the AABB on the Y axis

    private Entity entity;

    public AABB(Vector2D position,int width,int height){
        this.position = position;
        this.width = width;
        this.height = height;

        this.size = Math.max(width, height);
    }

    public AABB(Vector2D position,int radius){
        this.position = position;
        this.radius = radius;

        this.size = radius;
    }

    public void setBox(Vector2D position,int width,int height){
        this.position = position.add(new Vector2D(width , height));
        this.width = width;
        this.height = height;

        this.size = Math.max(width, height);
    }

    public void setCircle(Vector2D position,int radius){
        this.position = position;
        this.radius = radius;

        this.size = radius;
    }

    public boolean collides(AABB bBox){
        float ax = ((position.getWorldVar().x + (width/2)) + xOffset); // Get the center X of the AABB
        float ay = ((position.getWorldVar().y + (height/2)) + yOffset); // Get the center Y of the AABB
        float bx = ((bBox.position.getWorldVar().x + (bBox.width/2)) + bBox.getxOffset());// Get the center X of the other AABB
        float by = ((bBox.position.getWorldVar().y + (bBox.height/2)) + bBox.getyOffset()); // Get the center Y of the other AABB

        ax = ax + (width / 2);
        ay = ay + (height / 2);
        bx = bx + (bBox.width / 2);
        by = by + (bBox.height / 2);

        if(Math.abs(ax - bx) < (width / 2) + (bBox.width / 2)){ // Check if the distance between the two AABBs is less than the sum of their width
            return Math.abs(ay - by) < (height / 2) + (bBox.height / 2); // Check if the distance between the two AABBs is less than the sum of their height
        }
        return false;
    }

    public boolean colCircleBox(AABB aBox){
        float cx = (float) (position.getWorldVar().x + radius / Math.sqrt(2) - entity.getSize() / Math.sqrt(2)); // Get the center X of the circle
        float cy = (float) (position.getWorldVar().y + radius / Math.sqrt(2) - entity.getSize() / Math.sqrt(2)); // Get the center Y of the circle

        float xDelta = cx - Math.max(aBox.position.getWorldVar().x + (aBox.getWidth() / 2), Math.min(cx, aBox.position.getWorldVar().x)); // Get the distance between the center X of the circle and the center X of the AABB
        float yDelta = cy - Math.max(aBox.position.getWorldVar().y + (aBox.getHeight() / 2), Math.min(cy, aBox.position.getWorldVar().y)); // Get the distance between the center Y of the circle and the center Y of the AABB

        return (xDelta * xDelta + yDelta * yDelta) < ((this.radius / Math.sqrt(2)) * (this.radius / Math.sqrt(2))); // Check if the distance between the circle and the AABB is less than the sum of their radius
    }

    public Vector2D getPosition() {
        return position;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getRadius() {
        return radius;
    }

    public int getSize() {
        return size;
    }

    public float getxOffset() {
        return xOffset;
    }

    public float getyOffset() {
        return yOffset;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setxOffset(float xOffset) {
        this.xOffset = xOffset;
    }

    public void setyOffset(float yOffset) {
        this.yOffset = yOffset;
    }

    public void render(Graphics2D g){
        if(Debug.debugging) {
            g.setColor(Color.RED);
            int x = (int) (position.getWorldVar().x + (width / 2) + xOffset);
            int y = (int) (position.getWorldVar().y + (height / 2) + yOffset);
            g.drawRect(x, y,(int) width, (int) height);
        }
    }

}