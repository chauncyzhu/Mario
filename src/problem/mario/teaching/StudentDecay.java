package problem.mario.teaching;

import agent.agents.Policy;
import agent.shapings.PotentialBasedShaping;
import agent.shapings.Shaping;
import agent.state.StateAction;

import java.util.*;
import util.RNG;


public class StudentDecay extends Student{
    private Map<Long, Advice> actionReusingProb = new HashMap<>();

    private int cumReuseTimes = 0;
    private double decay;   // fixed reusing times

    public StudentDecay(double decay, Policy policy, TeachingAgent teacher, TeachingStrategy strategy, double alpha, double lambda, PotentialBasedShaping initialization, Shaping shape, double gamma) {
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
                    actionReusingProb.put(key, new Advice(action, currProb * this.decay));
                    cumReuseTimes++;
                    return action;
                }
            }
        }

        if (!advisedStateList.contains(key) && strategy.inUse(this, prevSA)) {
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
