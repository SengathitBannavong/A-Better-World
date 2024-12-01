package game.tile;

import game.object.GridCell;

import java.io.*;

public class GridCellWrite {
    private final GridCell gridCell;
    private static String path;

    public GridCellWrite(String path){
        GridCellWrite.path = path;
        gridCell = new GridCell();
    }

    public void setGrid(int x, int y){
        gridCell.setGrid(x, y, (byte) 1);
    }

    public static void writeGrid(){
        System.out.println("Writing grid to file");
        byte[][] grid = GridCell.getGrid();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("res/maps/"+ path +".txt"))) {
            writer.write(path);
            writer.newLine();
            int width = GridCell.getWidth();
            int height = GridCell.getHeight();
            for(int i = 0; i < width * Map.getScale(); i++){
                for(int j = 0; j < height * Map.getScale(); j++){
                    System.out.println("with " + i + " and " + j +"and" + width*Map.getScale() + "and" + height*Map.getScale());
                    writer.write(grid[i][j] + " ");
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Grid written to file");
    }

    public static void parseGrid(String path) {
        GridCellWrite.path = path;
        byte[][] grid = new byte [(Map.getWidth() * Map.getScale())][(Map.getHeight() * Map.getScale())+1];
        try (BufferedReader reader = new BufferedReader(new FileReader("res/"+ path))) {
            String line = reader.readLine();
            int i = 0;
            if (line != null) {
                while ((line = reader.readLine()) != null) {
                   String [] parts = line.split(" ");
                   for(int j = 0; j < parts.length; j++){
                       grid[i][j] = Byte.parseByte(parts[j]);
                   }
                   i++;
                }
            }
            GridCell.setGridArray(grid, Map.getWidth(), Map.getHeight());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Grid parsed from file " + path +" Complete");
    }

    public static void setPath(String path){
        GridCellWrite.path = path;
    }

    public int getGrid(int x, int y) {
        return gridCell.getGrid(x, y);
    }

    public void deleteGrid(int posX, int posY) {
        gridCell.setGrid(posX, posY, (byte) 0);
    }
}
