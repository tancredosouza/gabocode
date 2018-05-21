package lgdt.movement.antigravity;

import lgdt.util.PT;
import lgdt.movement.antigravity.ForceField;

public class GravityPoint implements ForceField { 
    PT position;
    double mass;
    double decay_power;

    public GravityPoint(PT position, double mass, int decay_power) {
        this.position = position;
        this.mass = mass;
        this.decay_power = decay_power;
    }

    public GravityPoint(PT position, double mass) {
        this.position = position;
        this.mass = mass;
        this.decay_power = 2;
    }

    public PT getForce(PT p) {
        double size = mass / Math.pow(position.distance(p), decay_power);
        return p.subtract(position).normalize().scale(size);
    }
}