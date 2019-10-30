/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.agents;

import agent.state.StateAction;
import java.util.ArrayList;
import util.Util;

/**
 *
 * @author timbrys
 */
public class GreedyPolicy extends Policy{

    @Override
    public int chooseAction(StateAction sa, double[] preferences) {
        return Util.argMax(preferences);
    }

    @Override
    public double[] probabilities(double[] preferences) {
        ArrayList<Integer> best = Util.argMaxes(preferences);
        double[] probabilities = new double[preferences.length];
        for(int i=0; i<probabilities.length; i++){
            if(best.contains(i)){
                probabilities[i] = 1.0/best.size();
            } else {
                probabilities[i] = 0.0;
            }
        }
        return probabilities;
    }

    @Override
    public double probability(StateAction sa, double[] preferences) {
        ArrayList<Integer> best = Util.argMaxes(preferences);
        if(best.contains(sa.getAction())){
            return 1.0/best.size();
        } else {
            return 0.0;
        }
    }

}