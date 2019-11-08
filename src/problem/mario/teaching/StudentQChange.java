package problem.mario.teaching;

import agent.agents.Policy;
import agent.shapings.PotentialBasedShaping;
import agent.shapings.Shaping;
import agent.state.StateAction;
import problem.mario.TeachingMario;

import java.util.HashMap;
import java.util.Map;

public class StudentQChange extends Student{
    private Map<Long, Advice> actionReusing = new HashMap<>();
    private boolean isAdvised;
    private int cumReuseTimes;
    private double qThred;

    public StudentQChange(double qThred, Policy policy, TeachingAgent teacher, TeachingStrategy strategy, double alpha, double lambda, PotentialBasedShaping initialization, Shaping shape, double gamma) {
        super(policy, teacher, strategy, alpha, lambda, initialization, shape, gamma);
        this.qThred = qThred;
    }


    /** Choose a action, possibly with advice. */
    public int getAction() {
        int choice = prevSA.getAction();  // current action
        Long key = prevSA.singleKey();  // state key

        // reusing advised actions
        if (strategy.askBudgetInUse()){
            if (actionReusing.containsKey(key) && actionReusing.get(key).isqReuse()){
                Advice preAdvice = actionReusing.get(key);
                cumReuseTimes += 1;
                actionReusing.put(key, new Advice(preAdvice.getAdvice(), false));
                this.isAdvised = true;
                return preAdvice.getAdvice();
            }
        }

        if (currEpisode > TeachingMario.beginEpisodes && !advisedStateList.contains(key) && strategy.inUse(this, prevSA)) {
            int advice = teacher.chooseBestAction(prevSA);   // teacher's best action

            if (strategy.giveAdvice(teacher, prevSA, choice, advice)) {
                prevSA.setAction(advice);
                adviceCount++;

                // add advised states
                advisedStateList.add(key);

                actionReusing.put(key, new Advice(advice, false));
                this.isAdvised = true;
                return advice;
            }
        }

        this.isAdvised = false;
        return choice;
    }


    public void update(StateAction previous, double reward, StateAction next) {
        StateAction state = next.clone();
        Long key = previous.singleKey();  // state key

        double currQ = getQ(previous);

        cmac.setTraces(previous);

        double delta = shape.shape(previous, next, reward);
        delta -= getQ(previous);

        double[] Qs = new double[prob.getNumActions()];
        double best = -Double.MAX_VALUE;
        for(int i=0; i<prob.getNumActions(); i++){
            state.setAction(i);
            Qs[i] = getQ(state);
            if(Qs[i] > best){
                best = Qs[i];
            }
        }
        delta = delta + gamma*best;

        cmac.update(alpha*delta);

        double newQ = getQ(previous);
        double qChange = newQ - currQ;

        if (this.isAdvised && !actionReusing.get(key).isqReuse()){   // need to be evaluated when false
            if (qChange > this.qThred){
//                System.out.println("Reuse currQ:"+currQ+"-newQ:"+newQ+"-qChange:"+qChange);
//                System.out.println("cumReuseTimes:"+cumReuseTimes);
                actionReusing.put(key, new Advice(previous.getAction(), true));
            }
        }

        if(Qs[next.getAction()] == best){
            cmac.decay();
        } else {
            cmac.resetEs();
        }
    }

    public int getCumReuseTimes(){
        return cumReuseTimes;
    }


}
