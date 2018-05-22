package lgdt;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.RobotDeathEvent;

import java.awt.*;

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

	public void run() {
		init();
		
		// main loop
		while(true) {
			runScan();
			runMovement();
			runTarget();
			execute();
		}
	}

	// Events

	public void onScannedRobot(ScannedRobotEvent e) {
		RobotInfo r = new RobotInfo(this, e, true);
		radar.addRobotInfo(r);
		gun.addRobotInfo(r);
		movement.addRobotInfo(r);
	}

	public void onRobotDeath(RobotDeathEvent e) {
		radar.onRobotDeath(e.getName());
		gun.onRobotDeath(e.getName());
		movement.onRobotDeath(e.getName());
	}

	private void runScan() { radar.run(this); }
	private void runMovement() { movement.run(this); }
	private void runTarget() { gun.run(this); }

	// initialization
	private void init() {
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
	}
}