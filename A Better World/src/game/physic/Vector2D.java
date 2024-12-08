package game.physic;

public class Vector2D {
    public float x;
    public float y;

    public static float worldX;
    public static float worldY;

    public Vector2D() {
        x = 0;
        y = 0;
    }

    public Vector2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void addX(float f) {
        x += f;
    }
    public void addY(float f) {
        y += f;
    }

    public void setX(float f) {
        x = f;
    }
    public void setY(float f) {
        y = f;
    }

    public void setVector(Vector2D v) {
        x = v.x;
        y = v.y;
    }

    public void setVector(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public static void setWorldVar(float x, float y) {
        worldX = x;
        worldY = y;
    }

    public Vector2D getWorldVar() {
        return new Vector2D(x - worldX, y - worldY);
    }

    public void setVector(float x, float y, float worldX, float worldY) {
        this.x = x;
        this.y = y;
        Vector2D.worldX = worldX;
        Vector2D.worldY = worldY;
    }

    // Add another vector to this vector
    public Vector2D add(Vector2D v){
        return new Vector2D(this.x + v.x, this.y + v.y);
    }

    // Subtract another vector to this vector
    public Vector2D subtract(Vector2D v){
        return new Vector2D(this.x - v.x, this.y - v.y);
    }

    // Multiply this vector by scalar
    public Vector2D multiply(float scalar){
        return new Vector2D(this.x * scalar,this.y * scalar);
    }

    // Calculate the magnitude (length) of the vector
    public float magnitude(){
        return (float)Math.sqrt(x*x + y*y);
    }

    // Normalize the vector to have a magnitude of 1 (unit vector)
    public Vector2D normalize(){
        float magnitude = magnitude();
        if(magnitude != 0){
            return new Vector2D(this.x / magnitude, this.y / magnitude);
        }
        return new Vector2D(0,0);
    }

    // Calculate the distance between two vector (positions)
    public float distance(Vector2D v){
        float dx = this.x - v.x;
        float dy = this.y - v.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public String toString() {
        return "Vector2D{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public Vector2D abs() {
        return new Vector2D(Math.abs(x), Math.abs(y));
    }

    // dot product
    public float dot(Vector2D v) {
        return x * v.x + y * v.y;
    }
}
