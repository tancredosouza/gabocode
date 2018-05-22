package lgdt.movement.antigravity;

import lgdt.gun.VirtualBullet;
import lgdt.movement.antigravity.ForceField;
import lgdt.util.PT;
import lgdt.util.RobotInfo;

public class BulletField implements ForceField {
	private VirtualBullet bullet;
	private double mass, decay_power;
	private boolean alive;

	public BulletField(VirtualBullet bullet, double mass, double decay_power) {
		this.bullet = bullet;
		this.mass = mass;
		this.decay_power = decay_power;
		alive = true;
	}

	public PT getForce(RobotInfo robot) {
		PT pos = bullet.getPosition(robot.getTime());
		if(pos.x < 0 || pos.x > 2200 || pos.y < 0 || pos.y > 2200) {
			alive = false;
		}
		double size = mass / Math.pow(pos.distance(robot.getPosition()), decay_power);
		return robot.getPosition().subtract(pos).normalize().scale(size);
	}

	public boolean canDestroy() {
		return !alive;
	}
}