/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package problem.mario;

import agent.agents.*;
import agent.state.StateAction;
import java.util.ArrayList;
import util.RNG;
import util.Util;

/**
 *
 * @author timbrys
 */
public class MarioSimplePolicy extends Policy{

    protected double epsilon;
    
    public MarioSimplePolicy(double epsilon){
        this.epsilon = epsilon;
    }
    
    @Override
    public int chooseAction(StateAction sa, double[] preferences) {
        if(RNG.randomDouble() < epsilon){
            return RNG.randomInt(preferences.length);
        } else {
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
    
    @Override
    public void endEpisode(){
    }

    //incorrect
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

    //incorrect
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
