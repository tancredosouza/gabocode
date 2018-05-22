package lgdt.util;

import lgdt.util.RobotInfo;

public class EnergyDropEvent {
	private double drop, shotProbability;
	private RobotInfo info;

	public EnergyDropEvent(RobotInfo robot, double drop, double shotProbability) {
		this.info = robot;
		this.drop = drop;
		this.shotProbability = shotProbability;
	}

	public double getDrop() { return drop; }
	public double getShotProbability() { return shotProbability; }
	public RobotInfo getInfo() { return info; }

	public void setShotProbability(double probability) { shotProbability = probability; }
}