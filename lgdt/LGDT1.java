package lgdt;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.RobotDeathEvent;

import java.awt.*;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;

import lgdt.util.*;

public class LGDT1 extends AdvancedRobot {
	private Hashtable<String, PT> points;

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

	public void onScannedRobot(ScannedRobotEvent e) {
		double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
		double enemyX = getX() + e.getDistance() * Math.sin(absoluteBearing);
		double enemyY = getY() + e.getDistance() * Math.cos(absoluteBearing);
		out.println("Enemy \"" + e.getName() + "\" found at (" + enemyX + ", " + enemyY + ")");
		out.println("My position is " + getX() + ", " + getY());
		points.put(e.getName(), new PT(enemyX, enemyY));
	}

	private void runScan() {
		setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
	}

	private void runMovement() {
		if(-1e-4 < getTurnRemaining() && getTurnRemaining() < 1e-4) {
			double set = 180 * (Math.random() % 2 * 2 - 1);
			out.println("setting with " + set);
			setTurnRight(set);
		} else {
			out.println("still needs " + getTurnRemaining());
		}
		setAhead(100);
	}

	private int state;
	String target;

	private void runTarget() {
		if(state == 0) {
			// choosing target
			if(points.isEmpty()) {
				out.println("No elements");
				return;
			}
			double lastDist = 91238129;
			PT cur = new PT(getX(), getY());
			Enumeration it = points.keys();
			while(it.hasMoreElements()) {
				String candidate = (String) it.nextElement();
				PT nxt = (PT) points.get(candidate);
				if(lastDist > cur.distance(nxt)) {
					target = candidate;
					lastDist = cur.distance(nxt);
				}
			}
			state = 1;
			out.println("Got target! " + target + " will be hunted!");
		} else if(state == 1) {
			// aiming
			PT cur = new PT(getX(), getY());
			double deltaHeading = (points.get(target).subtract(cur)).angle((new PT(0, 1)).rotate(-getGunHeadingRadians()));
			PT hmm = (new PT(0, 1)).rotate(-getGunHeadingRadians());
			out.println("gun heading is " + getGunHeadingRadians());
			out.println(hmm.x + ", " + hmm.y);
			double eps = 1e-3;
			out.println("deltaHeading is " + deltaHeading);
			if(-eps < deltaHeading && deltaHeading < eps) {
				fire(3);
				state = 0;
			} else {
				setTurnGunRightRadians(deltaHeading);
				//state = 0;
			}
		}
	}

	public void onRobotDeath(RobotDeathEvent e) {
		points.remove(e.getName());
		state = 0;
	}

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
		points = new Hashtable<String, PT>();
		state = 0;
	}
}