/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package problem.cartpole;

import agent.agents.EnsembleAgent;
import agent.agents.GreedyPolicy;
import agent.agents.Policy;
import agent.agents.QLambdaAgent;
import agent.state.StateAction;
import problem.Problem;
import rlpark.plugin.rltoys.algorithms.control.acting.Greedy;

/**
 *
 * @author timbrys
 */
public class IvomarEnsembleAgent extends EnsembleAgent{

    public boolean greedy = false;

    public IvomarEnsembleAgent(Problem prob, QLambdaAgent[] agents, Policy policy) {
        super(prob, agents, policy);
    }
    
    public int getAction(StateAction sa){
        return policy.chooseAction(sa, getPreferences(sa));
    }
    
    @Override
    protected double[] getPreferences(StateAction sa) {
        if(greedy){
            return agents[0].getQs(sa);
        } else {
            return agents[1].getQs(sa);
        }
    }
    
    public void setGreedy(boolean greedy){
        this.greedy = greedy;
    }
}
