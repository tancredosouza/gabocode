package lgdt.util;

import java.io.Serializable;

import robocode.util.Utils;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

import lgdt.util.PT;

public class RobotInfo implements Serializable {
	private PT position, velocity;
	private double speed;
	private String name;
	private long time;
	private double energy;
	private boolean isEnemy;
	private double headingRadian, headingRadianSpeed;

	public RobotInfo(AdvancedRobot robot, ScannedRobotEvent e, boolean isEnemy) {
		name = e.getName();
		time = robot.getTime();
		double absoluteBearing = robot.getHeadingRadians() + e.getBearingRadians();
		double robotX = robot.getX() + e.getDistance() * Math.sin(absoluteBearing);
		double robotY = robot.getY() + e.getDistance() * Math.cos(absoluteBearing);
		position = new PT(robotX, robotY);
		velocity = (new PT(0, e.getVelocity())).rotate(-e.getHeadingRadians());
		this.isEnemy = isEnemy;
		energy = e.getEnergy();
		headingRadian = e.getHeadingRadians();
		headingRadianSpeed = 0;
		speed = e.getVelocity();
	}

	public RobotInfo(AdvancedRobot robot) {
		name = robot.getName();
		time = robot.getTime();
		position = new PT(robot.getX(), robot.getY());
		velocity = (new PT(0, robot.getVelocity())).rotate(-robot.getHeadingRadians());
		isEnemy = false;
		energy = robot.getEnergy();
		headingRadian = robot.getHeadingRadians();
		headingRadianSpeed = 0;
		speed = robot.getVelocity();
	}

	public void merge(RobotInfo robot) {
		double ratio = 0.83;
		long ticks = robot.getTime() - this.getTime();
		if(ticks > 0) {
			double averageAngleRatio = Utils.normalRelativeAngle(robot.getHeadingRadians() - this.getHeadingRadians()) / ticks;
			while(ticks > 0) {
				velocity = velocity.scale(1 - ratio).add(robot.getVelocity().scale(ratio));
				headingRadianSpeed = headingRadianSpeed * ratio + averageAngleRatio * (1 - ratio);
				ticks--;
			}
		}
		speed = robot.speed;
		time = robot.time;
		energy = robot.energy;
		headingRadian = robot.headingRadian;
		position = robot.position;
	}

	public PT getPosition() { return position; }
	public PT getVelocity() { return velocity; }
	public double getSpeed() { return speed; }
	public String getName() { return name; }
	public long getTime() { return time; }
	public double getEnergy() { return energy; }
	public double getHeadingRadians() { return headingRadian; }
	public double getHeadingRadianSpeed() { return headingRadianSpeed; }
	public boolean isEnemy() { return this.isEnemy; }

	public void setVelocity(PT velocity) { this.velocity = velocity; }
	public void setHeadingRadianSpeed(double speed) { this.headingRadianSpeed = speed; }
}