package Game;

import javax.swing.*;

public class GameWindow extends JFrame {
    public GameWindow() {
        setTitle("Character Battle");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GamePanel gamePanel = new GamePanel();
        add(gamePanel);

        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    public static void main(String[] args) {
        new GameWindow();
    }
}
