package tfg;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

import java.awt.*;
import java.util.Hashtable;
import java.util.Vector;

public class Second extends AdvancedRobot {
	private Hashtable<String, PT> points;

	public void run() {
		battleInit();
		

		// main loop
		while(true) {
			runScan();
			runMovement();
			runTarget();
		}
	}

	private void onScannedRobot(ScannedRobotEvent e) {
		double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
		double enemyX = getX() + e.getDistance() * Math.sin(absoluteBearing);
		double enemyY = getY() + e.getDistance() * Math.cos(absoluteBearing);
		out.println("Enemy \"" + e.getName() + "\" found at (" + enemyX + ", " + enemyY + ")");
		points.put(e.getName(), PT(enemyX, enemyY));
	}

	private void runScan() {
		setTurnRadarRight(Double.POSITIVE_INFINITY);
	}

	private void runMovement() {

	}

	private void runTarget() {

	}

	private void battleInit() {
		// independent movement
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		// Set colors
		setBodyColor(Color.red);
		setGunColor(Color.red);
		setRadarColor(Color.red);
		setScanColor(Color.red);
	}
}