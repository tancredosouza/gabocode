package lgdt.energydrop;

import robocode.AdvancedRobot;

import lgdt.util.RobotInfo;

import java.util.Hashtable;

public class SimpleEnergyDropScanner {
	private Hashtable<String, RobotInfo> targets = new Hashtable<String, RobotInfo>();
	private Hashtable<String, long> lastDrop = new Hashtable<String, long>();

	public void addRobotInfo(RobotInfo robot) {
		boolean isDrop = false;
		if(targets.contains(robot.getName())) {
			isDrop = lastDrop.get(robot.getName()) <= robot.getTime() && robot.getEnergy() < targets.get(robot.getName()).getEnergy();
			if(isDrop) {
				lastDrop.put(robot.getName(), robot.getTime());
			}
		} else {
			lastDrop.put(robot.getName(), 0);
		}
		targets.put(robot.getName(), robot);
		return isDrop;
	}

	public void onRobotDeath(String robotName) {
		targets.remove(robotName);
		lastDrop.remove(robotName);
	}
}