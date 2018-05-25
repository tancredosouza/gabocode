package lgdt.gun.lineartarget;

import robocode.AdvancedRobot;
import robocode.util.Utils;

import java.util.Hashtable;
import java.util.Enumeration;

import lgdt.gun.VirtualGun;
import lgdt.gun.VirtualBullet;
import lgdt.util.RobotInfo;
import lgdt.util.PT;
import lgdt.util.BattleField;

public class IterativeLinearTarget extends VirtualGun {
	BattleField battleField = null;
	AdvancedRobot robot = null;

	public void setBattleField(BattleField battleField) { this.battleField = battleField; }
	public void init(AdvancedRobot robot) { this.robot = robot;	}

	public VirtualBullet getBullet(RobotInfo robot, RobotInfo target, double power) {
		double bulletSpeed = (20 - 3 * power);
		PT predictedPosition = new PT(target.getPosition().x, target.getPosition().y);
		double deltaTime = 0;
		while((deltaTime++) * bulletSpeed < robot.getPosition().distance(predictedPosition)) {
			predictedPosition = predictedPosition.add(robot.getVelocity());
			if(!battleField.contains(predictedPosition, 18)) {
				predictedPosition = new PT(Math.min(Math.max(predictedPosition.x, 18), battleField.getBattleFieldWidth() - 18), 
										   Math.min(Math.max(predictedPosition.y, 18), battleField.getBattleFieldHeight() - 18));
				break;
			}
		}
		return new VirtualBullet(robot.getPosition(), predictedPosition.subtract(robot.getPosition()).normalize().scale(bulletSpeed), robot.getTime());
	}

	public VirtualBullet getBullet(RobotInfo robot) {
		// choosing target
		RobotInfo target = null;
		double targetDistance = 1e9;
		Enumeration<RobotInfo> it = battleField.elements();
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
