package lgdt.radar.lastseenradar;

import robocode.AdvancedRobot;
import robocode.util.Utils;

import java.util.Enumeration;
import java.util.Hashtable;

import lgdt.util.PT;
import lgdt.util.RobotInfo;
import lgdt.util.SubSystem;

public class LastSeenRadar implements SubSystem {
	private Hashtable<String, RobotInfo> lastSeen = new Hashtable<String, RobotInfo>();
	int state = 0;

	public void addRobotInfo(RobotInfo robot) {
		lastSeen.put(robot.getName(), robot);
		state = 0;
	}

	public void onRobotDeath(String robotName) {
		lastSeen.remove(robotName);
	}

	public void run(AdvancedRobot robot) {
		if(lastSeen.size() < robot.getOthers()) {
			robot.setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
		} else if(state == 0) {
			long last = robot.getTime() + 1;
			RobotInfo target = null;
			Enumeration<RobotInfo> it = lastSeen.elements();
			while(it.hasMoreElements()) {
				RobotInfo nxt = (RobotInfo) it.nextElement();
				if(last > nxt.getTime()) {
					target = nxt;
					last = nxt.getTime();
				}
			}
			if(target == null) {
				return;
			}
			double angle = target.getPosition().subtract(new PT(robot.getX(), robot.getY())).angle();
			robot.out.println("to robot " + target.getName() + " needs " + angle);
			angle = Utils.normalRelativeAngle(Math.PI / 2 - angle - robot.getRadarHeadingRadians());
			robot.setTurnRadarRightRadians(angle * 20000000);
			state = 1;
		}
	}
}