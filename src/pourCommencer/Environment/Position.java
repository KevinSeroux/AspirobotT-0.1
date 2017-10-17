package pourCommencer.Environment;

public class Position {
	public int x, y;

	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Position(Position posToCopy) {
		this.x = posToCopy.x;
		this.y = posToCopy.y;
	}
}
