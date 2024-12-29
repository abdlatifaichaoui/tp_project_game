package Game;

import javax.imageio.ImageIO;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public abstract class Character {
    protected String name;
    protected int health;
    protected int damage;
    protected int stamina;
    protected int maxHealth;
    protected int maxStamina;
    protected Image image;
    protected boolean inDefenseStance;
    private Point position;

    public Character(String name, int health, int damage, int stamina, String imagePath) {
        this.name = name;
        this.health = health;
        this.damage = damage;
        this.stamina = stamina;
        this.maxHealth = health;
        this.maxStamina = stamina;
        this.inDefenseStance = false;

        this.position = new Point(0, 0);
        // Load the image
        try {
            this.image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
            this.image = null; // Default to null if the image can't be loaded
        }
    }

    public abstract void useSpecialAbility(Character opponent);

    public void attack(Character target) {
        if (stamina >= 10) {
            int finalDamage = damage;
            if (target.isInDefenseStance()) {
                finalDamage /= 2; // Halve the damage if the opponent is defending
            }
            target.takeDamage(finalDamage);

            System.out.println(name + " attacks " + target.getName() + " for " + finalDamage + " damage.");
        } else {
            System.out.println(name + " is too tired to attack!");
        }
    }

    public void defend() {
        if (stamina < maxStamina) {
            stamina += 5; // Adjust the recovery amount (e.g., 5 stamina)
            if (stamina > maxStamina) {
                stamina = maxStamina; // Ensure stamina doesn't exceed max
            }
        }
    }

    public void resetDefenseStance() {
        inDefenseStance = false;

    }

    public void recoverStamina() {
        if (stamina < maxStamina) {
            stamina += 15; // Adjust the amount as needed
            if (stamina > maxStamina) {
                stamina = maxStamina;
            }
        }
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(int x, int y) {
        position.setLocation(x, y); // Update the position using Point's setLocation method
    }

    public void takeDamage(int damage) {
        health = Math.max(0, health - damage);
        System.out.println(name + " takes " + damage + " damage. Remaining health: " + health);
    }

    public boolean isAlive() {
        return health > 0;
    }

    public Rectangle getBounds(int x, int y) {
        if (image != null) {
            return new Rectangle(x, y, image.getWidth(null), image.getHeight(null));
        }
        return new Rectangle(x, y, 0, 0); // Default to zero if the image is not loaded
    } // Heal method

    public void heal(int amount) {
        if (amount > 0) {
            health = Math.min(maxHealth, health + amount);

        }
    }

    public boolean isInDefenseStance() {
        return inDefenseStance;
    }

    public void setDefenseStance(boolean defense) {
        inDefenseStance = defense; // Track whether in defense stance

    }

    public void setHealth(int health) {
        this.health = Math.min(maxHealth, health);
    }

    public void resetHealth() {
        health = maxHealth;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public void reduceStamina(int st) {
        stamina = stamina - st;
    }

    public int getDamage() {
        return damage;
    }

    public int getStamina() {
        return stamina;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int h) {
        maxHealth = h;
    }

    public void setDamage(int d) {
        damage = d;
    }

    public int getMaxStamina() {
        return maxStamina;
    }

    public Image getImage() {
        return image;
    }

    public void draw(Graphics g, int x, int y) {
        if (image != null) {
            g.drawImage(image, x, y, null);

        }
    }
}
