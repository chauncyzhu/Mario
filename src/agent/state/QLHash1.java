/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.state;

import agent.shapings.PotentialBasedShaping;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author timbrys
 */
public class QLHash1 {
    protected double gamma;
    protected double lambda;
    
    protected PotentialBasedShaping init;
    
    protected HashMap<Long, Double> weights;
    protected HashMap<Long, Double> es;
    
    public QLHash1(double alpha, double gamma, double lambda, PotentialBasedShaping init){
        this.weights = new HashMap<Long, Double>();
        this.es = new HashMap<Long, Double>();
        
        this.gamma = gamma;
        this.lambda = lambda;
        
        this.init = init;
    }
    
    public void reset(){
        resetEs();
        resetWeights();
    }
    
    public void resetWeights(){
        this.weights.clear();
    }
    
    public void resetEs(){
        this.es.clear();
    }
    
    public double getValue(StateAction features){
        double value = 0.0;
        for(long k : features.key()){
            if(!weights.containsKey(k)){
                weights.put(k, 0.0);
            }
            value += weights.get(k);
        }
        return value + init.potential(features);
    }
    
    public double[] getValues(StateAction features, int nrActions){
        StateAction sa = features.clone();
        double[] values = new double[nrActions];
        for(int i=0; i<nrActions; i++){
            sa.setAction(i);
            values[i] = getValue(sa);
        }
        return values;
    }
    
    public double[] getTileValues(StateAction features){
        double[] values = new double[features.key().length];
        double potential = init.potential(features)/values.length;
        for(int i=0; i<values.length; i++){
            if(!weights.containsKey(features.key()[i])){
                weights.put(features.key()[i], 0.0);
            }
            values[i] = weights.get(features.key()[i]) + potential;
        }
        return values;
    }
    
    public void setValue(StateAction features, double value){
        for(long k : features.key()){
            weights.put(k, value);
        }
    }
    
    public void update(double alpha_delta){
        Long f;
        for(Iterator<Long> it = es.keySet().iterator(); it.hasNext(); ){
            f = it.next();
            weights.put(f, weights.get(f) + (alpha_delta * es.get(f)));
        }
    }
    
    public void decay(){
        Map.Entry<Long, Double> f;
        double e;
        for(Iterator<Map.Entry<Long, Double>> it = es.entrySet().iterator(); it.hasNext(); ){
            f = it.next();
            e = f.getValue()*gamma*lambda;
            if(e < 0.001){
                it.remove();
            } else {
                es.put(f.getKey(), e);
            }
        }
    }
    
    public void setTraces(StateAction features){
        for(long k : features.key()){
            es.put(k, 1.0);
        }
    }
    
    public int getSize(){
        return weights.size();
    }
}