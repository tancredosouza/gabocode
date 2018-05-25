package lgdt.movement.minimumrisk;

import robocode.AdvancedRobot;

import lgdt.util.SubSystem;
import lgdt.util.PT;

import java.util.Enumeration;

public abstract class MinimumRiskBase extends SubSystem {
	public abstract double getRisk(PT position);
}