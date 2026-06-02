import java.util.List;
import java.util.ArrayList;
import java.io.*;
import javax.swing.*;
import java.awt.*;

public class Snake {
    private List<Point> body;
    private Direction direction;
    private Direction pendingDirection; // To handle input between moves; makes sure only the first input per tick is accepted

    // Initializes the snake with a starting position and direction
    public Snake() {
        body = new ArrayList<>();
        body.add(new Point(7, 7)); 
        body.add(new Point(6, 7));
        direction = Direction.RIGHT; 
    }

    // Moves the snake in its current direction
    public void move() {
        if (pendingDirection != null) {
            setDirection(pendingDirection);
            pendingDirection = null;
        }

        Point head = body.get(0);
        Point newHead = new Point(head.x, head.y);
        switch (direction) {
            case UP:
                newHead.y--;
                break;
            case DOWN:
                newHead.y++;
                break;
            case LEFT:
                newHead.x--;
                break;
            case RIGHT:
                newHead.x++;
                break;
        }

        // Add new head to the front of the body
        body.add(0, newHead); 
        // Makes sure the length of the snake stays the same while moving instead of leaving a trail
        body.remove(body.size() - 1); 
    }

    // Grows the snake by adding a new segment to the end of the body (tail)
    public void grow() {
        Point tail = body.get(body.size() - 1);
        body.add(new Point(tail.x, tail.y)); 
    }

    public List<Point> getBody() {
        return body;
    }

    // Sets the snake's direction, ensuring it cannot reverse directly onto itself
    public void setDirection(Direction newDir) {
        if (newDir == null) return;
        // ignore direct reverse input
        if ((direction == Direction.UP && newDir == Direction.DOWN) ||
            (direction == Direction.DOWN && newDir == Direction.UP) ||
            (direction == Direction.LEFT && newDir == Direction.RIGHT) ||
            (direction == Direction.RIGHT && newDir == Direction.LEFT)) {
            return;
        }
        this.direction = newDir;
    }

    // called by key handler; accepts only the first input per tick
    public void setPendingDirection(Direction newDir) {
        if (newDir == null) return;
        // ignore direct reverse relative to current direction
        if ((direction == Direction.UP && newDir == Direction.DOWN) ||
            (direction == Direction.DOWN && newDir == Direction.UP) ||
            (direction == Direction.LEFT && newDir == Direction.RIGHT) ||
            (direction == Direction.RIGHT && newDir == Direction.LEFT)) {
            return;
        }
        if (pendingDirection == null) {
            pendingDirection = newDir;
        }
    }

    // Checks for collisions with walls or self; returns true if a collision is detected
    public boolean checkCollision() {
        if (body == null || body.isEmpty()) return false;
        Point head = body.get(0);

        // grid size in tiles
        int cols = GamePanel.column / GamePanel.tile;
        int rows = GamePanel.row / GamePanel.tile;

        // wall collision (compare tile coordinates, not pixels)
        if (head.x < 0 || head.x >= cols || head.y < 0 || head.y >= rows) {
            return true;
        }

        // self collision
        for (int i = 2; i < body.size(); i++) {
            if (head.equals(body.get(i))) return true;
        }

        return false;
    }

    // Draws the face which follows the snake's direction :)
    public void drawFace(Graphics g, int cx, int cy, int size) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int r = size / 2;
        // face circle (use head color)
        g2.setColor(Color.BLUE);
        g2.fillOval(cx - r, cy - r, size, size);

        // direction unit vector (fx,fy) and perp (px,py)
        int fx = 0, fy = 0;
        switch (direction) {
            case UP -> { fx = 0; fy = -1; }
            case DOWN -> { fx = 0; fy = 1; }
            case LEFT -> { fx = -1; fy = 0; }
            case RIGHT -> { fx = 1; fy = 0; }
        }
        double perpX = -fy;
        double perpY = fx;

        // eyes
        int eyeR = Math.max(2, size / 8);
        double eyeForward = size * 0.22;
        double eyeSide = size * 0.18;
        int ex1 = (int) Math.round(cx + fx * eyeForward + perpX * eyeSide);
        int ey1 = (int) Math.round(cy + fy * eyeForward + perpY * eyeSide);
        int ex2 = (int) Math.round(cx + fx * eyeForward - perpX * eyeSide);
        int ey2 = (int) Math.round(cy + fy * eyeForward - perpY * eyeSide);

        g2.setColor(Color.WHITE);
        g2.fillOval(ex1 - eyeR, ey1 - eyeR, eyeR * 2, eyeR * 2);
        g2.fillOval(ex2 - eyeR, ey2 - eyeR, eyeR * 2, eyeR * 2);
        g2.setColor(Color.BLACK);
        g2.fillOval(ex1 - eyeR/2, ey1 - eyeR/2, Math.max(1, eyeR), Math.max(1, eyeR));
        g2.fillOval(ex2 - eyeR/2, ey2 - eyeR/2, Math.max(1, eyeR), Math.max(1, eyeR));

        // fangs (two small forward-pointing triangles close together)
        double fangForward = size * 0.38;
        double fangLength = size * 0.26;
        double fangSideOffset = size * 0.08;
        double fangWidth = size * 0.06;

        // left fang base center
        double b1x = cx + fx * fangForward + perpX * fangSideOffset;
        double b1y = cy + fy * fangForward + perpY * fangSideOffset;
        // right fang base center
        double b2x = cx + fx * fangForward - perpX * fangSideOffset;
        double b2y = cy + fy * fangForward - perpY * fangSideOffset;

        int[] t1x = new int[] {
            (int)Math.round(b1x + fx * fangLength),
            (int)Math.round(b1x + perpX * fangWidth),
            (int)Math.round(b1x - perpX * fangWidth)
        };
        int[] t1y = new int[] {
            (int)Math.round(b1y + fy * fangLength),
            (int)Math.round(b1y + perpY * fangWidth),
            (int)Math.round(b1y - perpY * fangWidth)
        };

        int[] t2x = new int[] {
            (int)Math.round(b2x + fx * fangLength),
            (int)Math.round(b2x + perpX * fangWidth),
            (int)Math.round(b2x - perpX * fangWidth)
        };
        int[] t2y = new int[] {
            (int)Math.round(b2y + fy * fangLength),
            (int)Math.round(b2y + perpY * fangWidth),
            (int)Math.round(b2y - perpY * fangWidth)
        };

        g2.setColor(Color.WHITE);
        g2.fillPolygon(t1x, t1y, 3);
        g2.fillPolygon(t2x, t2y, 3);
        g2.setColor(Color.WHITE);
        g2.drawPolygon(t1x, t1y, 3);
        g2.drawPolygon(t2x, t2y, 3);
    }

    // draws the snake when called
    public void draw(Graphics g) {
        for (Point segment : body) {
            if (segment.equals(body.get(0))) {
                g.setColor(Color.BLUE);
                g.fillRect(segment.x * GamePanel.tile, segment.y * GamePanel.tile, GamePanel.tile, GamePanel.tile);
                drawFace(g, segment.x * GamePanel.tile + GamePanel.tile / 2, segment.y * GamePanel.tile + GamePanel.tile / 2, GamePanel.tile);
            }
            else{
                g.setColor(Color.BLUE);
                g.fillRect(segment.x * GamePanel.tile, segment.y * GamePanel.tile, GamePanel.tile, GamePanel.tile);
        
            }
        }
    }
}