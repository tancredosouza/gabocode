package lgdt.movement.antigravity;

import lgdt.util.PT;
import lgdt.util.RobotInfo;
import lgdt.movement.antigravity.ForceField;

public class GravityPoint implements ForceField { 
    PT position;
    double mass;
    double decay_power;

    public GravityPoint(PT position, double mass, double decay_power) {
        this.position = position;
        this.mass = mass;
        this.decay_power = decay_power;
    }

    public GravityPoint(PT position, double mass) {
        this.position = position;
        this.mass = mass;
        this.decay_power = 2;
    }

    public PT getForce(RobotInfo robot) {
        double size = mass / Math.pow(position.distance(robot.getPosition()), decay_power);
        if(mass < 0) {
            size = Math.max(size, mass / 1e5);
        }
        return robot.getPosition().subtract(position).normalize().scale(size);
    }

    public boolean canDestroy() {
        return false;
    }
}