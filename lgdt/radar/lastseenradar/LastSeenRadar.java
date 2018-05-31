package lgdt.radar.lastseenradar;

import robocode.ScannedRobotEvent;
import robocode.AdvancedRobot;
import robocode.util.Utils;

import java.util.Iterator;
import java.util.HashMap;

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

	private void addName(String name) {
		if(name.equals(wantedTarget)) {
			state = 0;
		}
	}

	public void addRobotInfo(RobotInfo scanned) {
		addName(scanned.getName());
	}

	public void onScannedRobot(ScannedRobotEvent event) {
		addName(event.getName());
	}

	public void init(AdvancedRobot robot) { this.robot = robot; }

	public void run() {
		if(battleField.size() > 0 && battleField.get(wantedTarget) == null) {
			state = 0;
		}
		if(battleField.size() < robot.getOthers()) {
			robot.setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
		} else if(state == 0) {
			long last = robot.getTime() + 1;
			RobotInfo target = null;
			Iterator<RobotInfo> it = battleField.values();
			while(it.hasNext()) {
				RobotInfo nxt = (RobotInfo) it.next();
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
			if(Math.abs(angle) < 1e-6) {
				angle = 1;
			}
			robot.setTurnRadarRightRadians(angle * 20000000);
			state = 1;
		}
	}
}
