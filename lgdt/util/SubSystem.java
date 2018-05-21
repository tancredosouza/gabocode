package lgdt.util;

import robocode.AdvancedRobot;

import lgdt.util.RobotInfo;

public interface SubSystem {
	public void addRobotInfo(RobotInfo robot);
	public void onRobotDeath(String robotName);
	public void run(AdvancedRobot robot);
}