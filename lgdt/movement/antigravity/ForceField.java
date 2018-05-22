package lgdt.movement.antigravity;

import lgdt.util.PT;
import lgdt.util.RobotInfo;

public interface ForceField {
	PT getForce(RobotInfo robot);
	boolean canDestroy();
}