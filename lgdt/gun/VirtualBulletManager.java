package lgdt.gun;

import robocode.AdvancedRobot;
import robocode.util.Utils;

import lgdt.gun.VirtualBullet;
import lgdt.gun.VirtualBulletHitEvent;
import lgdt.gun.multigun.Multigun;
import lgdt.util.PT;
import lgdt.util.RobotInfo;
import lgdt.util.SubSystem;
import lgdt.util.BattleField;


import java.util.ArrayList;
import java.util.HashMap;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class VirtualBulletManager extends SubSystem {
    class Info {
        public VirtualBullet bullet;
        public Multigun gun;
        public String targetName;
        public int id;
        public Color color;
        public double weight;

        public Info(VirtualBullet bullet, Multigun gun, String targetName, int id, Color color, double weight) {
            this.bullet = bullet;
            this.gun = gun;
            this.targetName = targetName;
            this.id = id;
            this.color = color;
            this.weight = weight;
        }
    }

    static double EPS = 1e-6;
    ArrayList<Info> bullets = new ArrayList<Info>();
	BattleField battleField = null;
    AdvancedRobot robot;
    long curTime = 0;

	public void onPaint(Graphics2D graph) {
        curTime = robot.getTime();
        for (Info info : bullets) {
            PT bullet_position = info.bullet.getPosition(curTime);
            if(info.weight > 0.3) {
                graph.setColor(info.color);
                Ellipse2D.Double circle = new Ellipse2D.Double((int)bullet_position.x, (int)bullet_position.y, 10, 10);
                graph.fill(circle);
            }
        }
    }

    public void addBullet(VirtualBullet bullet, Multigun gun, String targetName, int id, Color color, double weight) {
        bullets.add(new Info(bullet, gun, targetName, id, color, weight));
    }
    
    public void addBullet(VirtualBullet bullet, Multigun gun, String targetName, int id, Color color) {
        addBullet(bullet, gun, targetName, id, color, 1.0);
    }

    public void setBattleField(BattleField battleField) {
		this.battleField = battleField;
	}

	public void init(AdvancedRobot robot) {
        this.robot = robot;
    }

    public double getDanger(PT position) {
        double ans = 0;
        for (Info info : bullets) {
            ans += info.weight / Math.pow(info.bullet.getPosition(curTime).subtract(position).length(), 2) * (1 + Math.cos(info.bullet.velocity.angle(position.subtract(info.bullet.getPosition(curTime)))));
        }
        return ans;
    }

	public void run() {
        curTime = robot.getTime();
        RobotInfo myRobot = new RobotInfo(robot);
        for (int i = 0; i < bullets.size(); i++) {
            Info info = bullets.get(i);
            RobotInfo target = (info.targetName == null ? myRobot : battleField.get(info.targetName));

            boolean needToRemove = false;
            if (target == null) {
                needToRemove = true;
            } else {
                PT bullet_position = info.bullet.getPosition(robot.getTime());
                PT bullet_velocity_rot = info.bullet.velocity.rotateDegree(90);
                PT target_vector = target.getPosition().subtract(bullet_position);
                double cross_product = bullet_velocity_rot.cross(target_vector);
                double distance = target.getPosition().distance(bullet_position);
                double angle = info.bullet.velocity.angle(target.getPosition().subtract(info.bullet.origin));
                if (distance < EPS) { 
                    // hit target
                    if(info.gun != null) {
                        info.gun.onVirtualBulletHit(new VirtualBulletHitEvent(true, info.id, angle, info.targetName, info.bullet));
                    }
                    needToRemove = true;
                } else if (bullet_position.x < 0 || bullet_position.y < 0
                            || bullet_position.x >= robot.getBattleFieldWidth()
                            || bullet_position.y >= robot.getBattleFieldHeight()
                            || cross_product > EPS) {
                    // missed
                    if(info.gun != null) {
                        info.gun.onVirtualBulletHit(new VirtualBulletHitEvent(false, info.id, angle, info.targetName, info.bullet));
                    }
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