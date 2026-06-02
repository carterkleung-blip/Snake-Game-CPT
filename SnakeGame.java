/*
    Com-Sci CPT 2026: Snake Game
    Group members: Carter, Kensen, Edwin
    ICS4U1
    June 1, 2026
    This is the main class for the Snake Game. It creates the game window and initializes the game panel where the game logic is implemented.
    This version of the Snake Game includes a menu for adjusting the speed of the snake as well as adjusting the number of apples that spawn.
*/



import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;

public class SnakeGame extends JPanel {
    public static void main(String[] args) {
        // Create the game window
        JFrame frame = new JFrame("Snake Game CPT - Carter, Kensen, Edwin");

        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GamePanel panel = new GamePanel();
        frame.add(panel);
        frame.pack();
        // Starts at center of the screen
        frame.setVisible(true);
    }   
}   