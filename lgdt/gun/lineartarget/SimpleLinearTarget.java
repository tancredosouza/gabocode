package lgdt.gun.lineartarget;

import robocode.AdvancedRobot;
import robocode.util.Utils;

import java.util.HashMap;
import java.util.Iterator;

import lgdt.gun.VirtualGun;
import lgdt.gun.VirtualBullet;
import lgdt.util.RobotInfo;
import lgdt.util.PT;
import lgdt.util.BattleField;
import lgdt.util.Converter;

public class SimpleLinearTarget extends VirtualGun {
	BattleField battleField = null;
	AdvancedRobot robot = null;

	public void setBattleField(BattleField battleField) { this.battleField = battleField; }
	public void init(AdvancedRobot robot) { this.robot = robot;	}

	public VirtualBullet getBullet(RobotInfo robot, RobotInfo target, double power) {
		double bulletSpeed = (20 - 3 * power);
		double lateralVelocity = target.getVelocity().length() *
				Math.sin(target.getHeadingRadians() - Converter.convertRadian(robot.getPosition().angle()));
		lateralVelocity = target.getPosition().subtract(robot.getPosition()).normalize().cross(target.getVelocity());
		double offset = Math.asin(lateralVelocity / bulletSpeed);
		if(robot.getTime() - target.getTime() > 10) {
			power = 0.5;
		}
		return new VirtualBullet(robot.getPosition(), target.getPosition().subtract(robot.getPosition()).normalize().scale(bulletSpeed).rotate(offset), robot.getTime());
	}

	public VirtualBullet getBullet(RobotInfo robot) {
		// choosing target
		RobotInfo target = null;
		double targetDistance = 1e9;
		Iterator<RobotInfo> it = battleField.values();
		while(it.hasNext()) {
			RobotInfo nxt = (RobotInfo) it.next();
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

	public void run() {
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
