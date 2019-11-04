package problem.mario.teaching;

import agent.agents.Policy;
import agent.shapings.PotentialBasedShaping;
import agent.shapings.Shaping;
import agent.state.StateAction;
import problem.mario.TeachingMario;

import java.util.ArrayList;
import java.util.List;


/**
 * Superclass for all student learners.
 */
public class Student extends TeachingAgent {

	protected TeachingAgent teacher; // Gives advice
	protected TeachingStrategy strategy; // Determines when advice is given
	
	private boolean testMode; // When set, will not explore or learn or take advice
	protected int adviceCount; // During the last episode

	protected List<Long> advisedStateList;   // advised states

	protected int currEpisode = 0;

	public Student(Policy policy, TeachingAgent teacher, TeachingStrategy strategy, double alpha, double lambda, PotentialBasedShaping initialization, Shaping shape, double gamma) {
		super(policy, alpha, lambda, initialization, shape, gamma);
		this.teacher = teacher;
		this.strategy = strategy;

//		this.advisedStateList = new ArrayList<>();
	}


	public int getAdviceCount()
	{
		return adviceCount;
	}

	public void newEpisode(){
		prevSA = prob.getState(this);
		prevSA.setAction(chooseAction(prevSA));
		this.advisedStateList = new ArrayList<>();
		currEpisode++;
	}


	/** Prepare for the first move. */
//	public void startEpisode() {
//		adviceCount = 0;
//
//		if (strategy.inUse(this, prevSA)) {
//			strategy.startEpisode();
//		}
//	}
	
	/** Choose a action, possibly with advice. */
	public int getAction() {
		int choice = prevSA.getAction();  // current action

		if (currEpisode > TeachingMario.beginEpisodes && !advisedStateList.contains(prevSA.singleKey()) && strategy.inUse(this, prevSA)) {
			int advice = teacher.chooseBestAction(prevSA);   // teacher's best action

			if (strategy.giveAdvice(teacher, prevSA, choice, advice)) {
				prevSA.setAction(advice);
				adviceCount++;

				// add advised states
				advisedStateList.add(prevSA.singleKey());
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

	public int getCumReuseTimes(){return 0;}
}
