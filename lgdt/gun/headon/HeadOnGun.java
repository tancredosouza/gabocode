package lgdt.gun.headon;

import robocode.AdvancedRobot;

import java.util.Hashtable;
import java.util.Enumeration;

import lgdt.gun.VirtualGun;
import lgdt.gun.VirtualBullet;
import lgdt.util.RobotInfo;
import lgdt.util.PT;

public class HeadOnGun implements VirtualGun {
	Hashtable<String, RobotInfo> targets;

	public HeadOnGun() {
		targets = new Hashtable<String, RobotInfo>();
	}

	public void addRobotInfo(RobotInfo robot) {
		targets.put(robot.name, robot);
	}

	public void onRobotDeath(String robotName) {
		targets.remove(robotName);
	}

	public VirtualBullet getBullet(RobotInfo robot) {
		// choosing target
		RobotInfo target = null;
		double targetDistance = 1e9;
		Enumeration<RobotInfo> it = targets.elements();
		while(it.hasMoreElements()) {
			RobotInfo nxt = (RobotInfo) it.nextElement();
			if(nxt.isEnemy()) {
				if(robot.position.distance(nxt.position) < targetDistance) {
					targetDistance = robot.position.distance(nxt.position);
					target = nxt;
				}
			}
		}
		if(target == null) {
			return null;
		}
		// choosing firing angle
		double power = 3;
		return new VirtualBullet(robot.position, target.position.subtract(robot.position).normalize().scale(power), robot.time);
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