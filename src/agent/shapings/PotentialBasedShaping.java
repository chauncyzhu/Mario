/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.shapings;

import agent.state.StateAction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import problem.Problem;

/**
 *
 * @author timbrys
 */
public abstract class PotentialBasedShaping extends Shaping implements Serializable {

    private static final long serialVersionUID = 6529685098267757690L;


    protected double prevPotential;
    protected double initialPotential;
    
    protected boolean memoize;
    protected ArrayList<HashMap<Long, Double>> memoization;

    public PotentialBasedShaping(){
    }
    
    public PotentialBasedShaping(double scaling, double gamma){
        super(scaling, gamma);
        this.prevPotential = Double.MAX_VALUE;
        this.memoize = false;
        this.memoization = null;
    }
    
    public PotentialBasedShaping(double scaling, double gamma, boolean memoize){
        super(scaling, gamma);
        this.prevPotential = Double.MAX_VALUE;
        this.memoize = memoize;
        this.memoization = new ArrayList<HashMap<Long, Double>>();
    }
    
    protected void checkArrays(int action){
        while(memoization.size() <= action){
            memoization.add(new HashMap<Long, Double>());
        }
    }
    
    public void setProblem(Problem problem){
        this.problem = problem;
    }
    
    public void reset(){
        prevPotential = Double.MAX_VALUE;
    }
    
    @Override
    public double shape(StateAction sa1, StateAction sa2, double reward){
        if(dummyState(sa2)){
            return dummyShape(sa1, reward);
        }
        double phi;
        if(prevPotential == Double.MAX_VALUE){
            phi = potential(sa1);
            initialPotential = phi;
        } else {
            phi = prevPotential;
        }
        double nextPhi = potential(sa2);
        prevPotential = nextPhi;
        
        return reward + gamma * nextPhi - phi;
    }
    
    //move to dummy state with initial potential
    public double dummyShape(StateAction sa, double reward){
        double phi;
        if(prevPotential == Double.MAX_VALUE){
            phi = potential(sa);
            initialPotential = phi;
        } else {
            phi = prevPotential;
        }
        double nextPhi = initialPotential;
        
        return reward + gamma * nextPhi - phi;
    }
    
    protected boolean dummyState(StateAction sa){
        return sa.getAction() == -1;
    }
    
    public double potential(StateAction sa){
//        if(memoize && sa.key().length == 1){
//            return scaling*memoizedPotential(sa);
//        } else {
//            return scaling*actualPotential(sa);
//        }
        if(memoize){
            return scaling*memoizedPotential(sa);
        } else {
            return scaling*actualPotential(sa);
        }
    }
    
    protected double memoizedPotential(StateAction sa){
//        if(memoization.containsKey(sa.key()[0])){
//            return memoization.get(sa.key()[0]);
//        } else {
//            double potential = actualPotential(sa);
//            memoization.put(sa.key()[0], potential);
//            return potential;
//        }
        checkArrays(sa.getAction());
        double potential = 0.0;
        double actualPotential = 0.0;
        boolean calculated = false;
        
        for(int i=0; i<sa.key().length; i++){
            if(memoization.get(sa.getAction()).containsKey(sa.key()[i])){
                potential += memoization.get(sa.getAction()).get(sa.key()[i]);
            } else {
                if(!calculated){
                    actualPotential = actualPotential(sa);
                    calculated = true;
                }
                double pot = actualPotential / sa.key().length;
                memoization.get(sa.getAction()).put(sa.key()[i], pot);
                potential += pot;
            }
        }
        return potential;
    }
    
    protected abstract double actualPotential(StateAction sa);
    
    public void endEpisode(){
        this.prevPotential = Double.MAX_VALUE;
    }
}
