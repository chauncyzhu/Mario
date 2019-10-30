/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.agents;

import agent.state.StateAction;

/**
 *
 * @author timbrys
 */
public abstract class Policy {
    
    public abstract int chooseAction(StateAction sa, double[] preferences);
    
    public void endEpisode(){}
    
    public abstract double probability(StateAction sa, double[] preferences);
    public abstract double[] probabilities(double[] preferences);
}
