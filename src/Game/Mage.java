package Game;

public class Mage extends Character {
    public Mage(String name, int health, int damage, int stamina, String imagePath) {
        super(name, health, damage, stamina, imagePath);
    }

    @Override
    public void useSpecialAbility(Character opponent) {
        if (stamina >= 30 && health != maxHealth) {
            System.out.println(getName() + " uses Heal!");
            int healAmount = 20; // Heal for 20 HP
            setHealth(Math.min(getHealth() + healAmount, 100));
            stamina -= 30;

        }
    }

}