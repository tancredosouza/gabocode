package lgdt;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.RobotDeathEvent;

import java.awt.*;
import java.util.Hashtable;
import java.util.Enumeration;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MoveAction;

import lgdt.util.PT;
import lgdt.util.RobotInfo;
import lgdt.movement.antigravity.*;

public class LGDT1v1 extends AdvancedRobot {
	private Hashtable<String, PT> points;
	AntiGravityMovement movement;
	int tickCount;
	final double WALL_MASS = 20000;
	final double WALL_DECAY_POWER = 3;
	final double MAX_CENTER_MASS = 1000, CENTER_CHANGE_FREQ = 5;
	final double ENEMY_MASS = 10000;

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
		points.put(e.getName(), new PT(enemyX, enemyY));
		movement.put(e.getName(), new GravityPoint(new PT(enemyX, enemyY), ENEMY_MASS));
	}

	private void runScan() {
		setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
	}

	private void addWalls() {
		PT wall1 = new PT(getBattleFieldWidth(), getY()),
		wall2 = new PT(0, getY()),
		wall3 = new PT(getX(), getBattleFieldHeight()),
		wall4 = new PT(getX(), 0);

		movement.put("Wall1", new GravityPoint(wall1, WALL_MASS, WALL_DECAY_POWER));
		movement.put("Wall2", new GravityPoint(wall2, WALL_MASS, WALL_DECAY_POWER));
		movement.put("Wall3", new GravityPoint(wall3, WALL_MASS, WALL_DECAY_POWER));
		movement.put("Wall4", new GravityPoint(wall4, WALL_MASS, WALL_DECAY_POWER));
	}

	private void addCenter() {
		if (getTime() % CENTER_CHANGE_FREQ == 0) {
			PT center = new PT(getBattleFieldWidth() / 2.0, 
							   getBattleFieldHeight() / 2.0);
			double mass = (Math.random() * 2 - 1) * MAX_CENTER_MASS;
			movement.put("Center", new GravityPoint(center, mass));
		}
	}

	private void runMovement() {
		addWalls();
		addCenter();
		PT dir = movement.getForce(new RobotInfo(this));
		if(dir.x == 0 && dir.y == 0) {
			return;
		}
		double angle = dir.angle(new PT(0, 1)) - getHeadingRadians();
		double size = dir.length();
		out.format("move: dirX: %f dirY: %f len: %f%n", dir.x, dir.y, size);
		if(Math.abs(angle) < Math.PI / 2) {
			setTurnRightRadians(angle);
			setAhead(Double.POSITIVE_INFINITY);
		} else {
			setTurnRightRadians(angle - Math.PI / 2);
			setAhead(Double.NEGATIVE_INFINITY);
		}
	}

	private int state;
	String target;

	private void runTarget() {
		if(state == 0) {
			// choosing target
			if(points.isEmpty()) {
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
		} else if(state == 1) {
			// aiming
			PT cur = new PT(getX(), getY());
			double deltaHeading = (points.get(target).subtract(cur)).angle((new PT(0, 1)).rotate(-getGunHeadingRadians()));
			double eps = 1e-3;
			if(-eps < deltaHeading && deltaHeading < eps) {
				fire(3);
				state = 0;
			} else {
				setTurnGunRightRadians(deltaHeading);
			}
		}
	}

	public void onRobotDeath(RobotDeathEvent e) {
		points.remove(e.getName());
		movement.remove(e.getName());
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
		movement = new AntiGravityMovement();
	}
}