package lgdt.util;

import robocode.AdvancedRobot;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import robocode.BulletHitEvent;
import robocode.HitRobotEvent;
import robocode.HitByBulletEvent;
import robocode.HitWallEvent;
import robocode.WinEvent;
import robocode.BattleEndedEvent;
import robocode.RoundEndedEvent;
import robocode.SkippedTurnEvent;
import robocode.DeathEvent;

import lgdt.util.BattleField;
import lgdt.util.RobotInfo;

public abstract class SubSystem {
	public void addRobotInfo(RobotInfo robot) {}
	public void onRobotDeath(RobotDeathEvent event) {}
	public void onScannedRobot(ScannedRobotEvent event) {}
	public void onBulletHit(BulletHitEvent event) {}
	public void onHitRobotEvent(HitRobotEvent event) {}
	public void onHitByBullet(HitByBulletEvent event) {}
	public void onHitWall(HitWallEvent event) {}
	public void onWin(WinEvent event) {}
	public void onBattleEnded(BattleEndedEvent event) {}
	public void onRoundEnded(RoundEndedEvent event) {}
	public void onSkippedTurn(SkippedTurnEvent event) {}
	public void onDeath(DeathEvent event) {}
	public void setBattleField(BattleField battleField) {}

	public abstract void init(AdvancedRobot robot);
	public abstract void run();
}