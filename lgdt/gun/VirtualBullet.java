package lgdt.gun;

import lgdt.util.PT;

public class VirtualBullet {
	public PT origin, velocity;
	private long startTime;

	public VirtualBullet(PT origin, PT velocity, long currentTime) {
		this.origin = origin;
		this.velocity = velocity;
		this.startTime = currentTime;
	}

	public PT getPosition(long currentTime) {
		return origin.add(velocity.scale(currentTime - startTime));
	}

	public double getFirepower() {
		return (20 - velocity.length()) / 3;
	}

	public double getFiringangle() {
		return velocity.angle();
	}

	public long getTime() {
		return startTime;
	}
}