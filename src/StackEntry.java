import java.util.HashSet;
import java.util.Set;

public class StackEntry {
	private final String action;
	private final String state;
	private final Set<String> actions;
	
	public StackEntry( final String action, final String state, final Set<String> actions) {
		this.action = action;
		this.state = state;
		this.actions = actions;
	}
	
	public Object clone() {
		Set<String> actions = new HashSet<String>();
		actions.addAll(this.actions);
		return new StackEntry(action, state, actions);
	}

	public String getAction() {
		return action;
	}

	public String getState() {
		return state;
	}

	public Set<String> getActions() {
		return actions;
	}
	
}
