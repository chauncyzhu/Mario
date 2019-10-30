/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.agents;

import agent.state.StateAction;
import java.util.ArrayList;
import util.RNG;
import util.Util;

/**
 *
 * @author timbrys
 */
public class EGreedyPolicy extends Policy{

    protected Policy greedy;
    protected double epsilon;
    protected double decay;
    protected double min;
    
    public EGreedyPolicy(double epsilon){
        this(epsilon, 1.0, 0.0);
    }
    
    public EGreedyPolicy(double epsilon, double decay, double min){
        this(new GreedyPolicy(), epsilon, decay, min);
    }
    
    public EGreedyPolicy(Policy policy, double epsilon, double decay, double min){
        this.epsilon = epsilon;
        this.decay = decay;
        this.min = min;
        this.greedy = policy;
    }
    
    @Override
    public int chooseAction(StateAction sa, double[] preferences) {
        if(RNG.randomDouble() < epsilon){
            return epsilonAction(sa, preferences);
        } else {
            return greedy.chooseAction(sa, preferences);
        }
    }
    
    protected int epsilonAction(StateAction sa, double[] preferences) {
        return RNG.randomInt(preferences.length);
    }
    
    @Override
    public void endEpisode(){
        epsilon *= decay;
        if(epsilon < min){
            epsilon = min;
        }
        greedy.endEpisode();
    }

    @Override
    public double[] probabilities(double[] preferences) {
        ArrayList<Integer> best = Util.argMaxes(preferences);
        double[] probabilities = new double[preferences.length];
        for(int i=0; i<probabilities.length; i++){
            if(best.contains(i)){
                probabilities[i] = (1.0-epsilon)/best.size();
            } else {
                probabilities[i] = (epsilon)/(preferences.length-best.size());
            }
        }
        return probabilities;
    }

    @Override
    public double probability(StateAction sa, double[] preferences) {
        ArrayList<Integer> best = Util.argMaxes(preferences);
        if(best.contains(sa.getAction())){
            return (1.0-epsilon)/best.size();
        } else {
            return (epsilon)/(preferences.length-best.size());
        }
    }
    
}
