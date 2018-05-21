package lgdt.energydrop;

import lgdt.util.RobotInfo;

import java.lang.Long;
import java.util.Hashtable;

public class SimpleEnergyDropScanner {
	private Hashtable<String, RobotInfo> targets = new Hashtable<String, RobotInfo>();
	private Hashtable<String, Long> lastDrop = new Hashtable<String, Long>();

	public boolean addRobotInfo(RobotInfo robot) {
		boolean isDrop = false;
		if(targets.containsKey(robot.getName())) {
			isDrop = lastDrop.get(robot.getName()).longValue() <= robot.getTime() && robot.getEnergy() < targets.get(robot.getName()).getEnergy();
			if(isDrop) {
				lastDrop.put(robot.getName(), new Long(robot.getTime() + 5));
			}
		} else {
			lastDrop.put(robot.getName(), new Long(0));
		}
		targets.put(robot.getName(), robot);
		return isDrop;
	}

	public void onRobotDeath(String robotName) {
		targets.remove(robotName);
		lastDrop.remove(robotName);
	}
}
