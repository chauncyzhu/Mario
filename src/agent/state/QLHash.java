/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.state;

import agent.shapings.PotentialBasedShaping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author timbrys
 */
public class QLHash implements Serializable {
    private static final long serialVersionUID = 6529685098267757690L;

    protected double gamma;
    protected double lambda;
    
    protected PotentialBasedShaping init;
    
    protected ArrayList<HashMap<Long, Double>> weights;
    protected HashMap<Long, Integer> visits;

    protected ArrayList<HashMap<Long, Double>> es;
    
    protected int nrActions;


    public QLHash(double alpha, double gamma, double lambda, PotentialBasedShaping init){
        this(alpha, gamma, lambda, init, -1);
    }
    

    public QLHash(double alpha, double gamma, double lambda, PotentialBasedShaping init, int nrActions){
        this.weights = new ArrayList<HashMap<Long, Double>>();
        this.es = new ArrayList<HashMap<Long, Double>>();
        this.visits = new HashMap<Long, Integer>();

        this.gamma = gamma;
        this.lambda = lambda;
        
        this.init = init;
        
        this.nrActions = nrActions;
    }

    public ArrayList<HashMap<Long, Double>> getWeights(){
        return weights;
    }
    
    public void reset(){
        resetEs();
        resetWeights();
    }
    
    public void resetWeights(){
        for(int i=0; i<es.size(); i++){
            this.weights.get(i).clear();
        }
    }
    
    public void resetEs(){
        for(int i=0; i<es.size(); i++){
            this.es.get(i).clear();
        }
    }
    
    protected void checkArrays(int action){
        while(weights.size() <= action){
            weights.add(new HashMap<Long, Double>());
            es.add(new HashMap<Long, Double>());
        }
    }

    public void setVisits(StateAction features){
        for(long k : features.key()) {
            if (!visits.containsKey(k)) {
                visits.put(k, 0);
            } else {
                visits.put(k, visits.get(k) + 1);
            }
        }
    }

    public int getVisits(StateAction features){
        int vi = 0;
        for(long k : features.key()) {
            vi += visits.get(k);
        }
        return vi;
    }

    public HashMap<Long, Integer> getVisitsMap(){
        return visits;
    }

    
    public double getValue(StateAction features){
        double value = 0.0;
        for(long k : features.key()){
            checkArrays(features.getAction());
            if(!weights.get(features.getAction()).containsKey(k)){
                weights.get(features.getAction()).put(k, 0.0);
            }
            value += weights.get(features.getAction()).get(k);
        }
        return value + init.potential(features);
    }
    
    public double[] getValues(StateAction features, int nrActions){
        StateAction sa = features.clone();
        double[] values = new double[nrActions];
        checkArrays(nrActions-1);
        for(int i=0; i<nrActions; i++){
            sa.setAction(i);
            values[i] = getValue(sa);
        }
        return values;
    }
    
    public double[] getValues(StateAction features){
        //Dirty tricks here
        if(nrActions < weights.size()){
            return getValues(features, weights.size());
        } else {
            return getValues(features, nrActions);
        }
    }
    
    public double[] getTileValues(StateAction features){
        double[] values = new double[features.key().length];
        double potential = init.potential(features)/values.length;
        for(int i=0; i<values.length; i++){
            checkArrays(features.getAction());
            if(!weights.get(features.getAction()).containsKey(features.key()[i])){
                weights.get(features.getAction()).put(features.key()[i], 0.0);
            }
            values[i] = weights.get(features.getAction()).get(features.key()[i]) + potential;
        }
        return values;
    }
    
    public void setValue(StateAction features, double value){
        for(long k : features.key()){
            checkArrays(features.getAction());
            weights.get(features.getAction()).put(k, value);
        }
    }
    
    public void update(double alpha_delta){
        Long f;
        for(int i=0; i<es.size(); i++){
            for(Iterator<Long> it = es.get(i).keySet().iterator(); it.hasNext(); ){
                f = it.next();
                weights.get(i).put(f, weights.get(i).get(f) + (alpha_delta * es.get(i).get(f)));
            }
        }
    }
    
    public void decay(){
        Map.Entry<Long, Double> f;
        double e;
        for(int i=0; i<es.size(); i++){
            for(Iterator<Map.Entry<Long, Double>> it = es.get(i).entrySet().iterator(); it.hasNext(); ){
                f = it.next();
                e = f.getValue()*gamma*lambda;
                if(e < 0.001){
                    it.remove();
                } else {
                    es.get(i).put(f.getKey(), e);
                }
            }
        }
    }
    
    public void setTraces(StateAction features){
        checkArrays(features.getAction());
        for(long k : features.key()){
            es.get(features.getAction()).put(k, 1.0);
        }
    }
    
    public void setNrActions(int nrActions) {
        this.nrActions = nrActions;
    }

}