package lgdt.util;

import robocode.AdvancedRobot;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import robocode.BulletHitEvent;
import robocode.HitRobotEvent;
import robocode.HitByBulletEvent;

import lgdt.util.RobotInfo;

import java.util.Enumeration;
import java.util.Hashtable;

public class BattleField {
	private Hashtable<String, RobotInfo> table = new Hashtable<String, RobotInfo>();
	private double battleFieldWidth, battleFieldHeight;

	public BattleField(double battleFieldWidth, double battleFieldHeight) {
		this.battleFieldHeight = battleFieldHeight;
		this.battleFieldWidth = battleFieldWidth;
	}

	public boolean contains(PT position, double border) {
		return border < position.x && position.x < battleFieldWidth - border &&
			   border < position.y && position.y < battleFieldHeight - border;
	}

	public Enumeration<RobotInfo> elements() { return table.elements(); }

	public RobotInfo get(String name) { return table.get(name); }

	public void addRobotInfo(RobotInfo robot) {
		if(!table.containsKey(robot.getName())) {
			table.put(robot.getName(), robot);
		} else {
			RobotInfo info = table.get(robot.getName());
			info.merge(robot);
			table.put(robot.getName(), info);
		}
	}

	public double getBattleFieldWidth() { return battleFieldWidth; }
	public double getBattleFieldHeight() { return battleFieldHeight; }
	public void onRobotDeath(RobotDeathEvent event) { table.remove(event.getName()); }
	public void onScannedRobot(ScannedRobotEvent event) { /*TODO*/ }
	public void onBulletHit(BulletHitEvent event) { /*TODO*/ }
	public void onHitRobot(HitRobotEvent event) { /*TODO*/ }
	public void onHitByBullet(HitByBulletEvent event) { /*TODO*/ }
	public int size() { return table.size(); }
}