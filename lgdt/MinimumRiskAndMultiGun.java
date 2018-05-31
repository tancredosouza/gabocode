package lgdt;

import robocode.TeamRobot;
import robocode.ScannedRobotEvent;
import robocode.RobotDeathEvent;
import robocode.HitRobotEvent;

import java.awt.*;

import lgdt.util.BattleField;
import lgdt.util.RobotInfo;
import lgdt.util.SubSystem;
import lgdt.radar.lastseenradar.LastSeenRadar;
import lgdt.movement.minimumrisk.MinimumRiskTFG1v1;
import lgdt.gun.lineartarget.IterativeLinearTarget;
import lgdt.gun.circulartarget.IterativeCircularTarget;
import lgdt.gun.headon.HeadOnGun;
import lgdt.gun.multigun.Multigun;

public class MinimumRiskAndMultiGun extends TeamRobot {
	// Scan
	SubSystem radar = new LastSeenRadar();

	// Movement
	MinimumRiskTFG1v1 movement = new MinimumRiskTFG1v1();
	
	// Target
	Multigun gun = new Multigun();

	// BattleField
	BattleField battleField = null;

	public void run() {
		init();
		
		// main loop
		while(true) {
			radar.run();
			movement.run();
			gun.run();
			execute();
		}
	}

	// Events

	public void onHitRobot(HitRobotEvent e) {
		movement.onHitRobot(e);
	}

	public void onPaint(Graphics2D g) {
		// movement.setGraphic(g);
		gun.onPaint(g);
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		RobotInfo r = new RobotInfo(this, e, !isTeammate(e.getName()));
		battleField.addRobotInfo(r);
		radar.onScannedRobot(e);
		gun.onScannedRobot(e);
		movement.onScannedRobot(e);
	}

	public void onRobotDeath(RobotDeathEvent e) {
		battleField.onRobotDeath(e);
		radar.onRobotDeath(e);
		gun.onRobotDeath(e);
		movement.onRobotDeath(e);
	}

	// initialization
	private void init() {
		battleField = new BattleField(getBattleFieldWidth(), getBattleFieldHeight());
		// independent movement
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		// Set colors
		setBodyColor(Color.red);
		setGunColor(Color.red);
		setRadarColor(Color.red);
		setScanColor(Color.red);
		// init modules
		radar.init(this);
		movement.init(this);
		gun.init(this);
		radar.setBattleField(battleField);
		movement.setBattleField(battleField);
		gun.setBattleField(battleField);
	}
}