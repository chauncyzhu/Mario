/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.agents;

import agent.state.StateAction;
import util.RNG;

/**
 *
 * @author timbrys
 */
public class SoftMaxPolicy extends Policy{
    
    protected double tau;
    protected double decay;
    protected double min;
    
    public SoftMaxPolicy(double tau){
        this(tau, 1.0, 0.0);
    }
    
    public SoftMaxPolicy(double tau, double decay, double min){
        this.tau = tau;
        this.decay = decay;
        this.min = min;
    }

    @Override
    public int chooseAction(StateAction sa, double[] preferences) {
        double[] probs = probabilities(preferences);
        double threshold = RNG.randomDouble();
        double total = 0.0;
        for(int i=0; i<preferences.length; i++){
            total += probs[i];
            if(total > threshold){
                return i;
            }
        }
        return preferences.length-1;
    }
    
    @Override
    public double[] probabilities(double[] preferences){
        double sum = 0.0;
        double[] values = new double[preferences.length];
        double highest = -Double.MAX_VALUE;
        for(int i=0; i<preferences.length; i++){
            values[i] = preferences[i]/tau;
            if(values[i] > highest){
                highest = values[i];
            }
        }
        
        for(int i=0; i<preferences.length; i++){
            values[i] = Math.exp(values[i]-highest);
            sum += values[i];
        }
        for(int i=0; i<preferences.length; i++){
            values[i] = values[i]/sum;
        }
        return values;
    }
    
    @Override
    public void endEpisode(){
        tau *= decay;
        if(tau < min){
            tau = min;
        }
    }

    @Override
    public double probability(StateAction sa, double[] preferences) {
        double sum = 0.0;
        double[] values = new double[preferences.length];
        double highest = -Double.MAX_VALUE;
        for(int i=0; i<preferences.length; i++){
            values[i] = preferences[i]/tau;
            if(values[i] > highest){
                highest = values[i];
            }
        }
        
        for(int i=0; i<preferences.length; i++){
            values[i] = Math.exp(values[i]-highest);
            sum += values[i];
        }
        return values[sa.getAction()]/sum;
    }
}
