import java.util.*;
import java.util.List;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel {
    public static int tile = 40;
    public static int column = 640;
    public static int row = 640;
    private Snake snake;
    private int appleCount = 1;                
    private List<Food> foods = new ArrayList<>();
    private javax.swing.Timer timer;
    private boolean started = false;
    private int score = 0;
    private int highscore = 0;
    private int speed = 120; // default speed (ms per tick)
    private boolean dead = false;
    private boolean classicMode = false; 
    private boolean skibidi = false; // fun mode
    private boolean eaten = false; // to track if food was eaten this tick, can be used for animations or other effects in the future

    // Handles the game logic, drawing, and user input. Essentially runs the game from this class.
    public GamePanel() {
        // initialize snake and foods
        this.snake = new Snake();
        for (int i = 0; i < appleCount; i++) {
            Food f = new Food();
            while (snake.getBody().contains(new Point(f.getCol(), f.getRow()))
                    || foods.stream().anyMatch(of -> of.getCol() == f.getCol() && of.getRow() == f.getRow())) {
                f.generateNewLocation();
            }
            foods.add(f);
        }
        ensureFoods();
        setPreferredSize(new Dimension(640, 640));
        setBackground(Color.GREEN);
        setFocusable(true);
        requestFocusInWindow();

        // initializes timer (used to determine frame rate/speed)
        int delayMs = speed; 
        timer = new javax.swing.Timer(delayMs, ev -> {
            if (skibidi == false) {
                updateGame();
            }
            else {
                int rand = new Random().nextInt(5);
                for (int i = 0; i < rand; i++) { 
                    updateGame();
                    if (eaten == true) {
                        eaten = false;
                        break;
                    }
                }
            }
            repaint();
        });

        // create popup and show on Enter/Return
        JPopupMenu popup = createPopupMenu();
        InputMap im = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "showPopup");
        am.put("showPopup", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                popup.show(GamePanel.this, getWidth() / 2, getHeight() / 2);
            }
        });

        // scans for directional inputs and updates the snake's pending direction accordingly 
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_W:
                        snake.setPendingDirection(Direction.UP);
                        runIfNeeded();
                        break;
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_S:
                        snake.setPendingDirection(Direction.DOWN);
                        runIfNeeded();
                        break;
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                        snake.setPendingDirection(Direction.LEFT);
                        runIfNeeded();
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        snake.setPendingDirection(Direction.RIGHT);
                        runIfNeeded();
                        break;
                }
            }
        });
    }

    // ensures the correct number of foods are present based on appleCount, and that they are not overlapping the snake or each other
    private void ensureFoods() {
        if (foods == null) foods = new ArrayList<>();
        // add missing foods
        while (foods.size() < Math.max(1, appleCount)) {
            Food f = new Food();
            while (snake.getBody().contains(new Point(f.getCol(), f.getRow()))
                    || foods.stream().anyMatch(of -> of.getCol() == f.getCol() && of.getRow() == f.getRow())) {
                f.generateNewLocation();
            }
            foods.add(f);
        }
        // trim excess foods
        while (foods.size() > Math.max(1, appleCount)) {
            foods.remove(foods.size() - 1);
        }
    }
 
    // updates appleCount and calls ensureFoods
    public void setAppleCount(int n) {
        appleCount = Math.max(1, n);
        ensureFoods();
        repaint();
    }

    // creates the popup menu for adjusting settings and resetting the game (appears on Enter/Return and on death)
    private JPopupMenu createPopupMenu() {
        JPopupMenu menu = new JPopupMenu();

        // initialize speed submenu with radio buttons for different speeds
        JMenu speedMenu = new JMenu("Speed");
        ButtonGroup speedGroup = new ButtonGroup();
        JRadioButtonMenuItem verySlow = new JRadioButtonMenuItem("Very Slow");
        JRadioButtonMenuItem slow = new JRadioButtonMenuItem("Slow");
        JRadioButtonMenuItem normal = new JRadioButtonMenuItem("Normal");
        JRadioButtonMenuItem fast = new JRadioButtonMenuItem("Fast");
        JRadioButtonMenuItem veryFast = new JRadioButtonMenuItem("Very Fast");
        JRadioButtonMenuItem classic = new JRadioButtonMenuItem("Classic");
        JRadioButtonMenuItem huh = new JRadioButtonMenuItem("???");
        speedGroup.add(verySlow); speedGroup.add(slow); speedGroup.add(normal); speedGroup.add(fast); speedGroup.add(veryFast); speedGroup.add(huh);

        // set selected based on current speed
        verySlow.setSelected(speed == 200);
        slow.setSelected(speed == 150);
        normal.setSelected(speed == 120);
        fast.setSelected(speed == 90);
        veryFast.setSelected(speed == 60);
        classic.setSelected(classicMode);
        huh.setSelected(speed == 500);

        verySlow.addActionListener(e -> { 
            skibidi = false;
            classicMode = false;
            speed = 200;
            if (timer != null) timer.setDelay(speed);
        });
        slow.addActionListener(e -> { 
            skibidi = false;
            classicMode = false;
            speed = 150;
            if (timer != null) timer.setDelay(speed);
        });
        normal.addActionListener(e -> {
            skibidi = false;
            classicMode = false;
            speed = 120;
            if (timer != null) timer.setDelay(speed);
        });
        fast.addActionListener(e -> {
            skibidi = false;
            classicMode = false;
            speed = 90;
            if (timer != null) timer.setDelay(speed);
        });

        veryFast.addActionListener(e -> {
            skibidi = false;
            classicMode = false;
            speed = 60;
            if (timer != null) timer.setDelay(speed);
        });

        classic.addActionListener(e -> {
            classicMode = true;
            skibidi = false;
            speed = 150;
            if (timer != null) timer.setDelay(speed);
        });

        huh.addActionListener(e -> {
            speed = 500;
            skibidi = true;
            classicMode = false;
            timer.setDelay(speed);
        });

        speedMenu.add(verySlow); speedMenu.add(slow); speedMenu.add(normal); speedMenu.add(fast); speedMenu.add(veryFast); speedMenu.add(classic); speedMenu.add(huh);
        menu.add(speedMenu);

        // initialize apple count submenu with radio buttons for different counts
        JMenu applesMenu = new JMenu("Apples");
        ButtonGroup applesGroup = new ButtonGroup();
        for (int i = 1; i <= 6; i++) { // choose range you want
            final int count = i;
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(Integer.toString(i));
            item.setSelected(appleCount == i);
            item.addActionListener(e -> {
                setAppleCount(count);
            });
            applesGroup.add(item);
            applesMenu.add(item);
        }
        menu.add(applesMenu);

        // option to reset high score
        JMenuItem resetHigh = new JMenuItem("Reset High Score");
        resetHigh.addActionListener(e -> {
            highscore = 0;
        });
        menu.add(resetHigh);

        // option to reset game (uses new settings)
        JMenuItem reset = new JMenuItem("Restart");
        reset.addActionListener(e -> {
            restart();
        });
        menu.add(reset);

        return menu;
    }

    // method to start timer, makes sure the game can start only when a directional key is pressed for the first time
    public void runIfNeeded () {
        if (!started) {
            timer.start();
            started = true;
        }
    }
    // draws the grid
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < column / tile; i++) {
            for (int j = 0; j < row / tile; j++) {
                g.setColor(Color.GRAY);
                g.drawRect(i * tile, j * tile, tile, tile);
            }
        }
        snake.draw(g);
        for (Food f : foods) {
            f.draw(g);
        }
        // draw score / UI
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("Score: " + score, 8, 18);
        g.drawString("High Score: " + highscore, 8, 36);
        if (!started) {
            String msg = "Press an arrow key to start";
            FontMetrics fm = g.getFontMetrics();
            int mx = ((getWidth() - fm.stringWidth(msg)) / 2);
            g.drawString(msg, mx, (getHeight() / 2) - 50);
        }
        if (dead == true) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            String msg = "Game Over! Your score was: " + score;
            FontMetrics fm = g.getFontMetrics();
            int mx = ((getWidth() - fm.stringWidth(msg)) / 2);
            g.drawString(msg, mx, (getHeight() / 2) - 50);

            String msg3 = "Current High Score: " + highscore;
            FontMetrics fm3 = g.getFontMetrics();
            int mx3 = ((getWidth() - fm3.stringWidth(msg3)) / 2);
            g.drawString(msg3, mx3, (getHeight() / 2) - 30);

            String msg2 = "Press Enter to change settings or reset";
            FontMetrics fm2 = g.getFontMetrics();
            int mx2 = ((getWidth() - fm2.stringWidth(msg)) / 2 - 40);
            g.drawString(msg2, mx2, (getHeight() / 2) - 10);
        }
    }

    public void run() {
        // optional: keep for compatibility, just to not break existing code that calls run()
        runIfNeeded();
    }

    // restarts the game
    public void restart () {
        if (timer != null) timer.stop();
        snake = new Snake();
        for (Food f : foods) {
            do {
                f.generateNewLocation();
            } while (snake.getBody().contains(new Point(f.getCol(), f.getRow()))
                    || foods.stream().filter(ff -> ff != f)
                            .anyMatch(ff -> ff.getCol() == f.getCol() && ff.getRow() == f.getRow()));
        }
        if (classicMode) {
            speed = 150;
        }  
        score = 0;
        started = false;
        dead = false;
        if (timer != null) timer.setDelay(speed);
        repaint();
    }

    // updates the game every frame/tick by moving snake, checking for food consumption and collisions, and repainting the panel appropriately
    public void updateGame() {
        snake.move();

        Point head = snake.getBody().get(0); // head is in grid units (col,row)
        for (Food f : foods) {
            if (head.x == f.getCol() && head.y == f.getRow()) {
                snake.grow();
                score++;
                eaten = true;
                if (classicMode) {
                    speed -= 3;
                    timer.setDelay(speed);
                }
                if (score > highscore) {
                    highscore = score;
                }
                // make sure food is spawning on valid tile
                do {
                    f.generateNewLocation();
                } while (snake.getBody().contains(new Point(f.getCol(), f.getRow()))
                        || foods.stream().filter(ff -> ff != f)
                                .anyMatch(ff -> ff.getCol() == f.getCol() && ff.getRow() == f.getRow()));
                break; // only one food can be eaten this tick
            }
        }

        // Check for collisions after each move/frame
        if (snake.checkCollision()) {
            // stop the game loop timer
            timer.stop();
            dead = true;

            // show the same popup menu used elsewhere as the "death" menu
            JPopupMenu deathMenu = createPopupMenu();
            JMenuItem quit = new JMenuItem("Quit");
            quit.addActionListener(e -> System.exit(0));
            deathMenu.addSeparator();
            deathMenu.add(quit);

            // show at center of panel
            deathMenu.show(this, getWidth() / 2, getHeight() / 2);

            // don't proceed further in this tick
            repaint();
            return;
        }

        repaint();
    }
}
