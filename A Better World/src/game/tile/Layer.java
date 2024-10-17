package game.tile;

public class Layer {
    protected String name;
    protected int index;
    protected int width;
    protected int height;
    protected int[] data;

    public Layer(String name, int width, int height, int index) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.index = index;
        this.data = new int[width * height];
    }

    public void parseString(String data) {
        String[] parts = data.split(",");
        for (int i = 0; i < parts.length; i++) {
            this.data[i] = Integer.parseInt(parts[i].replaceAll("\\s+", ""));
        }
    }

    public void setData(int[] data) {
        this.data = data;
    }

    public int getPos(int x, int y) {
        return data[(x * width) + y];
    }

    public int getIndex() {
        return index;
    }
}
