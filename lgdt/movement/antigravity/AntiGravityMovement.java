package lgdt.movement.antigravity;

import robocode.AdvancedRobot;
import robocode.util.Utils;

import lgdt.util.PT;
import lgdt.util.SubSystem;
import lgdt.util.RobotInfo;
import lgdt.movement.antigravity.ForceField;
import lgdt.movement.antigravity.GravityPoint;

import java.util.Hashtable;
import java.util.Enumeration;

public class AntiGravityMovement implements SubSystem {
	static double WALL_MASS = 20000;
	static int WALL_DECAY_POWER = 3;
	static double MAX_CENTER_MASS = 1000, CENTER_CHANGE_FREQ = 5;
	static double ENEMY_MASS = 10000;

	Hashtable<String, ForceField> fields = new Hashtable<String, ForceField>();

	public void put(String name, ForceField point) {
		fields.put(name, point);
	}

	public void remove(String name) {
		fields.remove(name);
	}

	public void addRobotInfo(RobotInfo robot) {
		fields.put(robot.getName(), new GravityPoint(robot.getPosition(), ENEMY_MASS * robot.getEnergy() / 100));
	}

	public void onRobotDeath(String robotName) {
		fields.remove(robotName);
	}

	public void run(AdvancedRobot robot) {
		addWalls(robot);
		addCenter(robot);
		PT dir = getForce(new  PT(robot.getX(), robot.getY()));
		if(dir.x == 0 && dir.y == 0) {
			return;
		}
		double angle = dir.angle(new PT(0, 1)) - robot.getHeadingRadians();
		//double size = dir.length();
		//robot.out.format("move: dirX: %f dirY: %f len: %f%n", dir.x, dir.y, size);
		if(Math.abs(angle) < Math.PI / 2) {
			robot.setTurnRightRadians(Utils.normalRelativeAngle(angle));
			robot.setAhead(Double.POSITIVE_INFINITY);
		} else {
			robot.setTurnRightRadians(Utils.normalRelativeAngle(angle + Math.PI));
			robot.setAhead(Double.NEGATIVE_INFINITY);
		}
	}

    public PT getForce(PT cur_position) {
        PT net_force = new PT(0, 0);
        Enumeration<ForceField> points_e = fields.elements();  
        while (points_e.hasMoreElements()) {
            ForceField point = (ForceField) points_e.nextElement();
            net_force = net_force.add(point.getForce(cur_position));
        }
        return net_force;
    }

    private void addCenter(AdvancedRobot robot) {
    	if (robot.getTime() % CENTER_CHANGE_FREQ == 0) {
			PT center = new PT(robot.getBattleFieldWidth() / 2.0, 
							   robot.getBattleFieldHeight() / 2.0);
			double mass = (Math.random() * 2 - 1) * MAX_CENTER_MASS;
			fields.put("Center", new GravityPoint(center, mass));
		}
    }

    private void addWalls(AdvancedRobot robot) {
    	PT wall1 = new PT(robot.getBattleFieldWidth(), robot.getY()),
		wall2 = new PT(0, robot.getY()),
		wall3 = new PT(robot.getX(), robot.getBattleFieldHeight()),
		wall4 = new PT(robot.getX(), 0);

		fields.put("Wall1", new GravityPoint(wall1, WALL_MASS, WALL_DECAY_POWER));
		fields.put("Wall2", new GravityPoint(wall2, WALL_MASS, WALL_DECAY_POWER));
		fields.put("Wall3", new GravityPoint(wall3, WALL_MASS, WALL_DECAY_POWER));
		fields.put("Wall4", new GravityPoint(wall4, WALL_MASS, WALL_DECAY_POWER));
    }
}