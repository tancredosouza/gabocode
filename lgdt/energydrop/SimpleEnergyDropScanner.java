package lgdt.energydrop;

import lgdt.util.RobotInfo;

import java.lang.Long;
import java.util.HashMap;

public class SimpleEnergyDropScanner {
	private HashMap<String, RobotInfo> targets = new HashMap<String, RobotInfo>();
	private HashMap<String, Long> lastDrop = new HashMap<String, Long>();

	public boolean addRobotInfo(RobotInfo robot) {
		boolean isDrop = false;
		if(targets.containsKey(robot.getName())) {
			double drop = targets.get(robot.getName()).getEnergy() - robot.getEnergy();
			isDrop = lastDrop.get(robot.getName()).longValue() <= robot.getTime() && drop > 0;
			if(isDrop) {
				drop = Math.min(drop, 3);
				lastDrop.put(robot.getName(), new Long(robot.getTime() + (long)(10 + drop / 2)));
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
