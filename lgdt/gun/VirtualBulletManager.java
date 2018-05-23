package lgdt.gun;

import robocode.AdvancedRobot;
import robocode.util.Utils;

import lgdt.gun.VirtualBullet;
import lgdt.gun.VirtualGun;
import lgdt.gun.VirtualBulletHitEvent;
import lgdt.util.PT;
import lgdt.util.RobotInfo;
import lgdt.util.SubSystem;

import java.util.ArrayList;
import java.util.Hashtable;
import java.awt.*;

public class VirtualBulletManager implements SubSystem {
    class Info {
        public VirtualBullet bullet;
        public VirtualGun gun;
        public String targetName;
        public int id;

        public Info(VirtualBullet bullet, VirtualGun gun, String targetName, int id) {
            this.bullet = bullet;
            this.gun = gun;
            this.targetName = targetName;
            this.id = id;
        }
    }

    static double EPS = 1e-6;
    ArrayList<Info> bullets = new ArrayList<Info>();
    Hashtable<String, RobotInfo> targets = new Hashtable<String, RobotInfo>();
    AdvancedRobot robot;

	public void onPaint(Graphics2D graph) {
        for (Info info : bullets) {
            PT bullet_position = info.bullet.getPosition(rotbot.getTime());
            graph.setColor(Color.BLUE);
            graph.fillRect((int)bullet_position.x, (int)bullet_position.y, 10, 10);
        }
    }
    
    public void addBullet(VirtualBullet bullet, VirtualGun gun, String targetName, int id) {
        bullets.add(new Info(bullet, gun, targetName, id));
    }

    public void addRobotInfo(RobotInfo robot) {
        targets.put(robot.getName(), robot);
    }

	public void onRobotDeath(String robotName) {
        targets.remove(robotName);
    }

	public void init(AdvancedRobot robot) {
        this.robot = robot;
    }

	public void run(AdvancedRobot robot) {
        for (int i = 0; i < bullets.size(); i++) {
            Info info = bullets.get(i);
            RobotInfo target = targets.get(info.targetName);

            boolean needToRemove = false;
            if (target == null) {
                needToRemove = true;
            } else {
                PT bullet_position = info.bullet.getPosition(rotbot.getTime());
                PT bullet_velocity_rot = info.bullet.velocity.rotateDegree(90);
                PT target_vector = target.getPosition().subtract(bullet_position);
                double cross_product = bullet_velocity_rot.cross(target_vector);
                double distance = target.distace(bullet_position);
                double angle = info.bullet.velocity.angle(target.getPosition().subtract(bullet.origin));
                if (distance < EPS) { 
                    // hit target
                    info.gun.sendHit(new VirtualBulletHitEvent(true, info.id, angle));
                    needToRemove = true;
                } else if (bullet_position.x < 0 || bullet_position.y < 0
                            || bullet_position.x >= robot.getBattleFieldWidth()
                            || bullet_position.y >= robot.getBattleFieldHeight()
                            || cross_product > EPS) {
                    // missed
                    info.gun.sendHit(new VirtualBulletHitEvent(false, info.id, angle));
                    needToRemove = true;
                }
            }

            if (needToRemove) {
                // remove info
                bullets.set(i, bullets.get(bullets.size() - 1));
                bullets.remove(bullets.size() - 1);
                i--;
            }
        }
    }

}