package lgdt.gun.circulartarget;

import robocode.AdvancedRobot;
import robocode.util.Utils;

import java.util.Hashtable;
import java.util.Enumeration;

import lgdt.gun.VirtualGun;
import lgdt.gun.VirtualBullet;
import lgdt.util.RobotInfo;
import lgdt.util.PT;

public class IterativeCircularTarget extends VirtualGun {
	static public void merge(RobotInfo robot, RobotInfo oldRobot) {
		double ratio = 0.83;
		long ticks = robot.getTime() - oldRobot.getTime();
		double headingAngleRatio = oldRobot.getHeadingRadianSpeed();
		if(ticks > 0) {
			PT velocity = robot.getVelocity();
			double averageAngleRatio = Utils.normalRelativeAngle(robot.getHeadingRadians() - oldRobot.getHeadingRadians()) / ticks;
			robot.setVelocity(oldRobot.getVelocity());
			while(ticks > 0) {
				robot.setVelocity(velocity.scale(1 - ratio).add(robot.getVelocity().scale(ratio)));
				headingAngleRatio = headingAngleRatio * ratio + averageAngleRatio * (1 - ratio);
				ticks--;
			}
		}
		robot.setHeadingRadianSpeed(headingAngleRatio);
	}

	private double battleFieldHeight, battleFieldWidth;
	private Hashtable<String, RobotInfo> targets = new Hashtable<String, RobotInfo>();

	public void init(AdvancedRobot robot) {
		battleFieldHeight = robot.getBattleFieldHeight();
		battleFieldWidth = robot.getBattleFieldWidth();
	}

	public void addRobotInfo(RobotInfo robot) {
		RobotInfo oldRobot = robot;
		if(targets.containsKey(robot.getName())) {
			oldRobot = targets.get(robot.getName());
		}
		merge(robot, oldRobot);
		targets.put(robot.getName(), robot);
	}

	public void onRobotDeath(String robotName) {
		targets.remove(robotName);
	}

	public VirtualBullet getBullet(RobotInfo robot, RobotInfo target, double power) {
		double bulletSpeed = (20 - 3 * power);
		double radius = target.getSpeed() / target.getHeadingRadianSpeed();
		PT predictedPosition = new PT(target.getPosition().x, target.getPosition().y);
		double deltaTime = 0;
		while((deltaTime++) * bulletSpeed < robot.getPosition().distance(predictedPosition)) {
			double tothead = deltaTime * target.getHeadingRadianSpeed();
			if(radius > 3000) {
				predictedPosition = target.getPosition().add(target.getVelocity().scale(deltaTime));
			} else {
				predictedPosition.y = target.getPosition().y + (Math.sin(target.getHeadingRadians() + tothead) - Math.sin(target.getHeadingRadians())) * radius;
				predictedPosition.x = target.getPosition().x + (Math.cos(target.getHeadingRadians()) - Math.cos(target.getHeadingRadians() + tothead)) * radius;
			}
			if(predictedPosition.x < 18 || predictedPosition.x > battleFieldWidth - 18 || 
			   predictedPosition.y < 18 || predictedPosition.y > battleFieldHeight - 18) {
				predictedPosition = new PT(Math.min(Math.max(predictedPosition.x, 18), battleFieldWidth - 18), 
										   Math.min(Math.max(predictedPosition.y, 18), battleFieldHeight - 18));
				break;
			}
		}
		return new VirtualBullet(robot.getPosition(), predictedPosition.subtract(robot.getPosition()).normalize().scale(bulletSpeed), robot.getTime());
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
		double power;
		double distance = robot.getPosition().distance(target.getPosition());
		if(distance < 200) {
			power = 3;
		} else if(distance < 600) {
			power = 2.5;
		} else if(robot.getEnergy() > 20) {
			power = 2.2;
		} else {
			power = 0.1;
		}
		return getBullet(robot, target, power);
	}

	public void run(AdvancedRobot robot) {
		/*if(Math.abs(robot.getGunTurnRemaining()) > 1e-9) {
			return;
		}*/
		VirtualBullet bullet = getBullet(new RobotInfo(robot));
		if(bullet != null) {
			boolean isAimed = super.aimGun(robot, bullet, 0.01);
			robot.out.println(isAimed + " and firepower " + bullet.getFirepower());
			if(bullet.getFirepower() > 0.8 && isAimed) {
				robot.setFire(bullet.getFirepower());
			}
		}
	}
}
