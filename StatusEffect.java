package Game;

public abstract class StatusEffect {
    private int duration;

    public StatusEffect(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public void reduceDuration() {
        duration--;
    }

    public abstract void applyEffect(Character target);
}

class PoisonEffect extends StatusEffect {
    public PoisonEffect(int duration) {
        super(duration);
    }

    @Override
    public void applyEffect(Character target) {
        target.takeDamage(5); // Deal 5 damage per turn
    }
}

class HealOverTimeEffect extends StatusEffect {
    public HealOverTimeEffect(int duration) {
        super(duration);
    }

    @Override
    public void applyEffect(Character target) {
        target.heal(5); // Heal 5 HP per turn
    }
}
