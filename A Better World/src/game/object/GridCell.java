package game.object;

import game.tile.Map;

public class GridCell {
    private static byte [][] grid = null;
    private static int width = 0;
    private static int height = 0;

    public GridCell(){
        if(grid == null){
            System.out.println("GridCell setVariable() called from constructor");
            setVariable();
        }
    }

    public void setGrid(int x, int y, byte value){
        grid[x][y] = value;
    }

    public byte getGrid(int x, int y){
        return grid[x][y];
    }

    public static int getWidth(){
        return width;
    }

    public static int getHeight(){
        return height;
    }

    public static byte[][] getGrid(){
        return grid;
    }

    public static void setGridArray(byte[][] grid, int width, int height){
        GridCell.grid = grid;
        GridCell.width = width;
        GridCell.height = height;
    }

    public void clearGrid(){
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                grid[i][j] = 0;
            }
        }
    }

    public void clearGrid(int x, int y){
        grid[x][y] = 0;
    }

    public static void setVariable(){
        GridCell.width = Map.getWidth();
        GridCell.height = Map.getHeight();
        grid = new byte[width * Map.getScale()][height * Map.getScale()];
        init();
    }

    public static void init(){
        for(int i = 0; i < width * Map.getScale(); i++){
            for(int j = 0; j < height * Map.getScale(); j++){
                grid[i][j] = 0;
            }
        }
    }

    public boolean isBlock(int x, int y){
        return grid[x][y]==1;
    }



}
