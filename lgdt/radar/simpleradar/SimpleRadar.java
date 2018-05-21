package lgdt.radar.simpleradar;

import robocode.AdvancedRobot;

import lgdt.util.RobotInfo;
import lgdt.util.SubSystem;

public class SimpleRadar implements SubSystem {
	public void addRobotInfo(RobotInfo robot) {}
	public void onRobotDeath(String robotName) {}
	public void run(AdvancedRobot robot) {
		robot.setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
	}
}