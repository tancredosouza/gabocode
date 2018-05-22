package lgdt.movement.antigravity;

import lgdt.gun.VirtualBullet;
import lgdt.movement.antigravity.ForceField;
import lgdt.util.PT;
import lgdt.util.RobotInfo;

public class BulletField implements ForceField {
	private VirtualBullet bullet;
	private double mass;
	private boolean alive;

	public BulletField(VirtualBullet bullet, double mass) {
		this.bullet = bullet;
		this.mass = mass;
		alive = true;
	}

	public PT getForce(RobotInfo robot) {
		PT pos = bullet.getPosition(robot.getTime());
		if(pos.x < 0 || pos.x > 2200 || pos.y < 0 || pos.y > 2200) {
			alive = false;
		}
		double size = mass / Math.pow(pos.distance(robot.getPosition()), 2);
		return robot.getPosition().subtract(pos).normalize().scale(size);
	}

	public boolean canDestroy() {
		return !alive;
	}
}