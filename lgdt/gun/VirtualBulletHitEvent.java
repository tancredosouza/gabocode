package lgdt.gun;

import lgdt.util.PT;

public class VirtualBulletHitEvent {
    boolean is_hit;
    int id;
    double angle;

    public VirtualBulletHitEvent(boolean is_hit, int id, int angle) {
        this.is_hit = is_hit;
        this.id = id;
        this.angle = angle;
    }

    public boolean isHit() {
        return is_hit;
    }

    public double getRelativeAngle() {
        return angle;
    }

    public int getId() {
        return id;
    }
}