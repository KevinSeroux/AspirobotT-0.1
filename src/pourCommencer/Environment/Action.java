package pourCommencer.Environment;

public class Action {
	public ActionType type;
	public Object data;

	public Action(ActionType type) {
		this.type = type;
	}

	public Action(ActionType type, Object data) {
		this(type);
		this.data = data;
	}
}
