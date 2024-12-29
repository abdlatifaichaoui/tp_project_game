package Game;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.awt.event.*;
import java.util.Iterator;
import javax.swing.border.AbstractBorder;

public class GamePanel extends JPanel {
    // *********************************** //
    // Fields
    // *********************************** //
    private Character selectedCharacter;
    private Character warrior = new Warrior("Kreatos", 120, 15, 50, "src/images/w.png");
    private Character mage = new Mage("Harry Potter", 100, 12, 60, "src/images/m.png");

    private Character Assassin = new Assassin("Zoro", 80, 20, 70, "src/images/a.png");
    private Character opponent = mage;
    // Counter to track the number of rounds
    private String playerMessage = "";
    private String opponentMessage = "";
    private boolean battleStarted = false;
    private boolean playerTurn = true; // Tracks the current turn
    // private String battleMessage = ""; // "Round 1" text
    private JButton attackButton, specialButton, recoverButton, defendButton, playButton;
    private ArrayList<FloatingText> floatingMessages = new ArrayList<>();
    private boolean showCharacterSelection = false;
    private Image backgroundImage;
    private boolean showTitle = true;
    private Character hoveredCharacter = null;

    private int experience = 0;
    private int level = 1;
    private int expToNextLevel = 100; // Example threshold for leveling up
    // *********************************** //
    // Constructor
    // *********************************** //

