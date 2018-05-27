package lgdt.movement.minimumrisk;

import robocode.AdvancedRobot;
import robocode.HitRobotEvent;
import robocode.util.Utils;

import lgdt.util.RobotInfo;
import lgdt.util.BattleField;
import lgdt.util.PT;
import lgdt.movement.minimumrisk.MinimumRiskBase;

import java.util.Enumeration;
import java.util.Hashtable;
import java.awt.*;

public class MinimumRiskTFG1v1 extends MinimumRiskBase {
	static double scale = 1e3;
	AdvancedRobot robot;
	BattleField battleField = null;
	PT targetPosition = new PT(0, 0);
	Graphics2D graph = null;

	public void setGraphic(Graphics2D graph) {
		this.graph = graph;
	}

	public void setBattleField(BattleField battleField) {
		this.battleField = battleField;
	}

	public void init(AdvancedRobot robot) {
		this.robot = robot;
		targetPosition = new PT(robot.getX(), robot.getY());
	}

	public void onHitRobot(HitRobotEvent event) {
		targetPosition = new PT(robot.getX(), robot.getY());
		run();
	}

	public void run() {
		PT position = new PT(robot.getX(), robot.getY());
		if(targetPosition.distance(position) < 20) {
			double curCost = getRisk(targetPosition);
			BattleField bf = new BattleField(robot.getBattleFieldWidth(), robot.getBattleFieldHeight());
			PT nextPosition = targetPosition;
			for(int i = 0; i < 200; i++) {
				double angle = 2 * Math.PI * Math.random();
				double distance = 50 + 100 * Math.random();
				PT testPosition = position.add(new PT(Math.cos(angle) * distance, Math.sin(angle) * distance));
				if(bf.contains(testPosition, 30)) {
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
		Enumeration<RobotInfo> it = battleField.elements();
		double dist = targetPosition.distance(position);
		dist = Math.max(dist, 0.00001);
		double eval = 0.08 / Math.pow(dist, 2);
		double myEnergy = robot.getEnergy();
		double minDist = 1e9;
		double bestApproach = 0;
		while(it.hasMoreElements()) {
			RobotInfo en = (RobotInfo) it.nextElement();
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
				eval += 2 / Math.pow(dist, 2);
			}
		}
		eval -= bestApproach;
		double limit = 400;
		if(minDist < limit) {
			eval += 2 / Math.pow(minDist, 2);
		} else {
			eval += -2 / Math.pow(minDist, 1.5);
			eval *= Math.pow(limit / minDist, Math.min(4, robot.getOthers()));
		}
		return eval;
	}
}