package problem.mario.teaching;

import agent.agents.Policy;
import agent.shapings.PotentialBasedShaping;
import agent.shapings.Shaping;
import agent.state.StateAction;


/**
 * Superclass for all student learners.
 */
public class Student extends TeachingAgent {

	private TeachingAgent teacher; // Gives advice
	private TeachingStrategy strategy; // Determines when advice is given
	
	private boolean testMode; // When set, will not explore or learn or take advice
	private int adviceCount; // During the last episode


	public Student(Policy policy, TeachingAgent teacher, TeachingStrategy strategy, double alpha, double lambda, PotentialBasedShaping initialization, Shaping shape, double gamma) {
		super(policy, alpha, lambda, initialization, shape, gamma);
		this.teacher = teacher;
		this.strategy = strategy;
	}


	public int getAdviceCount()
	{
		return adviceCount;
	}


	/** Prepare for the first move. */
	public void startEpisode() {
		adviceCount = 0;

		if (strategy.inUse(this, prevSA)) {
			strategy.startEpisode();
		}
	}
	
	/** Choose a action, possibly with advice. */
	public int getAction() {
		int choice = prevSA.getAction();  // current action
		
		if (strategy.inUse(this, prevSA)) {
			int advice = teacher.chooseBestAction(prevSA);   // teacher's best action
			if (strategy.giveAdvice(teacher, prevSA, choice, advice)) {
				prevSA.setAction(advice);
				adviceCount++;
				return advice;
			}
		}

		return choice;
	}
	
//	/** Save the current policy to a file. */
//	public void savePolicy(String filename) {
//		savePolicy(filename);
//	}

	/** Report amount of advice given in the last episode,
	 *  along with any other data the strategy wants to record. */
	public double[] episodeData() {
		
		double[] extraData = strategy.episodeData();
		
		double[] data = new double[extraData.length+1];
		data[0] = adviceCount;
		
		for (int d=0; d<extraData.length; d++)
			data[d+1] = extraData[d];
		
		return data;
	}
}
