package Game;

public class Warrior extends Character {

    public Warrior(String name, int health, int damage, int stamina, String imagePath) {
        super(name, health, damage, stamina, imagePath);
    }

    @Override
    public void useSpecialAbility(Character opponent) {
        if (stamina >= 30) {
            System.out.println(getName() + " uses Power Strike!");
            int specialDamage = getDamage() * 2; // Deals double damage
            opponent.takeDamage(specialDamage);
            stamina -= 30;
            System.out.println(opponent.getName() + " takes " + specialDamage + " damage!");
        } else {
            System.out.println(getName() + " doesn't have enough stamina for Power Strike!");
        }
    }

    public boolean canUseSpecialAbility() {
        return stamina >= 30;
    }
}
