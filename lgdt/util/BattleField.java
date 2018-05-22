package lgdt.util;

public class BattleField {
	double battleFieldWidth, battleFieldHeight;

	public BattleField(double battleFieldWidth, double battleFieldHeight) {
		this.battleFieldHeight = battleFieldHeight;
		this.battleFieldWidth = battleFieldWidth;
	}

	public boolean contains(PT position, double border) {
		return border < position.x && position.x < battleFieldWidth - border &&
			   border < position.y && position.y < battleFieldHeight - border;
	}
}