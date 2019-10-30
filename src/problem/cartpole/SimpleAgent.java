/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package problem.cartpole;

import agent.agents.EGreedyPolicy;
import agent.agents.EnsembleAgent;
import agent.agents.QLambdaAgent;
import agent.state.StateAction;
import problem.Problem;
import util.RNG;

/**
 *
 * @author timbrys
 */
public class SimpleAgent extends EnsembleAgent {
    
    public SimpleAgent(Problem prob) {
        super(prob, new QLambdaAgent[]{}, new EGreedyPolicy(0.0));
    }

    @Override
    protected double[] getPreferences(StateAction sa) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    protected int chooseAction(StateAction sa){
        if(sa.getState()[3] < 0){
            return 0;
        } else {
            return 1;
        }
    }
    
}
