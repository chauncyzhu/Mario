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
public class DeterministicGreedyPolicy extends Policy{

    @Override
    public int chooseAction(StateAction sa, double[] preferences) {
        return Util.argMaxes(preferences).get(0);
    }

    @Override
    public double[] probabilities(double[] preferences) {
        ArrayList<Integer> best = Util.argMaxes(preferences);
        double[] probabilities = new double[preferences.length];
        probabilities[best.get(0)] = 1.0;
        return probabilities;
    }

    @Override
    public double probability(StateAction sa, double[] preferences) {
        ArrayList<Integer> best = Util.argMaxes(preferences);
        if(best.get(0) == sa.getAction()){
            return 1.0;
        } else {
            return 0.0;
        }
    }

}