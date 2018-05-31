package lgdt.gun.multigun;

import robocode.AdvancedRobot;
import robocode.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.awt.*;

import lgdt.gun.VirtualGun;
import lgdt.gun.VirtualBullet;
import lgdt.gun.VirtualBulletHitEvent;
import lgdt.gun.VirtualBulletManager;
import lgdt.util.RobotInfo;
import lgdt.util.PT;
import lgdt.util.BattleField;
import lgdt.util.SubSystem;

// guns
import lgdt.gun.circulartarget.IterativeCircularTarget;
import lgdt.gun.headon.HeadOnGun;
import lgdt.gun.lineartarget.IterativeLinearTarget;
import lgdt.gun.lineartarget.SimpleLinearTarget;

public class Multigun extends SubSystem {
    class GunInfo {
        class Stats {
            private double mean, squareMean;
            private int numberOfElements;

            Stats() {
                this.mean = 0.0;
                this.squareMean = 0.0;
                this.numberOfElements = 0;
            }

            public void addValue(double value) {
                mean *= numberOfElements;
                squareMean *= numberOfElements;
                mean += value;
                squareMean += value * value;
                numberOfElements++;
                mean /= numberOfElements;
                squareMean /= numberOfElements;
            }

            public double getMean() {
                return mean;
            }

            public double getVariance() {
                return squareMean - mean * mean;
            }
        }

        public VirtualGun gun;
        public Color bulletCollor;
        private HashMap<String, Stats> error_table;

        GunInfo(VirtualGun gun, Color bulletCollor) {
            this.gun = gun;
            this.bulletCollor = bulletCollor;
            error_table = new HashMap<String, Stats>();
        }

        public double getMeanError(String targetName) {
            Stats s = error_table.get(targetName);
            if (s == null) return 0;
            else return s.getMean();
        }

        public double getVarianceError(String targetName) {
            Stats s = error_table.get(targetName);
            if (s == null) return 0;
            else return s.getVariance();
        }

        public void addError(String targetName, double error) {
            Stats s = error_table.get(targetName);
            if (s == null) {
                s = new Stats();
            }
            s.addValue(error);
            error_table.put(targetName, s);
        }
    }

    enum State {
        TARGET,
        CHOOSE_GUN,
        AIM;
    }

    String targetName = null;
    VirtualGun chosenGun = null;
    VirtualBulletManager virtualBulletManager = new VirtualBulletManager();
    BattleField battleField = null;
	AdvancedRobot robot = null;
    ArrayList<GunInfo> guns = new ArrayList<GunInfo>();
    State state = State.TARGET;

	public void setBattleField(BattleField battleField) { 
        this.battleField = battleField;
        for (GunInfo gun : guns) {
            gun.gun.setBattleField(battleField);
        }
        virtualBulletManager.setBattleField(battleField);
    }
    
    public void onPaint(Graphics2D graph) {
        virtualBulletManager.onPaint(graph);
    }

    public void init(AdvancedRobot robot) {
        this.robot = robot;
        guns.add(new GunInfo(new IterativeCircularTarget(), Color.BLUE));
        guns.add(new GunInfo(new HeadOnGun(), Color.GREEN));
        guns.add(new GunInfo(new IterativeLinearTarget(), Color.RED));
        guns.add(new GunInfo(new SimpleLinearTarget(), Color.PINK));
        for (GunInfo gun : guns) {
            gun.gun.init(robot);
        }
        virtualBulletManager.init(robot);
    }

	public void run() {
		if (state == State.TARGET) {
            targetName = findTarget(new RobotInfo(robot));
            if(targetName != null) {
                state = State.CHOOSE_GUN;
            }
            robot.out.println("Chose target: " + targetName);
        } else if (state == State.CHOOSE_GUN) {
            chosenGun = chooseGun();
            if (chosenGun != null) {
                String gunName = chosenGun.getClass().getSimpleName();
                robot.out.println("Chosen gun: " + gunName);
                state = State.AIM;
            } else {
                state = State.TARGET;
            }
        }else if (state == State.AIM) {
            RobotInfo target = battleField.get(targetName);
            if (target == null) {
                state = State.TARGET;
            }
            VirtualBullet bullet = aimGun(target);
            if (bullet != null) {
                robot.setFire(bullet.getFirepower());
                state = State.TARGET;
                robot.out.println("fired");

                RobotInfo iRobotInfo = new RobotInfo(robot);
                int id = 0;
                for (GunInfo gun : guns) {
                    Iterator<RobotInfo> it = battleField.values();
                    while(it.hasNext()) {
                        RobotInfo nxt = (RobotInfo) it.next();
                        if(nxt.isEnemy()) {
                            double power = getBulletPower(nxt.getPosition().distance(iRobotInfo.getPosition()));
                            bullet = gun.gun.getBullet(iRobotInfo, nxt, power);
                            virtualBulletManager.addBullet(bullet, this, nxt.getName(), id, gun.bulletCollor);
                        }
                    }
                    id++;
                }
            }
        }
        // virtual
        virtualBulletManager.run();
    }
    
    public void onVirtualBulletHit(VirtualBulletHitEvent event) {
        GunInfo gun = guns.get(event.getId());
        gun.addError(event.getTargetName(), Math.abs(event.getRelativeAngle()));
    }

    String findTarget(RobotInfo robot) {
        String targetName = null;
        double targetDistance = 1e9;
        Iterator<RobotInfo> it = battleField.values();
        while(it.hasNext()) {
            RobotInfo nxt = (RobotInfo) it.next();
            if(nxt.isEnemy()) {
                if(robot.getPosition().distance(nxt.getPosition()) < targetDistance) {
                    targetDistance = robot.getPosition().distance(nxt.getPosition());
                    targetName = nxt.getName();
                }
            }
        }
        return targetName;
    }

    VirtualGun chooseGun() {
        double minError = 1e9;
        VirtualGun chosenGun = null;
        for (GunInfo gun : guns) {
            if (gun.getMeanError(targetName) < minError) {
                chosenGun = gun.gun;
                minError = gun.getMeanError(targetName);
            }
        }
        return chosenGun;
    }

    double getBulletPower(double distance) {
        if(distance < 200) {
			return 3;
		} else if(distance < 600) {
			return 2.5;
		} else if(robot.getEnergy() > 20) {
			return 1.5;
		} else {
			return 1;
		}
    }

    VirtualBullet aimGun(RobotInfo target) {
        RobotInfo iRobotInfo = new RobotInfo(robot);
        if (target == null) return null;
        double power = getBulletPower(target.getPosition().distance(iRobotInfo.getPosition()));
        VirtualBullet bullet = chosenGun.getBullet(iRobotInfo, target, power);
        boolean isAimed = chosenGun.aimGun(robot, bullet, 0.01);
        if (isAimed && robot.getGunHeat() == 0) {
            return bullet;
        } else {
            return null;
        }
    }
}
