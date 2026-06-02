import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

public class Food {
    private int col;
    private int row;
    private int x;
    private int y;
    private static final int SIZE = 23; 
    private Random random;

    // initializes the food
    public Food() {
        random = new Random();
        generateNewLocation();
    }

    // generates a new random location for the food, making sure it is not out of bounds and is centered in the tile
    public void generateNewLocation() {
        int cols = Math.max(1, GamePanel.column / GamePanel.tile);
        int rows = Math.max(1, GamePanel.row / GamePanel.tile);
        col = random.nextInt(cols);
        row = random.nextInt(rows);
        int tile = GamePanel.tile;
        x = col * tile + (tile / 2) - (SIZE / 2);
        y = row * tile + (tile / 2) - (SIZE / 2);
    }

    // draws the food when called
    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, SIZE, SIZE);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getCol() { 
        return col; 

    }
    public int getRow() { 
        return row; 
    }
}