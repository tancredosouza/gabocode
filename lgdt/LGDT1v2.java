package lgdt;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.RobotDeathEvent;

import java.awt.*;

import lgdt.util.RobotInfo;
import lgdt.movement.antigravity.AntiGravityMovement;
import lgdt.gun.headon.HeadOnGun;

public class LGDT1v2 extends AdvancedRobot {
	// Movement
	AntiGravityMovement movement;
	
	// Target
	HeadOnGun gun;

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
		gun.addRobotInfo(r);
		movement.addRobotInfo(r);
	}

	public void onRobotDeath(RobotDeathEvent e) {
		gun.onRobotDeath(e.getName());
		movement.onRobotDeath(e.getName());
	}

	// Scan

	private void runScan() {
		setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
	}

	// Movement

	private void runMovement() {
		movement.run(this);
	}

	// Target
	private void runTarget() {
		gun.run(this);
	}

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
		// initializing components
		// scan
		// movement
		movement = new AntiGravityMovement();
		// target
		gun = new HeadOnGun();
	}
}