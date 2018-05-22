package lgdt.gun.headon;

import robocode.AdvancedRobot;

import java.util.Hashtable;
import java.util.Enumeration;

import lgdt.gun.VirtualGun;
import lgdt.gun.VirtualBullet;
import lgdt.util.RobotInfo;
import lgdt.util.PT;

public class HeadOnGun implements VirtualGun {
	private Hashtable<String, RobotInfo> targets = new Hashtable<String, RobotInfo>();

	public void addRobotInfo(RobotInfo robot) {
		targets.put(robot.getName(), robot);
	}

	public void onRobotDeath(String robotName) {
		targets.remove(robotName);
	}

	public void init(AdvancedRobot robot) {
		
	}

	public VirtualBullet getBullet(RobotInfo robot, RobotInfo target, double power) {
		return new VirtualBullet(robot.getPosition(), target.getPosition().subtract(robot.getPosition()).normalize().scale(20 - 3 * power), robot.getTime());
	}

	public VirtualBullet getBullet(RobotInfo robot) {
		// choosing target
		RobotInfo target = null;
		double targetDistance = 1e9;
		Enumeration<RobotInfo> it = targets.elements();
		while(it.hasMoreElements()) {
			RobotInfo nxt = (RobotInfo) it.nextElement();
			if(nxt.isEnemy()) {
				if(robot.getPosition().distance(nxt.getPosition()) < targetDistance) {
					targetDistance = robot.getPosition().distance(nxt.getPosition());
					target = nxt;
				}
			}
		}
		if(target == null) {
			return null;
		}
		// choosing firing angle
		double power = 3;
		return getBullet(robot, target, power);
	}

	public void run(AdvancedRobot robot) {
		VirtualBullet bullet = getBullet(new RobotInfo(robot));
		if(bullet != null) {
			double deltaHeading = bullet.velocity.angle((new PT(0, 1)).rotate(-robot.getGunHeadingRadians()));
			double eps = 1e-3;
			robot.setTurnGunRightRadians(deltaHeading);
			if(-eps < deltaHeading && deltaHeading < eps) {
				robot.setFire(bullet.getFirepower());
			}
		}
	}
}