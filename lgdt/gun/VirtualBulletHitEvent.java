package lgdt.gun;

import lgdt.util.PT;
import lgdt.gun.VirtualBullet;

public class VirtualBulletHitEvent {
    boolean isHit;
    String targetName;
    double angle;
    VirtualBullet bullet;
    int id;

    public VirtualBulletHitEvent(boolean isHit, int id, double angle, String targetName, VirtualBullet bullet) {
        this.isHit = isHit;
        this.id = id;
        this.angle = angle;
        this.targetName = targetName;
        this.bullet = bullet;
    }

    public boolean isHit() {
        return isHit;
    }

    public double getRelativeAngle() {
        return angle;
    }

    public int getId() {
        return id;
    }

    public VirtualBullet getBulllet() {
        return bullet;
    }

    public String getTargetName() {
        return targetName;
    }
}