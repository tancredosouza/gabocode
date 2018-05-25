package lgdt.gun;

import robocode.AdvancedRobot;

import lgdt.util.PT;
import lgdt.util.RobotInfo;
import lgdt.gun.VirtualBullet;
import lgdt.util.SubSystem;

public abstract class VirtualGun extends SubSystem {
	public abstract VirtualBullet getBullet(RobotInfo robot, RobotInfo target, double power);
	public abstract VirtualBullet getBullet(RobotInfo robot);

	public boolean aimGun(AdvancedRobot robot, VirtualBullet bullet, double eps) {
		double deltaHeading = bullet.velocity.angle((new PT(0, 1)).rotate(-robot.getGunHeadingRadians()));
		robot.setTurnGunRightRadians(deltaHeading);
		robot.out.println("deltaHeading is " + deltaHeading);
		return Math.abs(deltaHeading) <= eps;
	}

	public boolean aimGun(AdvancedRobot robot, VirtualBullet bullet) {
		return aimGun(robot, bullet, 1e-3);
	}
}