package lgdt;

import robocode.TeamRobot;
import robocode.ScannedRobotEvent;
import robocode.RobotDeathEvent;
import robocode.HitRobotEvent;
import robocode.MessageEvent;
import robocode.SkippedTurnEvent;

import lgdt.util.BattleField;
import lgdt.util.RobotInfo;
import lgdt.util.SubSystem;
import lgdt.util.EnergyDropEvent;
import lgdt.energydrop.SimpleEnergyDropScanner;
import lgdt.radar.lastseenradar.LastSeenRadar;
import lgdt.movement.minimumrisk.MinimumRiskTFG1v1;
import lgdt.gun.lineartarget.IterativeLinearTarget;
import lgdt.gun.circulartarget.IterativeCircularTarget;
import lgdt.gun.headon.HeadOnGun;
import lgdt.gun.multigun.Multigun;
import lgdt.gun.VirtualBullet;

import java.awt.*;
import java.io.IOException;

public class MinimumRiskAndMultiGun extends TeamRobot {
	// Energy drop
	SimpleEnergyDropScanner energyDrop = new SimpleEnergyDropScanner();

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
			try {
				broadcastMessage(new RobotInfo(this));
			} catch(IOException ignored) {

			}
			radar.run();
			movement.run();
			gun.run();
			VirtualBullet bullet = gun.shotBullet;
			if(bullet != null) {
				try {
					broadcastMessage(bullet);
				} catch(IOException ignored) {

				}
			}
			execute();
		}
	}

	// Events

	public void onSkippedTurn(SkippedTurnEvent e) {
		out.println("Skipped turn " + e.getSkippedTurn());
	}

	public void onMessageReceived(MessageEvent e) {
		if(e.getMessage() instanceof RobotInfo) {
			RobotInfo info = (RobotInfo) e.getMessage();
			if(!info.getName().equals(getName())) {
				//out.println("Got message of " + info.getName());
				addRobotInfo(info);
				
			}
		} else if(e.getMessage() instanceof VirtualBullet) {
			VirtualBullet bullet = (VirtualBullet) e.getMessage();
			movement.addConfirmedBullet(bullet);
		}
	}

	public void addRobotInfo(RobotInfo info) {
		battleField.addRobotInfo(info);
		if(info.isEnemy()) {
			EnergyDropEvent e = energyDrop.addRobotInfo(info);
			if(e != null) {
				out.println("Found drop from " + e.getInfo().getName());
				movement.onEnergyDrop(e);
			}
		}
		radar.addRobotInfo(info);
		gun.addRobotInfo(info);
		movement.addRobotInfo(info);
	}

	public void onHitRobot(HitRobotEvent e) {
		movement.onHitRobot(e);
	}

	public void onPaint(Graphics2D g) {
		movement.onPaint(g);
		gun.onPaint(g);
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		RobotInfo r = new RobotInfo(this, e, !isTeammate(e.getName()));
		addRobotInfo(r);
		try {
			broadcastMessage(r);
		} catch(IOException ignored) {

		}
	}

	public void onRobotDeath(RobotDeathEvent e) {
		energyDrop.onRobotDeath(e);
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
		setGunColor(Color.black);
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