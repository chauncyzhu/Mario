package problem.mario.teaching;

import agent.agents.Policy;
import agent.shapings.PotentialBasedShaping;
import agent.shapings.Shaping;
import agent.state.StateAction;
import problem.mario.TeachingMario;
import util.RNG;

import java.util.HashMap;
import java.util.Map;


public class StudentDecayQChange extends Student{
    private Map<Long, Advice> actionReusingProb = new HashMap<>();

    private int cumReuseTimes = 0;
    private double decay;   // fixed reusing times
    private double qThred;
    private double currDecay = 0;   // fixed reusing times
    private boolean isReused;

    public StudentDecayQChange(double decay, double qThred, Policy policy, TeachingAgent teacher, TeachingStrategy strategy, double alpha, double lambda, PotentialBasedShaping initialization, Shaping shape, double gamma) {
        super(policy, teacher, strategy, alpha, lambda, initialization, shape, gamma);
        this.decay = decay;
        this.qThred = qThred;
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

                    this.isReused = true;
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

                this.isReused = true;
                return advice;
            }
        }

        this.isReused = false;
        return choice;
    }


    public void update(StateAction previous, double reward, StateAction next) {
        StateAction nextState = next.clone();
        Long key = previous.singleKey();  // state key

        double currQ = getQ(previous);

        // update Q-value
        cmac.setTraces(previous);
        double delta = shape.shape(previous, next, reward);
        delta -= getQ(previous);
        double best = getV(nextState);
        delta = delta + gamma*best;
        cmac.update(alpha*delta);

        if (this.isReused){   // need to be evaluated when false
            // the change of Q-value
            double newQ = getQ(previous);
            double qChange = newQ - currQ;

            // set decay prob
            // double newDecay = Math.pow(this.decay, Math.exp(-qChange));
            // double newDecayProb = actionReusingProb.get(key).getRemainProb() * newDecay;


            //System.out.println("qChange:"+qChange+"-decay:"+this.decay+"-newDecay:"+newDecay);
            double newDecayProb;
            if (qChange > this.qThred){
                newDecayProb = 1.0;
            }else{
                newDecayProb = actionReusingProb.get(key).getRemainProb() * this.decay;
            }
            actionReusingProb.put(key, new Advice(next.getAction(), newDecayProb));
            this.isReused = false;
        }

        nextState.setAction(next.getAction());
        if(getQ(nextState) == best){
            cmac.decay();
        } else {
            cmac.resetEs();
        }
    }


    public int getCumReuseTimes(){
        return cumReuseTimes;
    }
}
