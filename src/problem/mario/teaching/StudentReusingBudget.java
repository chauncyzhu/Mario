package problem.mario.teaching;

import agent.agents.Policy;
import agent.shapings.PotentialBasedShaping;
import agent.shapings.Shaping;
import agent.state.StateAction;
import problem.mario.TeachingMario;

import java.util.HashMap;
import java.util.Map;

public class StudentReusingBudget extends Student{
//    private Map<Long, Integer> actionReusing = new HashMap<>();
    private Map<Long, Advice> actionReusingBudget = new HashMap<>();

    private int cumReuseTimes = 0;
    private int reusingBudget;   // fixed reusing times

    public StudentReusingBudget(int reusingBudget, Policy policy, TeachingAgent teacher, TeachingStrategy strategy, double alpha, double lambda, PotentialBasedShaping initialization, Shaping shape, double gamma) {
        super(policy, teacher, strategy, alpha, lambda, initialization, shape, gamma);
        this.reusingBudget = reusingBudget;
    }


    /** Choose a action, possibly with advice. */
    public int getAction() {
        int choice = prevSA.getAction();  // current action
        Long key = prevSA.singleKey();  // state key

        // reusing advised actions
        if (strategy.askBudgetInUse()){
            if (actionReusingBudget.containsKey(key)){
                // reuse action and cut 1
                Advice preAdvice = actionReusingBudget.get(key);
                int action = preAdvice.getAdvice();
                int currLength = preAdvice.getRemainBudget();
                if (currLength > 0){
                    actionReusingBudget.put(key, new Advice(action, currLength-1));
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

                actionReusingBudget.put(key, new Advice(advice, reusingBudget));
                return advice;
            }
        }

        return choice;
    }

    public int getCumReuseTimes(){
        return cumReuseTimes;
    }

}
