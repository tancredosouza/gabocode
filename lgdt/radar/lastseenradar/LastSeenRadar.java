package lgdt.radar.lastseenradar;

import robocode.ScannedRobotEvent;
import robocode.AdvancedRobot;
import robocode.util.Utils;

import java.util.Enumeration;
import java.util.Hashtable;

import lgdt.util.BattleField;
import lgdt.util.PT;
import lgdt.util.RobotInfo;
import lgdt.util.SubSystem;

public class LastSeenRadar extends SubSystem {
	private BattleField battleField;
	private AdvancedRobot robot = null;
	String wantedTarget = "";
	int state = 0;

	public void setBattleField(BattleField battleField) {
		this.battleField = battleField;
	}

	public void onScannedRobot(ScannedRobotEvent event) {
		if(event.getName().equals(wantedTarget)) {
			state = 0;
		}
	}

	public void init(AdvancedRobot robot) { this.robot = robot; }

	public void run() {
		if(battleField.size() < robot.getOthers()) {
			robot.setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
		} else if(state == 0) {
			long last = robot.getTime() + 1;
			RobotInfo target = null;
			Enumeration<RobotInfo> it = battleField.elements();
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
			wantedTarget = target.getName();
			double angle = target.getPosition().subtract(new PT(robot.getX(), robot.getY())).angle();
			angle = Utils.normalRelativeAngle(Math.PI / 2 - angle - robot.getRadarHeadingRadians());
			robot.setTurnRadarRightRadians(angle * 20000000);
			state = 1;
		}
	}
}
