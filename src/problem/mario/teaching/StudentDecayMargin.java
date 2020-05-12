package problem.mario.teaching;

import agent.agents.Policy;
import agent.shapings.PotentialBasedShaping;
import agent.shapings.Shaping;
import agent.state.StateAction;
import problem.mario.TeachingMario;
import util.RNG;

import java.util.HashMap;
import java.util.Map;


public class StudentDecayMargin extends Student{
    private Map<Long, Advice> actionReusingProb = new HashMap<>();

    private int cumReuseTimes = 0;
    private double decay;   // fixed reusing times
    private double currDecay = 0;   // fixed reusing times

    public StudentDecayMargin(double decay, Policy policy, TeachingAgent teacher, TeachingStrategy strategy, double alpha, double lambda, PotentialBasedShaping initialization, Shaping shape, double gamma) {
        super(policy, teacher, strategy, alpha, lambda, initialization, shape, gamma);
        this.decay = decay;
    }


    /** Choose a action, possibly with advice. */
    public int getAction() {
        int choice = prevSA.getAction();  // current action
        Long key = prevSA.singleKey();  // state key

        // reusing advised actions
        if (strategy.askBudgetInUse()){
            // no action is advised, then use the previously advised action and usual exploration strategy
            // if the advised action is reused, then the probability will decay.
            if (actionReusingProb.containsKey(key)){
                // reuse action and decay prob
                Advice preAdvice = actionReusingProb.get(key);
                int action = preAdvice.getAdvice();
                double currProb= preAdvice.getRemainProb();
                if (currProb > RNG.randomDouble()){
//                    if (currEpisode == 10000){
//                        this.currDecay = this.decay * 0.5;
//                    }
//
//                    if (currEpisode == 20000){
//                        this.currDecay = this.decay * 0.25;
//                    }
//
//                    if (currEpisode == 30000){
//                        this.currDecay = this.decay * 0.125;
//                    }

                    StateAction stateAction = prevSA.clone();
                    stateAction.setAction(action);  // the advised state-action

                    // decay^qDiff
                    // double marginValue = (getV(stateAction) - getQ(stateAction)) / getV(stateAction);
                    // double newDecayProb = currProb * Math.pow(this.decay, marginValue);

                    // use margin as decay value
                    // double newDecayValue = 1 - Math.exp(getQ(stateAction) - getV(stateAction));
                    // double newDecayProb = currProb * newDecayValue;

                    // compare margin and current decayvalue
                    double newDecayValue = Math.exp(getQ(stateAction) - getV(stateAction));
                    newDecayValue = Math.min(newDecayValue, this.decay);
                    double newDecayProb = currProb*newDecayValue;

                    //System.out.println("marginValue:"+marginValue+"-decay:"+this.decay+"-newDecay:"+newDecay);
                    actionReusingProb.put(key, new Advice(action, newDecayProb));
                    cumReuseTimes++;
                    return action;
                }
            }
        }

        if (currEpisode > TeachingMario.beginEpisodes && !advisedStateList.contains(key) && strategy.inUse(this, prevSA)) {
            int advice = teacher.chooseBestAction(prevSA);   // teacher's best action

            if (strategy.giveAdvice(teacher, prevSA, choice, advice)) {
                prevSA.setAction(advice);
                adviceCount++;

                // add advised states
                advisedStateList.add(key);

                actionReusingProb.put(key, new Advice(advice, 1.0));
                return advice;
            }
        }
        return choice;
    }

    public int getCumReuseTimes(){
        return cumReuseTimes;
    }
}
