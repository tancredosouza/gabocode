package lgdt.radar.simpleradar;

import robocode.AdvancedRobot;

import lgdt.util.RobotInfo;
import lgdt.util.SubSystem;

public class SimpleRadar extends SubSystem {
	AdvancedRobot robot = null;
	public void init(AdvancedRobot robot) { this.robot = robot; }
	public void run() { robot.setTurnRadarRightRadians(Double.POSITIVE_INFINITY); }
}