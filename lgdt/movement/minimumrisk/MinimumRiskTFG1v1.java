package lgdt.movement.minimumrisk;

import robocode.AdvancedRobot;
import robocode.HitRobotEvent;
import robocode.util.Utils;

import lgdt.util.RobotInfo;
import lgdt.util.BattleField;
import lgdt.util.PT;
import lgdt.movement.minimumrisk.MinimumRiskBase;
import lgdt.gun.headon.HeadOnGun;
import lgdt.gun.VirtualBullet;
import lgdt.gun.VirtualBulletManager;
import lgdt.util.EnergyDropEvent;

import java.util.Iterator;
import java.util.HashMap;
import java.awt.*;

public class MinimumRiskTFG1v1 extends MinimumRiskBase {
	static double scale = 1e3;
	AdvancedRobot robot;
	BattleField battleField = null;
	PT targetPosition = new PT(0, 0);
	Graphics2D graph = null;
	VirtualBulletManager bulletManager = new VirtualBulletManager();
	VirtualBulletManager confirmedManager = new VirtualBulletManager();
	HeadOnGun gun = new HeadOnGun();
	long last = -1;
	int lastPositionSize = 10;
	int lastPositionPointer = 0;
	int others = 0;
	double minRange = 50, range = 100;
	PT[] lastPositions;

	public void setGraphic(Graphics2D graph) {
		onPaint(graph);
	}

	public void onPaint(Graphics2D graph) {
		bulletManager.onPaint(graph);
		confirmedManager.onPaint(graph);
		this.graph = graph;
		graph.setColor(Color.RED);
		graph.fillRect((int)targetPosition.x, (int)targetPosition.y, 30, 30);
		graph.setColor(Color.BLACK);
		for(int i = 0; i < lastPositionSize; i++) {
			graph.fillRect((int)lastPositions[i].x, (int)lastPositions[i].y, 30, 30);
		}
	}

	public void setBattleField(BattleField battleField) {
		this.battleField = battleField;
		bulletManager.setBattleField(battleField);
		confirmedManager.setBattleField(battleField);
	}

	public void init(AdvancedRobot robot) {
		lastPositions = new PT[lastPositionSize];
		for(int i = 0; i < lastPositionSize; i++) {
			lastPositions[i] = new PT(-1000, -1000);
		}
		bulletManager.init(robot);
		confirmedManager.init(robot);
		this.robot = robot;
		targetPosition = new PT(robot.getX(), robot.getY());
	}

	public void addConfirmedBullet(VirtualBullet bullet) {
		confirmedManager.addBullet(bullet, null, null, 0, Color.CYAN);
	}

	public void onHitRobot(HitRobotEvent event) {
		robot.out.println("Got hit!");
		if(robot.getTime() - last < 5) {
			return;
		}
		last = -1;
		targetPosition = new PT(robot.getX(), robot.getY());
		range = 10;
		minRange = 20;
	}

	public void onEnergyDrop(EnergyDropEvent event) {
		VirtualBullet bullet = gun.getBullet(event.getInfo(), new RobotInfo(robot), event.getDrop());
		robot.out.println("Creating bullet from " + event.getInfo().getName() + ", origin " + bullet.origin + " and velocity " + bullet.velocity);
		double dist = bullet.origin.distance(new PT(robot.getX(), robot.getY()));
		double minDist = dist;
		Iterator<RobotInfo> it = battleField.values();
		while(it.hasNext()) {
			RobotInfo en = (RobotInfo) it.next();
			if(!en.isEnemy()) {
				minDist = Math.min(minDist, bullet.origin.distance(en.getPosition()));
			}
		}
		double weight = Math.pow(0.5, (dist - minDist) / 100);
		bulletManager.addBullet(bullet, null, null, 0, Color.YELLOW, weight);
	}

