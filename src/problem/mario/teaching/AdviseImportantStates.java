package problem.mario.teaching;

import agent.state.StateAction;
import problem.mario.TeachingMario;
import problem.mario.utils.Stats;


/**
 * Gives a fixed amount of advice in important states.
 */
public class AdviseImportantStates extends TeachingStrategy {
	
	public int left; // Advice to give
	public double maxvar;
	private double threshold; // Of action importance
	
	public AdviseImportantStates(double t) {
		left = TeachingMario.BUDGET;
		threshold = t;
	//	maxvar=0;
	}  

	/** When the state has widely varying Q-values. */
	public boolean giveAdvice(TeachingAgent teacher, StateAction stateAction, int _choice, int _advice) {
		
		
		boolean UseVariance = false;
		
		double[] qvalues = teacher.getQs(stateAction);
		double gap;
		
		//threshold *=2.5; //REMOVE just testing
			
		if (UseVariance == false) {
		    gap = Stats.max(qvalues) - Stats.min(qvalues);
		} else {
			//threshold = (int) 72500;//(99/100 * maxvar);
			gap = VarianceGap(qvalues);
			//gap = CVgap(qvalues);
			//threshold = (int) (80/100 * Experiments.varTHRESHOLD);

			//System.out.println(gap+"var");

			//threshold = VarianceThreshold(qvalues,threshold);
		}
		
		boolean important = gap > threshold;//> *1000
		//Experiments.writer.println(gap);

		if (important) {
			left--;
			//System.out.println("budget left: "+left);
			return true;
		}
		
		return false; 
	}
	 
	/** Until none left. */
	public boolean inUse(TeachingAgent student, StateAction stateAction) {
		return (left > 0);
	}

	
	public double VarianceGap(double[] qvalues)
	{
		//double currentvar = Stats.variance(qvalues, Stats.average(qvalues));
		double currentvar = Stats.absdeviation(qvalues, Stats.average(qvalues));

		//System.out.println(currentvar+"var1");
		//System.out.println(Experiments.varTHRESHOLD+"var2");
		//if (currentvar > Experiments.varTHRESHOLD) 
			//{
			
			//Experiments.varTHRESHOLD = currentvar;

			//}
		//System.out.println(currentvar+"var3");

		return currentvar;
}
	
	public double CVgap(double[] qvalues)
	{
		double currentvar = Math.sqrt(Stats.variance(qvalues, Stats.average(qvalues)))/Stats.average(qvalues);
		//double currentvar = Stats.absdeviation(qvalues, Stats.average(qvalues));

		//System.out.println(currentvar+"var1");
		//System.out.println(Experiments.varTHRESHOLD+"var2");
		//if (currentvar > Experiments.varTHRESHOLD) 
			//{
			
			//Experiments.varTHRESHOLD = currentvar;

			//}
		//System.out.println(currentvar+"var3");

		return currentvar;
}
	
	public int VarianceThreshold(double[] qvalues,int oldthreshold)
	{
	  double[] thresholdqvalues = new double[] {Stats.max(qvalues),(Stats.min(qvalues)+Stats.max(qvalues))/2,Stats.min(qvalues)};
	  return (int) Stats.variance(thresholdqvalues, Stats.average(thresholdqvalues));
		//return	  (int) Math.pow((Stats.average(qvalues)- (double) oldthreshold), 2);
	}
	
	}
	

