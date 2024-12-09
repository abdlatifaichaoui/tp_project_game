package Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import java.util.Iterator;
import javax.swing.border.AbstractBorder;

public class GamePanel extends JPanel {
    // *********************************** //
    // Fields
    // *********************************** //
    private Character selectedCharacter;
    private Character mage = new Mage("Harry Potter", 100, 12, 60, "src/images/m.png");
    private Character warrior = new Warrior("Kreatos", 100, 15, 50, "src/images/w.png");
    private Character Assassin = new Assassin("Zoro", 100, 10, 50, "src/images/a.png");
    private Character opponent = mage;
    private int roundCounter = 0; // Counter to track the number of rounds
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

        // Initialize action buttons
        setLayout(null);
        playButton = new JButton("Play");
        playButton.setBounds(350, 250, 100, 50);
        playButton.setFont(new Font("Arial", Font.BOLD, 18));
        playButton.setBackground(new Color(144, 238, 144)); // Light green
        playButton.setFocusPainted(false);
        playButton.setBorder(new RoundedBorder(Color.BLACK, 3, 15)); // Rounded border
        playButton.addActionListener(e -> {
            showTitle = false;
            showCharacterSelection = true; // Switch to character selection
            playButton.setVisible(false); // Hide the play button
            repaint(); // Redraw the screen
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
    // *********************************** //
    // Helper Methods
    // *********************************** //

    /**
     * Creates a styled button with a custom look.
     */
    private JButton createStyledButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 120, 40);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.BLACK);
        button.setBackground(new Color(230, 230, 250)); // Lavender background
        button.setFocusPainted(false);
        button.setBorder(new RoundedBorder(Color.BLACK, 3, 15)); // Thick black border with corner radius

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
            specialButton.setEnabled(selectedCharacter.getStamina() >= 20); // Example stamina requirement
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
            selectedCharacter = new Warrior("Kreatos", 100, 15, 50, "src/images/w.png");
        } else if (mage.getBounds(300, 300).contains(mouseX, mouseY)) {
            selectedCharacter = new Mage("Harry Potter", 100, 15, 50, "src/images/m.png");
        } else if (Assassin.getBounds(500, 300).contains(mouseX, mouseY)) {
            selectedCharacter = new Assassin("Zoro", 100, 15, 50, "src/images/a.png");
        }
        if (selectedCharacter != null) {
            battleStarted = true;
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
                } else {
                    // Optionally, you can display a message when stamina is not enough
                    playerMessage = selectedCharacter.getName() + " does not have enough stamina!";
                }
                break;
            case "Recover":
                selectedCharacter.recoverStamina();
                floatingMessages.add(new FloatingText("+10 Stamina", 100, 350, Color.BLUE, 20, 1000));
                playerMessage = selectedCharacter.getName() + " recovers stamina!";
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
        selectedCharacter.processStatusEffects();
        opponent.processStatusEffects();

        repaint();

        if (!opponent.isAlive()) {
            playerMessage = "You Win!";
            switchOpponent();
            return;
        }

        playerTurn = false;
        if (opponent.inDefenseStance) {
            opponent.defend();
        }
        Timer timer = new Timer(1000, e -> opponentTurn());
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
                return; // Exit this turn, as a new opponent has been set
            }
            return;
        }

        int decision = (int) (Math.random() * 100);
        int opponentHealth = opponent.getHealth();
        int opponentStamina = opponent.getStamina();

        // Prioritize defense if opponent is low on health
        if (opponentHealth < 30) {
            opponent.setDefenseStance(true);
            floatingMessages.add(new FloatingText("Defend!", 500, 300, Color.GREEN, 15, 1000));
            opponentMessage = opponent.getName() + " enters defense stance!";
            return; // Skip attack for defense stance
        }

        // High chance to defend if stamina is high and health is low
        if (decision < 30 + (roundCounter * 2) && opponentHealth < 50) {
            opponent.setDefenseStance(true);
            floatingMessages.add(new FloatingText("Defend!", 500, 300, Color.GREEN, 15, 1000));
            opponentMessage = opponent.getName() + " enters defense stance!";
        }
        // If the opponent has a special ability, prioritize using it strategically
        else if (decision < 60 + (roundCounter * 2) && opponentStamina >= 30) {
            int ddamage = opponent.getDamage();
            if (selectedCharacter.inDefenseStance) {
                ddamage = ddamage / 2;
            }

            opponent.useSpecialAbility(selectedCharacter);

            if (opponent == mage) {
                floatingMessages.add(new FloatingText("+" + 20 + " HP", 500, 350, Color.green, 20, 1000));
                opponentMessage = opponent.getName() + " uses a special ability!";
            } else if (opponent == warrior || opponent == Assassin) {
                floatingMessages.add(new FloatingText("-" + (ddamage * 2) + " HP", 250, 350, Color.ORANGE, 20, 1000));
                opponentMessage = opponent.getName() + " uses a special ability!";
            }
        }
        // If stamina is low, switch to a defensive or restorative strategy
        else if (opponentStamina < 10) {
            opponent.setDefenseStance(true);
            opponentMessage = opponent.getName() + " enters defense stance to conserve stamina!";
            floatingMessages.add(new FloatingText("Defend!", 500, 300, Color.GREEN, 15, 1000));
        } else {
            int ddamage = opponent.getDamage();
            if (selectedCharacter.inDefenseStance) {
                ddamage = ddamage / 2;
            }

            opponent.attack(selectedCharacter);
            opponent.reduceStamina(10);
            floatingMessages.add(new FloatingText("-" + ddamage + " HP", 250, 350, Color.RED, 20, 1000));
            opponentMessage = opponent.getName() + " attacks!";
        }
        int ddamage = opponent.getDamage();
        if (selectedCharacter.inDefenseStance) {
            ddamage = ddamage / 2;
        }
        // AI's decision based on player’s state (if player is often defending, attack
        // more aggressively)
        if (selectedCharacter.inDefenseStance && opponentStamina >= 20) {
            // If the player is in defense stance, the opponent could try to use special
            // attacks or wait for an opening.
            if (decision < 40) {
                opponent.setDefenseStance(true);
                floatingMessages.add(new FloatingText("Defend!", 500, 300, Color.GREEN, 15, 1000));
                opponentMessage = opponent.getName() + " prepares defensively!";
            } else if (decision < 60) {
                opponent.attack(selectedCharacter); // Proceed to attack
                opponent.reduceStamina(10);
                floatingMessages.add(new FloatingText("-" + ddamage + " HP", 250, 350, Color.RED, 20, 1000));
                opponentMessage = opponent.getName() + " attacks!";
            }
        }

        // Update status effects
        selectedCharacter.processStatusEffects();
        opponent.processStatusEffects();

        repaint();

        if (!selectedCharacter.isAlive()) {
            opponentMessage = "You have been defeated!";
            setButtonsVisible(false);
        }

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
        }

        if (!showCharacterSelection) {
            // Only display the "Play" button screen, no extra text
        } else if (!battleStarted) {
            // Character selection screen
            drawCharacterWithEnhancedBorder(g, warrior, 50, 300);
            drawCharacterWithEnhancedBorder(g, mage, 300, 300);
            drawCharacterWithEnhancedBorder(g, Assassin, 550, 300);

            // Draw "Select Your Character" text
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.setColor(Color.BLACK);
            g.drawString("Select Your Character", getWidth() / 2 - 150, 200);
        } else {
            // Battle screen logic
            if (selectedCharacter != null) {
                drawCharacter(g, selectedCharacter, 100, 300, false); // Player's character
            }
            drawCharacter(g, opponent, 500, 300, true); // Opponent's character

            // Draw health and stamina bars
            drawHealthBar(g, selectedCharacter, 100, 400, selectedCharacter.inDefenseStance);
            drawHealthBar(g, opponent, 500, 400, opponent.inDefenseStance);
            drawStaminaBar(g, selectedCharacter, 100, 400);
            drawStaminaBar(g, opponent, 500, 400);

            // Show messages
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
        int barWidth = 100;
        int barHeight = 10;

        // Calculate health percentage based on maxHealth
        double healthPercentage = (double) character.getHealth() / character.getMaxHealth();
        int filledWidth = (int) (barWidth * healthPercentage);

        // Position the health bar above the character
        int barX = x + 50; // Center the bar above the character image
        int barY = y - 150; // Place above the image

        // Draw the background of the health bar
        g.setColor(Color.RED);
        g.fillRect(barX, barY, barWidth, barHeight);

        // Draw the filled portion of the health bar
        g.setColor(Color.GREEN);
        g.fillRect(barX, barY, filledWidth, barHeight);

        // Draw the health text (e.g., "80/100 HP")
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        String healthText = character.getHealth() + " HP";
        g.drawString(healthText, barX + 20, barY - 5);
        if (inDefenseStance) {
            Image shieldIcon = new ImageIcon("src/images/blue_shield.png").getImage();
            g.drawImage(shieldIcon, x + 150, y - 155, 20, 20, null); // Adjust position and size as needed
        }
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
        opponent.increaseDifficulty(roundCounter); // Adjust stats based on rounds (see Step 4)

        // Reset opponent's health for the next battle
        opponent.resetHealth();

        // Show buttons again for the player
        setButtonsVisible(true);
        // Re-enable special button if allowed

        // Display a message for the new opponent
        playerMessage = "A new challenger approaches: " + opponent.getName();
        opponentMessage = ""; // Clear opponent message
        repaint(); // Redraw the panel with the new opponent
    }

    private void drawStaminaBar(Graphics g, Character character, int x, int y) {
        int barWidth = 100; // Width of the stamina bar
        int barHeight = 10; // Height of the stamina bar

        // Calculate stamina percentage based on maxStamina
        double staminaPercentage = (double) character.getStamina() / character.getMaxStamina();
        int filledWidth = (int) (barWidth * staminaPercentage);

        // Position the stamina bar above the character (adjust position as needed)
        int barX = x + 50; // Center the bar above the character image
        int barY = y - 110; // Adjust the vertical position (make it above health bar)

        // Draw the background of the stamina bar (gray color)
        g.setColor(Color.GRAY);
        g.fillRect(barX, barY, barWidth, barHeight);

        // Draw the filled portion of the stamina bar (green color for positive)
        g.setColor(Color.BLUE); // You can use different colors for stamina (blue for example)
        g.fillRect(barX, barY, filledWidth, barHeight);

        // Optionally, you can add a border for the stamina bar for a better look
        g.setColor(Color.BLACK); // Border color
        g.drawRect(barX, barY, barWidth, barHeight);

        // Optionally, draw the stamina text (e.g., "50/100 Stamina")
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        String staminaText = character.getStamina() + "/" + character.getMaxStamina() + " Stamina";
        g.drawString(staminaText, barX + 20, barY - 5); // Adjust text positioning as needed
    }

}