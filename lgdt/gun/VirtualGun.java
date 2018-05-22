package lgdt.gun;

import lgdt.util.RobotInfo;
import lgdt.gun.VirtualBullet;
import lgdt.util.SubSystem;

public interface VirtualGun extends SubSystem {
	public VirtualBullet getBullet(RobotInfo robot, RobotInfo target, double power);
	public VirtualBullet getBullet(RobotInfo robot);
}