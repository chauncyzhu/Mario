package problem.mario.teaching;

import agent.state.StateAction;
import problem.mario.TeachingMario;
import problem.mario.teaching.*;


/**
 * Gives a fixed amount of front-loaded advice.
 */
public class AdviseAtFirst extends TeachingStrategy {
	
	private int left; // Advice to give
	
	public AdviseAtFirst() {
		left = TeachingMario.BUDGET;
	}

	/** When there's some left. */
	public boolean giveAdvice(TeachingAgent teacher, StateAction _stateAction, int _choice, int _advice) {
		left--;
		return true;
	}
	
	/** Until none left. */
	public boolean inUse(TeachingAgent student, StateAction stateAction) {
		return (left > 0);
	}

	// the asking budget still can be used
	public boolean askBudgetInUse(){
		return (left > 0);
	}


}