    public GamePanel() {
        setPreferredSize(new Dimension(800, 600));
        Image originalImage = new ImageIcon("src/images/maxresdefault.jpg").getImage();
        backgroundImage = originalImage.getScaledInstance(800, 600, Image.SCALE_SMOOTH);
        // Mouse listener for character selection
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!battleStarted) {
                    handleCharacterSelection(e);
                }
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                handleCharacterHover(e);
            }
        });

        // Initialize action buttons
        setLayout(null);
        playButton = new JButton("Play");
        playButton.setBounds(350, 250, 100, 50);
        playButton.setFont(new Font("Arial", Font.BOLD, 18));
        playButton.setBackground(new Color(144, 238, 144)); // Light green
        playButton.setFocusPainted(false);
        playButton.setBorder(new RoundedBorder(Color.BLACK, 3, 15)); // Rounded border
        playButton.addActionListener(e -> {
            showTitle = false; /// Hide title screen
            showCharacterSelection = true; /// Enable character selection
            playButton.setVisible(false);
            repaint();
        });
        add(playButton);

        attackButton = createStyledButton("Attack", 100, 525);

        specialButton = createStyledButton("Special", 250, 525);

        recoverButton = createStyledButton("Recover", 400, 525);
        defendButton = createStyledButton("Defend", 550, 525);
        add(recoverButton);
        add(defendButton);
        add(attackButton);
        add(specialButton);

        // Hide buttons initially
        setButtonsVisible(false);
    }

    public class FloatingText {
        private String text;
        private int x, y;
        private Color color;
        private int size;
        private int lifespan; // How long the message lasts (in milliseconds)
        private long startTime; // Time when the message was created

        public FloatingText(String text, int x, int y, Color green, int size, int lifespan) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.color = green;
            this.size = size;
            this.lifespan = lifespan;
            this.startTime = System.currentTimeMillis();
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - startTime > lifespan;
        }

        public void render(Graphics g) {
            g.setColor(color);
            g.setFont(new Font("Arial", Font.BOLD, size));
            g.drawString(text, x, y);
        }
    }

    private JButton createStyledButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 120, 40);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.BLACK);
        button.setBackground(new Color(230, 250, 230)); // Lavender background
        button.setFocusPainted(false);
        button.setBorder(new RoundedBorder(Color.BLACK, 5, 15)); // Thick black border with corner radius

        button.addActionListener(e -> {
            if (button.isEnabled()) {
                handleAction(e.getActionCommand());
            }
        });

        return button;
    }

    private void updateButtonStates() {
        if (selectedCharacter != null) {
            attackButton.setEnabled(selectedCharacter.getStamina() >= 10);
            specialButton.setEnabled(selectedCharacter.getStamina() >= 30); // Example stamina requirement
            recoverButton.setEnabled(selectedCharacter.getStamina() < selectedCharacter.getMaxStamina());
            defendButton.setEnabled(selectedCharacter.getStamina() >= 5); // Example stamina requirement
        }
    }

    private static class RoundedBorder extends AbstractBorder {
        private final Color borderColor;
        private final int thickness;
        private final int radius;

        public RoundedBorder(Color borderColor, int thickness, int radius) {
            this.borderColor = borderColor;
            this.thickness = thickness;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }

    /**
     * Toggles the visibility of the buttons.
     */
    private void setButtonsVisible(boolean visible) {
        attackButton.setVisible(visible);
        specialButton.setVisible(visible);

        recoverButton.setVisible(visible);
        defendButton.setVisible(visible);
    }

    /**
     * Handles character selection during the initial screen.
     */
    private void handleCharacterSelection(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        if (warrior.getBounds(100, 300).contains(mouseX, mouseY)) {
            selectedCharacter = new Warrior("Kreatos", 120, 15, 50, "src/images/w.png");
        } else if (mage.getBounds(300, 300).contains(mouseX, mouseY)) {
            selectedCharacter = new Mage("Harry Potter", 100, 12, 60, "src/images/m.png");
        } else if (Assassin.getBounds(500, 300).contains(mouseX, mouseY)) {
            selectedCharacter = new Assassin("Zoro", 80, 20, 70, "src/images/a.png");
        }
        if (selectedCharacter != null) {
            battleStarted = true;
            showCharacterSelection = false;
            // battleMessage = "Round 1!";
            setButtonsVisible(true);
            Timer timer = new Timer(2000, evt -> {
                // battleMessage = "";
                repaint();
            });
            timer.setRepeats(false);
            timer.start();
            repaint();
        }
    }

    private void handleCharacterHover(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        if (warrior.getBounds(100, 300).contains(mouseX, mouseY)) {
            hoveredCharacter = warrior;
        } else if (mage.getBounds(300, 300).contains(mouseX, mouseY)) {
            hoveredCharacter = mage;
        } else if (Assassin.getBounds(500, 300).contains(mouseX, mouseY)) {
            hoveredCharacter = Assassin;
        } else {
            hoveredCharacter = null;
        }
        repaint();
    }

    private void levelUp() {
        level++;
        experience -= expToNextLevel; // Reset experience
        expToNextLevel += 50; // Increment threshold for next level
        selectedCharacter.setMaxHealth(selectedCharacter.getMaxHealth() + 20); // Example power-up
        selectedCharacter.setDamage(selectedCharacter.getDamage() + 5); // Increase damage
        floatingMessages.add(new FloatingText("Level Up!", 400, 100, Color.ORANGE, 40, 2000));
        playerMessage = "Level Up! Health and Damage Increased!";
        repaint();
    }

    /**
     * Handles the button actions during battle.
     */
    private void handleAction(String action) {
        if (!playerTurn || !selectedCharacter.isAlive() || !opponent.isAlive()) {
            return;
        }

        switch (action) {
            case "Attack":
                if (selectedCharacter.getStamina() >= 10) {
                    int ddamage = selectedCharacter.getDamage();
                    if (opponent.inDefenseStance) {
                        ddamage = ddamage / 2;
                    }
                    selectedCharacter.attack(opponent);
                    selectedCharacter.reduceStamina(10); // Example stamina reduction
                    floatingMessages.add(new FloatingText("-" + ddamage + " HP", 500, 350, Color.RED, 20, 1000));
                    playerMessage = selectedCharacter.getName() + " attacks!";

                    experience += 10; // Example experience gain per action
                    if (experience >= expToNextLevel) {
                        levelUp();
                    }
                } else {
                    playerMessage = "Not enough stamina to attack!";
                }
                break;

            case "Special":
                int ddamage = selectedCharacter.getDamage();
                if (opponent.inDefenseStance) {
                    ddamage = ddamage / 2;
                }

                if (selectedCharacter.getStamina() >= 30) {
                    selectedCharacter.useSpecialAbility(opponent);

                    // Add floating text based on the character
                    if (selectedCharacter == mage) {
                        floatingMessages.add(new FloatingText("+" + 20 + " HP", 250, 350, Color.green, 20, 1000));
                        playerMessage = selectedCharacter.getName() + " uses a special ability!";
                    } else if (selectedCharacter == warrior || selectedCharacter == Assassin) {
                        floatingMessages
                                .add(new FloatingText("-" + (ddamage * 2) + " HP", 500, 350, Color.ORANGE, 20, 1000));
                        playerMessage = selectedCharacter.getName() + " uses a special ability!";
                    }
                    experience += 15; // Example experience gain per action
                    if (experience >= expToNextLevel) {
                        levelUp();
                    }
                } else {
                    // Optionally, you can display a message when stamina is not enough
                    playerMessage = selectedCharacter.getName() + " does not have enough stamina!";
                }
                break;
            case "Recover":
                selectedCharacter.recoverStamina();
                floatingMessages.add(new FloatingText("+10 Stamina", 100, 350, Color.BLUE, 20, 1000));
                playerMessage = selectedCharacter.getName() + " recovers stamina!";
                experience += 5; // Example experience gain per action
                if (experience >= expToNextLevel) {
                    levelUp();
                }
                break;

            case "Defend":

                if (!selectedCharacter.inDefenseStance) {
                    selectedCharacter.setDefenseStance(true);
                    defendButton.setText("Exit Defense"); // Change button text to "Exit Defense"
                    floatingMessages.add(new FloatingText("Defend!", 100, 300, Color.GREEN, 15, 1000));
                    playerMessage = selectedCharacter.getName() + " enters defense stance!";

                }
                break;
            case "Exit Defense":

                if (selectedCharacter.inDefenseStance == true) {
                    selectedCharacter.setDefenseStance(false);
                    defendButton.setText("Defend"); // Change button text back to "Defend"
                    floatingMessages.add(new FloatingText(" exit Defend!", 100, 300, Color.GREEN, 15, 1000));
                    playerMessage = selectedCharacter.getName() + " exits defense stance!";
                }
                break;
        }

        updateButtonStates(); // Refresh buttons based on new stamina
        opponentMessage = "";

        repaint();

        if (!opponent.isAlive()) {
            experience += 30; // Example experience gain per action
            if (experience >= expToNextLevel) {
                levelUp();
            }
            playerMessage = "You Win!";
            switchOpponent();
            return;
        }

        playerTurn = false;
        if (opponent.inDefenseStance) {
            opponent.defend();
        }
        Timer timer = new Timer(1350, e -> opponentTurn());
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Handles the opponent's turn.
     */
    private void opponentTurn() {
        if (!opponent.isAlive() || !selectedCharacter.isAlive()) {
            if (!opponent.isAlive()) {
                switchOpponent();
                return;
            }
            return;
        }

        int decision = (int) (Math.random() * 100);
        int opponentHealth = opponent.getHealth();
        int opponentStamina = opponent.getStamina();
        boolean actionTaken = false; // Track if the opponent has performed an action

        // Defensive strategy when health is critically low
        if (opponentHealth < 30 && opponentStamina >= 10 && !opponent.inDefenseStance) {
            opponent.setDefenseStance(true);
            floatingMessages.add(new FloatingText("Defend!", 500, 300, Color.GREEN, 15, 1000));
            opponentMessage = opponent.getName() + " defends strategically!";
            actionTaken = true;
        }

        // Use special ability if stamina is sufficient
        if (!actionTaken && opponentStamina >= 30 && decision < 60) {
            opponent.useSpecialAbility(selectedCharacter);
            floatingMessages
                    .add(new FloatingText("-" + (opponent.getDamage() * 2) + " HP", 250, 350, Color.ORANGE, 20, 1000));
            opponentMessage = opponent.getName() + " uses a powerful special ability!";
            actionTaken = true;
        }

        // Attack if stamina is sufficient and not defending
        if (!actionTaken && opponentStamina >= 10) {
            opponent.attack(selectedCharacter);
            opponent.reduceStamina(10);
            floatingMessages.add(new FloatingText("-" + opponent.getDamage() + " HP", 250, 350, Color.RED, 20, 1000));
            opponentMessage = opponent.getName() + " attacks!";
            actionTaken = true;
        }

        // Recover stamina if none of the above conditions are met
        if (!actionTaken) {
            opponent.recoverStamina();
            floatingMessages.add(new FloatingText("+10 Stamina", 500, 300, Color.BLUE, 20, 1000));
            opponentMessage = opponent.getName() + " recovers stamina!";
            actionTaken = true;
        }

        // Exit defense stance if in it and no other actions were taken
        if (opponent.inDefenseStance && decision > 50 && actionTaken) {
            opponent.setDefenseStance(false);
            floatingMessages.add(new FloatingText("Exit Defense", 500, 300, Color.GREEN, 15, 1000));
            opponentMessage = opponent.getName() + " exits defense stance for a counter!";
        }

        repaint();

        // Check if the player has been defeated
        if (!selectedCharacter.isAlive()) {
            opponentMessage = "You have been defeated!";
            setButtonsVisible(false);
        }

        // End the opponent's turn and give control back to the player
        playerTurn = true;
        if (selectedCharacter.inDefenseStance) {
            selectedCharacter.defend();
        }
    }
    // *********************************** //
    // Drawing Methods
    // *********************************** //

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw background
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, this);
        } else {
            // Fallback background color if the image isn't found
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // Draw the title
        if (showTitle) {
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.setColor(Color.BLACK);
            g.drawString("SHADOW ADVENTURERS", getWidth() / 2 - 200, 100);
        } else if (showCharacterSelection && !battleStarted) { /// Draw character selection only if active
            drawCharacterWithEnhancedBorder(g, warrior, 50, 300);
            drawCharacterWithEnhancedBorder(g, mage, 300, 300);
            drawCharacterWithEnhancedBorder(g, Assassin, 550, 300);

            g.setFont(new Font("Arial", Font.BOLD, 28)); /// Larger font for clarity
            g.setColor(new Color(51, 51, 255)); /// Blue color for better visibility
            g.drawString("Select Your Character", getWidth() / 2 - 180, 150); /// Position moved higher

            if (hoveredCharacter != null) {
                drawCharacterStats(g, hoveredCharacter, getWidth() / 2 - 180, 180); /// Adjusted position for stats
                                                                                    /// display
            }
        } else if (battleStarted) { /// Ensure character selection UI is hidden during battle
            if (selectedCharacter != null) {
                drawCharacter(g, selectedCharacter, 100, 300, false);
            }
            drawCharacter(g, opponent, 500, 300, true);
            drawHealthBar(g, selectedCharacter, 100, 200, selectedCharacter.inDefenseStance);
            drawHealthBar(g, opponent, 500, 200, opponent.inDefenseStance);
            drawStaminaBar(g, selectedCharacter, 100, 250);
            drawStaminaBar(g, opponent, 500, 250);
            if (!playerMessage.isEmpty()) {
                g.setFont(new Font("Arial", Font.BOLD, 20));
                g.setColor(Color.BLUE);
                g.drawString(playerMessage, getWidth() / 2 - 200, 150);
            }

            if (!opponentMessage.isEmpty()) {
                g.setFont(new Font("Arial", Font.BOLD, 20));
                g.setColor(Color.RED);
                g.drawString(opponentMessage, getWidth() / 2 - 200, 180);
            }
            drawExperienceBar(g, 275, 50);
        }

        // Floating messages
        Iterator<FloatingText> iterator = floatingMessages.iterator();
        while (iterator.hasNext()) {
            FloatingText message = iterator.next();
            if (message.isExpired()) {
                iterator.remove(); // Remove expired messages
            } else {
                message.render(g);
            }
        }

    }

    private void drawCharacterStats(Graphics g, Character character, int x, int y) {
        g.setFont(new Font("Arial", Font.BOLD, 16)); /// Bold and larger font for better readability
        g.setColor(new Color(0, 102, 102)); /// Teal color for distinctive appearance
        g.drawString("Name: " + character.getName(), x, y);
        g.drawString("Health: " + character.getHealth() + " / " + character.getMaxHealth(), x, y + 25);
        g.drawString("Stamina: " + character.getStamina() + " / " + character.getMaxStamina(), x, y + 50);
        g.drawString("Damage: " + character.getDamage(), x, y + 75);
    }

    /**
     * Draws a character with a border around it.
     */
    private void drawCharacterWithEnhancedBorder(Graphics g, Character character, int x, int y) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(6)); // Thicker border
        drawCharacter(g, character, x, y, false);

        // Draw rounded border
        g2.setColor(Color.black);
        Rectangle bounds = character.getBounds(x, y);
        g2.drawRoundRect(bounds.x - 5, bounds.y - 5, bounds.width + 0, bounds.height, 20, 20);

        // Draw bold character name
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString(character.getName(), x + bounds.width / 2 - 30, y + bounds.height + 20);

    }

    /**
     * Draws a character image.
     */
    private void drawCharacter(Graphics g, Character character, int x, int y, boolean flip) {
        Image img = character.getImage();
        if (img != null) {
            if (flip) {
                img = flipImage(img); // Flip image horizontally
            }
            g.drawImage(img, x, y, null);
        }
    }

    private Image flipImage(Image img) {
        BufferedImage bufferedImg = new BufferedImage(img.getWidth(null), img.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImg.createGraphics();
        g2d.drawImage(img, img.getWidth(null), 0, -img.getWidth(null), img.getHeight(null), null);
        g2d.dispose();
        return bufferedImg;
    }

    /**
     * Draws the health bar of a character.
     */
    private void drawHealthBar(Graphics g, Character character, int x, int y, boolean inDefenseStance) {
        int barWidth = 200; // Larger width for better visibility
        int barHeight = 20; // Larger height
        int cornerRadius = 15; // Rounded corners

        // Calculate health percentage
        double healthPercentage = (double) character.getHealth() / character.getMaxHealth();
        int filledWidth = (int) (barWidth * healthPercentage);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the background of the health bar
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRoundRect(x, y, barWidth, barHeight, cornerRadius, cornerRadius);

        // Draw the filled portion of the health bar
        g2.setColor(Color.GREEN);
        g2.fillRoundRect(x, y, filledWidth, barHeight, cornerRadius, cornerRadius);

        // Draw the bold border
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3)); // Bold border
        g2.drawRoundRect(x, y, barWidth, barHeight, cornerRadius, cornerRadius);

        // Draw the health text
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        String healthText = "HP: " + character.getHealth() + "/" + character.getMaxHealth();
        g.drawString(healthText, x + barWidth / 2 - 40, y + barHeight / 2 + 5);
    }

    private void switchOpponent() {
        // Check which opponent to load next
        if (opponent == mage) {
            opponent = warrior; // Switch to Warrior if the Mage is defeated
        } else if (opponent == warrior) {
            opponent = Assassin; // Switch to Assassin if the Warrior is defeated
        } else {
            // If all opponents are defeated, end the game
            playerMessage = "You defeated all opponents! You win!";
            setButtonsVisible(false); // Hide buttons, as the game is over
            return; // Exit the method
        }

        // Scale opponent difficulty
        // Adjust stats based on rounds (see Step 4)

        // Reset opponent's health for the next battle
        opponent.resetHealth();
        selectedCharacter.heal(50);
        // Show buttons again for the player
        setButtonsVisible(true);
        // Re-enable special button if allowed

        // Display a message for the new opponent
        playerMessage = "A new challenger approaches: " + opponent.getName();
        opponentMessage = ""; // Clear opponent message
        repaint(); // Redraw the panel with the new opponent
    }

    private void drawStaminaBar(Graphics g, Character character, int x, int y) {
        int barWidth = 200; // Larger width
        int barHeight = 20; // Larger height
        int cornerRadius = 15; // Rounded corners

        // Calculate stamina percentage
        double staminaPercentage = (double) character.getStamina() / character.getMaxStamina();
        int filledWidth = (int) (barWidth * staminaPercentage);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the background of the stamina bar
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRoundRect(x, y, barWidth, barHeight, cornerRadius, cornerRadius);

        // Draw the filled portion of the stamina bar
        g2.setColor(Color.BLUE);
        g2.fillRoundRect(x, y, filledWidth, barHeight, cornerRadius, cornerRadius);

        // Draw the bold border
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3)); // Bold border
        g2.drawRoundRect(x, y, barWidth, barHeight, cornerRadius, cornerRadius);

        // Draw the stamina text
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        String staminaText = "Stamina: " + character.getStamina() + "/" + character.getMaxStamina();
        g.drawString(staminaText, x + barWidth / 2 - 40, y + barHeight / 2 + 5);
    }

    private void drawExperienceBar(Graphics g, int x, int y) {
        int barWidth = 200; // Larger width for the experience bar
        int barHeight = 20; // Larger height for the experience bar
        int cornerRadius = 15; // Rounded corners

        // Calculate experience percentage
        double expPercentage = (double) experience / expToNextLevel;
        int filledWidth = (int) (barWidth * expPercentage);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the background of the experience bar
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRoundRect(x, y, barWidth, barHeight, cornerRadius, cornerRadius);

        // Draw the filled portion of the experience bar
        g2.setColor(Color.YELLOW);
        g2.fillRoundRect(x, y, filledWidth, barHeight, cornerRadius, cornerRadius);

        // Draw the bold border
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3)); // Bold border
        g2.drawRoundRect(x, y, barWidth, barHeight, cornerRadius, cornerRadius);

        // Draw the experience text
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 14)); // Bold text for clarity
        String expText = "XP: " + experience + "/" + expToNextLevel;
        g.drawString(expText, x + barWidth / 2 - 40, y + barHeight / 2 + 5); // Center text

        // Draw the level beside the experience bar
        String levelText = "Level: " + level;
        g.drawString(levelText, x + barWidth + 20, y + barHeight / 2 + 5);
    }

}
