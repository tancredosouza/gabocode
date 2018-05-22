package lgdt.movement.minimumrisk;

import robocode.AdvancedRobot;
import robocode.util.Utils;

import lgdt.util.RobotInfo;
import lgdt.util.BattleField;
import lgdt.util.PT;
import lgdt.movement.minimumrisk.MinimumRiskBase;

import java.util.Enumeration;
import java.util.Hashtable;
import java.awt.*;

public class MinimumRiskTFG1v1 implements MinimumRiskBase {
	static double scale = 1e3;
	Hashtable<String, RobotInfo> robots = new Hashtable<String, RobotInfo>();
	PT targetPosition = new PT(0, 0);
	Graphics2D graph = null;

	public void setGraphic(Graphics2D graph) {
		this.graph = graph;
	}

	public void addRobotInfo(RobotInfo robot) {
		robots.put(robot.getName(), robot);
	}

	public void onRobotDeath(String robotName) {
		robots.remove(robotName);
	}

	public void init(AdvancedRobot robot) {
		targetPosition = new PT(robot.getX(), robot.getY());
	}

	public void run(AdvancedRobot robot) {
		PT position = new PT(robot.getX(), robot.getY());
		double curCost = getRisk(robot, targetPosition);
		if(targetPosition.distance(position) < 20) {
			BattleField bf = new BattleField(robot.getBattleFieldWidth(), robot.getBattleFieldHeight());
			PT nextPosition = targetPosition;
			for(int i = 0; i < 200; i++) {
				double angle = 2 * Math.PI * Math.random();
				double distance = 100 + 100 * Math.random();
				PT testPosition = position.add(new PT(Math.cos(angle) * distance, Math.sin(angle) * distance));
				if(bf.contains(testPosition, 30)) {
					double cost = getRisk(robot, testPosition);
					if(graph != null) {
						float seeCost = (float) (cost * scale);
						if(seeCost > 1) {
							scale *= 0.999;
						} else if(seeCost < 0.1) {
							scale *= 1.001;
						}
						seeCost = 1 - Math.min(1, Math.max(seeCost, 0));
						robot.out.println("painting with color " + seeCost + " ratio was " + (scale * cost));
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

	public double getRisk(AdvancedRobot robot, PT position) {
		Enumeration<RobotInfo> it = robots.elements();
		double dist = targetPosition.distance(position);
		dist = Math.max(dist, 0.00001);
		double eval = 0.08 / Math.pow(dist, 2);
		double myEnergy = robot.getEnergy();
		double minDist = 1e9;
		double bestApproach = 0;
		while(it.hasMoreElements()) {
			RobotInfo en = (RobotInfo) it.nextElement();
			dist = en.getPosition().distance(position);
			dist = Math.max(dist, 0.00001);
			minDist = Math.min(minDist, dist);
			double projection = Math.cos(position.subtract(targetPosition).angle(en.getPosition().subtract(targetPosition)));
			eval += Math.min(en.getEnergy() / myEnergy, 2) * (1 + Math.abs(projection)) / Math.pow(dist, 2);
			bestApproach = Math.max(bestApproach, Math.min(myEnergy / (en.getEnergy() + 20), 2) * 0.0001 * Math.abs(projection) / Math.pow(dist, 0.8 + 0.3 * robot.getOthers()));
		}
		eval -= bestApproach;
		double limit = 400;
		if(minDist < limit) {
			eval += 2 / Math.pow(minDist, 2);
		} else {
			eval += -2 / Math.pow(minDist, 1.5);
			eval *= Math.pow(limit / minDist, Math.min(3, robot.getOthers()));
		}
		return eval;
	}
}