	public void run() {
		others = robot.getOthers();
		bulletManager.run();
		PT position = new PT(robot.getX(), robot.getY());
		//robot.out.println(Math.min(getRisk(position), getRisk(targetPosition)));
		if(last == -1 || targetPosition.distance(position) < 10 || (Math.min(getRisk(position), getRisk(targetPosition)) > 0.001 && robot.getTime() - last >= 5)) {
			double curCost = 1e9;
			BattleField bf = new BattleField(robot.getBattleFieldWidth(), robot.getBattleFieldHeight());
			PT nextPosition = targetPosition;
			for(int i = 0; i < 300; i++) {
				double angle = 2 * Math.PI * Math.random();
				double distance = minRange + range * Math.random();
				PT testPosition = position.add(new PT(Math.cos(angle) * distance, Math.sin(angle) * distance));
				if(bf.contains(testPosition, 50)) {
					double cost = getRisk(testPosition);
					if(graph != null) {
						float seeCost = (float) (cost * scale);
						if(seeCost > 1) {
							scale *= 0.999;
						} else if(seeCost < 0.1) {
							scale *= 1.001;
						}
						seeCost = 1 - Math.min(1, Math.max(seeCost, 0));
						graph.setColor(new Color(seeCost, seeCost, seeCost));
						graph.fillRect((int)testPosition.x, (int)testPosition.y, 10, 10);
					}
					if(cost < curCost) {
						nextPosition = testPosition;
						curCost = cost;
					}
				}
			}
			targetPosition = nextPosition;
			lastPositions[lastPositionPointer] = position;
			lastPositionPointer = (lastPositionPointer + 1) % lastPositionSize;
			last = robot.getTime();
			range = 70;
			minRange = 50;
		} else {
			double angle = targetPosition.subtract(position).angle() - (Math.PI / 2 - robot.getHeadingRadians());
			double direction = 1;
			if(Math.cos(angle) < 0) {
				angle += Math.PI;
				direction = -1;
			}
			angle = Utils.normalRelativeAngle(angle);
			robot.setAhead(position.distance(targetPosition) * direction);
			robot.setTurnLeftRadians(angle);
			robot.setMaxVelocity(Math.abs(angle) > 1 ? 0 : 100);
		}
		graph = null;
	}

	public double getRisk(PT position) {
		Iterator<RobotInfo> it = battleField.values();
		double dist = targetPosition.distance(position);
		dist = Math.max(dist, 1);
		double eval = 0.08 / Math.pow(dist, 2);
		double myEnergy = robot.getEnergy();
		double minDist = 1e9;
		double bestApproach = 0;
		while(it.hasNext()) {
			RobotInfo en = (RobotInfo) it.next();
			if(en.isEnemy()) {
				dist = en.getPosition().distance(position);
				dist = Math.max(dist, 0.00001);
				minDist = Math.min(minDist, dist);
				double projection = Math.cos(position.subtract(targetPosition).angle(en.getPosition().subtract(targetPosition)));
				eval += Math.min(en.getEnergy() / myEnergy, 2) * (1 + Math.abs(projection)) / Math.pow(dist, 2);
				bestApproach = Math.max(bestApproach, Math.min(myEnergy / (en.getEnergy() + 20), 2) * 0.0001 * Math.abs(projection) / Math.pow(dist, 0.8 + 0.3 * robot.getOthers()));
			} else {
				dist = en.getPosition().distance(position);
				dist = Math.max(dist, 0.00001);
				minDist = Math.min(minDist, dist);
				eval += 2 / Math.pow(dist, 2);
			}
		}
		double ratio = 0.01;
		for(int i = 0; i < lastPositionSize; i++) {
			dist = position.distance(lastPositions[(-i + (lastPositionPointer - 1) % lastPositionSize + 3 * lastPositionSize) % lastPositionSize]);
			dist = Math.max(dist, 50);
			eval += 1 / Math.pow(dist, 2) * Math.pow(0.9, others) * ratio;
			ratio *= 0.8;
		}
		double limit = Math.min(300 + 50 * others, 700);
		//robot.out.println(limit);
		if(minDist < 200) {
			eval += 4 / Math.pow(minDist, 1.5);
		} else if(minDist < limit) {
			eval += 0.5 / Math.pow(minDist, 1.5);
		} else {
			//robot.out.println("got");
			eval += 0.5 / Math.pow(minDist, 1.5);
			eval *= Math.pow(1.1, (minDist - limit) / 50);
			//eval += 2 / Math.pow(minDist, 0.1) - 2 / Math.pow(limit, 0.1);
		}
		//eval -= bestApproach;
		eval += bulletManager.getDanger(position) * 2 + confirmedManager.getDanger(position) * 2;
		return eval;
	}
}