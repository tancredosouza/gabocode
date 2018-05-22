package lgdt.movement.minimumrisk;

import robocode.AdvancedRobot;

import lgdt.util.SubSystem;
import lgdt.util.PT;

import java.util.Enumeration;

public interface MinimumRiskBase extends SubSystem {
	public double getRisk(AdvancedRobot robot, PT position);
}