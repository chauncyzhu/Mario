package problem.mario.teaching;

import agent.state.StateAction;
import problem.mario.TeachingMario;
import problem.mario.utils.Stats;

/**
 * Gives a fixed amount of advice in important states where the student makes a mistake.
 */
public class CorrectImportantMistakes extends TeachingStrategy {
	
	protected int left; // Advice to give
	protected double threshold; // Of mistake importance
		
	public CorrectImportantMistakes(double t) {
		left = TeachingMario.BUDGET;
		threshold = t;
	}

	/** When the state has widely varying Q-values, and the student doesn't take the advice action. */
	public boolean giveAdvice(TeachingAgent teacher, StateAction stateAction, int choice, int advice) {
		
		double[] qvalues = teacher.getQs(stateAction);
		double gap = Stats.max(qvalues) - Stats.min(qvalues);
		boolean important = (gap > threshold);

		if (important) {
			boolean mistake = (choice != advice);

			if (mistake) {
				left--;
				return true;
			}
		}
		
		return false;
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
