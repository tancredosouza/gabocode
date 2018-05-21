package lgdt.movement.antigravity;

import lgdt.util.PT;

public class GravityPoint { 
    PT position;
    double mass;
    int decay_power;

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

    public PT force(PT p) {
        double size = mass / Math.pow(position.distance(p), decay_power);
        return p.subtract(position).normalize().scale(size);
    }
}