package lgdt.util;

import java.lang.Math;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

import lgdt.util.PT;

public class RobotInfo {
	public PT position, velocity;
	public String name;
	public long time;
	public boolean isEnemy;

	public RobotInfo(AdvancedRobot robot, ScannedRobotEvent e, boolean isEnemy) {
		name = e.getName();
		time = robot.getTime();
		double absoluteBearing = robot.getHeadingRadians() + e.getBearingRadians();
		double robotX = robot.getX() + e.getDistance() * Math.sin(absoluteBearing);
		double robotY = robot.getY() + e.getDistance() * Math.cos(absoluteBearing);
		position = new PT(robotX, robotY);
		velocity = (new PT(0, e.getVelocity())).rotate(-e.getHeadingRadians());
		this.isEnemy = isEnemy;
	}

	public RobotInfo(AdvancedRobot robot) {
		name = robot.getName();
		time = robot.getTime();
		position = new PT(robot.getX(), robot.getY());
		velocity = (new PT(0, robot.getVelocity())).rotate(-robot.getHeadingRadians());
		isEnemy = false;
	}

	public boolean isEnemy() {
		return this.isEnemy;
	}
}