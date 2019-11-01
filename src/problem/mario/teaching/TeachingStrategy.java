package problem.mario.teaching;

import agent.state.StateAction;

/**
 * Determines whether advice is given.
 */
public abstract class TeachingStrategy {
	
	public abstract boolean giveAdvice(TeachingAgent teacher, StateAction stateAction, int choice, int advice);

	public abstract boolean inUse(TeachingAgent student, StateAction stateAction);

	public abstract boolean askBudgetInUse();

	public void startEpisode() {} // Override to do start-of-episode stuff
		
	public double[] episodeData() { // Override to add data to learning curves
		double[] data = new double[0];
		return data;
	}
}
