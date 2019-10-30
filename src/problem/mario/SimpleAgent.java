/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package problem.mario;

import agent.agents.EGreedyPolicy;
import agent.agents.EnsembleAgent;
import agent.agents.Policy;
import agent.agents.QLambdaAgent;
import agent.state.StateAction;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import problem.Problem;
import problem.mario.ch.idsia.benchmark.mario.environments.Environment;
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
    
    protected int chooseAction(StateAction sa){
        double rand = RNG.randomDouble();
        if(rand < 1.0/3.0){
            return 2;
        } else if(rand < 2.0/3.0){
            return 5;
        } else {
            return 11;
        }
    }
    
}
