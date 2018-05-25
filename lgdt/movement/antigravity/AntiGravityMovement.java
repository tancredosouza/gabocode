package lgdt.movement.antigravity;

import robocode.AdvancedRobot;
import robocode.RobotDeathEvent;
import robocode.util.Utils;

import lgdt.util.PT;
import lgdt.util.SubSystem;
import lgdt.util.RobotInfo;
import lgdt.movement.antigravity.ForceField;
import lgdt.movement.antigravity.GravityPoint;
import lgdt.movement.antigravity.BulletField;
import lgdt.energydrop.SimpleEnergyDropScanner;
import lgdt.gun.headon.HeadOnGun;
import lgdt.gun.lineartarget.SimpleLinearTarget;
import lgdt.gun.VirtualGun;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Random;

public class AntiGravityMovement extends SubSystem {
	static double WALL_MASS = 500000;
	static double WALL_DECAY_POWER = 3.2;
	static double MAX_CENTER_MASS = 100, CENTER_CHANGE_FREQ = 30;
	static double ENEMY_MASS = 200;
	static double BULLET_MASS = 100;
	static double FORCE_OUT_MASS = 1000, FORCE_OUT_FREQ = 50;

    PT velocity = new PT(0, 0);
	Hashtable<String, ForceField> fields = new Hashtable<String, ForceField>();
	SimpleEnergyDropScanner dropScanner = new SimpleEnergyDropScanner();
	AdvancedRobot robot = null;
	VirtualGun gun = new HeadOnGun();
	VirtualGun gun2 = new SimpleLinearTarget();
	int bulletCount = 0;
	Random rand = new Random();


	public void put(String name, ForceField point) {
		fields.put(name, point);
	}

	public void remove(String name) {
		fields.remove(name);
	}

	public void addRobotInfo(RobotInfo robot) {
		if(dropScanner.addRobotInfo(robot) && this.robot != null) {
			this.robot.out.println("Creating bullet number " + bulletCount);
			fields.put("Bullet#" + (bulletCount++), new BulletField(gun.getBullet(robot, new RobotInfo(this.robot), 2.7), BULLET_MASS, 2.5));
			fields.put("Bullet#" + (bulletCount++), new BulletField(gun2.getBullet(robot, new RobotInfo(this.robot), 2.7), BULLET_MASS, 2.5));
		}
		fields.put(robot.getName(), new GravityPoint(robot.getPosition(), ENEMY_MASS * (50 + robot.getEnergy()) / 150));
	}

	public void onRobotDeath(RobotDeathEvent e) {
		fields.remove(e.getName());
	}

	public void init(AdvancedRobot robot) {
		this.robot = robot;
	}

	public void run() {
		addWalls(robot);
		addCenter(robot);
        PT F = getForce(new RobotInfo(robot));
        velocity = velocity.add(F);
        velocity = velocity.normalize();
        velocity = F;
		if(velocity.x == 0 && velocity.y == 0) {
			return;
		}
		robot.out.println(velocity);
		double angle = velocity.angle(new PT(0, 1)) - robot.getHeadingRadians();
		if(Math.abs(angle) < Math.PI / 2) {
			robot.setTurnRightRadians(Utils.normalRelativeAngle(angle));
			robot.setAhead(Double.POSITIVE_INFINITY);
		} else {
			robot.setTurnRightRadians(Utils.normalRelativeAngle(angle + Math.PI));
			robot.setAhead(Double.NEGATIVE_INFINITY);
		}
		cleanUp();
	}

    public PT getForce(RobotInfo robot) {
        PT net_force = new PT(0, 0);
        Enumeration<ForceField> points_e = fields.elements();
        while (points_e.hasMoreElements()) {
            ForceField point = (ForceField) points_e.nextElement();
            net_force = net_force.add(point.getForce(robot));
        }
        return net_force;
    }

    private void cleanUp() {
    	Vector<String> vec = new Vector<String>();
    	Enumeration<String> it = fields.keys();
    	while (it.hasMoreElements()) {
    		String name = (String) it.nextElement();
    		if(fields.get(name).canDestroy()) {
    			vec.add(name);
    		}
    	}
    	for(String str : vec) {
    		fields.remove(str);
    	}
    }

    private void addCenter(AdvancedRobot robot) {
    	if (robot.getTime() % CENTER_CHANGE_FREQ == 0) {
			PT center = new PT(robot.getBattleFieldWidth() / 2.0, 
							   robot.getBattleFieldHeight() / 2.0);
			double mass = (Math.random() * 2 - (1 / robot.getOthers())) * MAX_CENTER_MASS;
			robot.out.println(mass);
			fields.put("Center", new GravityPoint(center, mass));
		}
		if (robot.getTime() % FORCE_OUT_FREQ == 0) {
			PT center = new PT(robot.getX() + rand.nextGaussian() * 30,
							   robot.getY() + rand.nextGaussian() * 30);
			fields.put("FORCEOUT", new GravityPoint(center, FORCE_OUT_MASS * rand.nextDouble()));
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
