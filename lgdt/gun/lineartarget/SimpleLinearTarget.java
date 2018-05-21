package lgdt.gun.lineartarget;

import robocode.AdvancedRobot;

import java.util.Hashtable;
import java.util.Enumeration;

import lgdt.gun.VirtualGun;
import lgdt.gun.VirtualBullet;
import lgdt.util.RobotInfo;
import lgdt.util.PT;
import lgdt.util.Converter;

public class SimpleLinearTarget implements VirtualGun {
	private Hashtable<String, RobotInfo> targets = new Hashtable<String, RobotInfo>();

	public void addRobotInfo(RobotInfo robot) {
		targets.put(robot.getName(), robot);
	}

	public void onRobotDeath(String robotName) {
		targets.remove(robotName);
	}

	public VirtualBullet getBullet(RobotInfo robot) {return new VirtualBullet(new PT(0, 0), new PT(0, 0), 0);}

	public VirtualBullet getBullet(RobotInfo robot, AdvancedRobot r) {
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
		double power = 2.2;
		double bulletSpeed = (20 - 3 * power) * 2;
		double lateralVelocity = target.getVelocity().length() *
				Math.sin(target.getHeadingRadians() - Converter.convertRadian(robot.getPosition().angle()));
		lateralVelocity = target.getPosition().subtract(robot.getPosition()).normalize().cross(target.getVelocity());
		double offset = Math.asin(lateralVelocity / bulletSpeed);
		r.out.println("lateral velocity is " + lateralVelocity + ", offset is " + offset);
		if(robot.getTime() - target.getTime() > 10) {
			power = 0.5;
		}
		return new VirtualBullet(robot.getPosition(), target.getPosition().subtract(robot.getPosition()).normalize().scale(power).rotate(offset), robot.getTime());
	}

	public void run(AdvancedRobot robot) {
		VirtualBullet bullet = getBullet(new RobotInfo(robot), robot);
		if(bullet != null) {
			double deltaHeading = bullet.velocity.angle((new PT(0, 1)).rotate(-robot.getGunHeadingRadians()));
			double eps = 0.01;
			robot.setTurnGunRightRadians(deltaHeading);
			if(bullet.velocity.length() > 0.8 && -eps < deltaHeading && deltaHeading < eps) {
				robot.setFire(bullet.getFirepower());
			}
		}
	}
}