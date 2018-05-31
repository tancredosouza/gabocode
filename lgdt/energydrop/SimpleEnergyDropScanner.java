package lgdt.energydrop;

import robocode.RobotDeathEvent;

import lgdt.util.RobotInfo;
import lgdt.util.EnergyDropEvent;

import java.lang.Long;
import java.util.HashMap;

public class SimpleEnergyDropScanner {
	private HashMap<String, RobotInfo> targets = new HashMap<String, RobotInfo>();
	private HashMap<String, Long> lastDrop = new HashMap<String, Long>();

	public EnergyDropEvent addRobotInfo(RobotInfo robot) {
		if(targets.containsKey(robot.getName())) {
			double drop = targets.get(robot.getName()).getEnergy() - robot.getEnergy();
			boolean isDrop = lastDrop.get(robot.getName()).longValue() <= robot.getTime() && drop > 0 && drop <= 3;
			if(isDrop) {
				drop = Math.min(drop, 3);
				lastDrop.put(robot.getName(), new Long(robot.getTime() + (long)(10 + drop / 2)));
				targets.put(robot.getName(), robot);
				return new EnergyDropEvent(robot, drop, 1);
			} else {
				targets.put(robot.getName(), robot);
				return null;
			}
		} else {
			lastDrop.put(robot.getName(), new Long(0));
			targets.put(robot.getName(), robot);
			return null;
		}
	}

	public void onRobotDeath(RobotDeathEvent e) {
		targets.remove(e.getName());
		lastDrop.remove(e.getName());
	}
}
