package lgdt;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.RobotDeathEvent;

import java.awt.*;

import lgdt.util.BattleField;
import lgdt.util.RobotInfo;
import lgdt.util.SubSystem;
import lgdt.radar.lastseenradar.LastSeenRadar;
import lgdt.movement.antigravity.AntiGravityMovement;
import lgdt.gun.lineartarget.IterativeLinearTarget;

public class LGDT1v2LL extends AdvancedRobot {
	// Scan
	SubSystem radar = new LastSeenRadar();

	// Movement
	SubSystem movement = new AntiGravityMovement();
	
	// Target
	SubSystem gun = new IterativeLinearTarget();

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
	public void onScannedRobot(ScannedRobotEvent e) {
		RobotInfo r = new RobotInfo(this, e, true);
		battleField.addRobotInfo(r);
		radar.onScannedRobot(e);
		gun.onScannedRobot(e);
		movement.addRobotInfo(r);
	}

	public void onRobotDeath(RobotDeathEvent e) {
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