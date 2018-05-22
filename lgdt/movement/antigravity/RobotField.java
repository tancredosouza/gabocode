package lgdt.movement.antigravity;

import lgdt.util.PT;
import lgdt.util.RobotInfo;

import java.util.Random;

import javafx.scene.transform.Rotate;
import lgdt.movement.antigravity.ForceField;

public class RobotField implements ForceField {
    static double RANDOM_FACTOR = 400;
    RobotInfo enemy;
    double mass_const;

    public RobotField(RobotInfo enemy, double mass_const) {
        this.enemy = enemy;
        this.mass_const = mass_const;
    }

    public PT getForce(RobotInfo robot) {
        double square_distance = 
            Math.pow(enemy.getPosition().distance(robot.getPosition()), 2);
        double mass = mass_const * (enemy.getEnergy() - robot.getEnergy() 
                                    + RANDOM_FACTOR * Math.random());
        double size = mass / square_distance;
        return robot.getPosition()
                .subtract(enemy.getPosition())
                .normalize()
                .scale(size);
    }

    public boolean canDestroy() {
        return false;
    }
}