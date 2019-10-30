/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.agents;

import agent.state.StateAction;
import agent.demonstration.Demonstration;
import java.util.ArrayList;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import problem.Problem;
import util.*;

/**
 *
 * @author timbrys
 */
abstract public class EnsembleAgent{
    
    protected Policy policy;

    protected StateAction prevSA;

    protected QLambdaAgent[] agents;
//    protected QLambdaAgent observer;
    
//    protected int stepCounter;
//    protected double correlation;
//    public ArrayList<Double> correlations;
    
    protected Problem prob;
    
    protected Demonstration record;
    protected boolean recording;
    
    public EnsembleAgent(Problem prob, QLambdaAgent[] agents, Policy policy){
        this(prob, agents, policy, false);
    }
    
    public EnsembleAgent(Problem prob, QLambdaAgent[] agents, Policy policy, boolean recording){
        this.policy = policy;
        
        this.prob = prob;
        this.agents = agents;
     
        for(QLambdaAgent agent : agents){
            agent.setProblem(prob);
        }
        
        this.recording = recording;
        this.record = new Demonstration(-1);
        
//        this.correlation = 0.0;
//        this.stepCounter = 0;
//        this.correlations = new ArrayList<Double>();
    }
    
    public void update(StateAction sa, double reward) {
        update(sa, reward, false);
    }
    
    public void update(StateAction sa, double reward, boolean passive){
        update(sa, reward, passive, true);
    }

    public void update(StateAction sa, double reward, boolean passive, boolean learning) {
        if(!passive){
            sa.setAction(chooseAction(sa));
        }
        
//        stepCounter++;
//        correlation += getCorrelation(sa);
        
        if(learning){
            for (QLambdaAgent agent : agents) {
                agent.update(prevSA, reward, sa);
            }
        }
        
        if(recording){
            record.record(sa, reward);
        }
        
        prevSA = sa;
    }
    
    public int getAction(){
        return prevSA.getAction();
    }
    
    public int getAction(Policy policy, StateAction sa){
        return policy.chooseAction(sa, getPreferences(sa));
    }
    
    protected abstract double[] getPreferences(StateAction sa);
    
    public QLambdaAgent getAgent(int n){
        return agents[n];
    }
    
    public Demonstration getTrajectory(){
        return record;
    }
    
    protected int chooseAction(StateAction sa){
        return policy.chooseAction(sa, getPreferences(sa));
    }
    
    public void newEpisode(){
        prevSA = prob.getState(this);
        prevSA.setAction(chooseAction(prevSA));
    }
    
    public void setProblem(Problem prob){
        this.prob = prob;
    }
    
    public void setPolicy(Policy policy){
        this.policy = policy;
    }
    
    public void endEpisode() {
        for (QLambdaAgent agent : agents) {
            agent.endEpisode(prevSA);
        }
        policy.endEpisode();
//        correlations.add(correlation/stepCounter);
//        correlation = 0.0;
//        stepCounter = 0;
    }
    
    public void reset(){
        for(QLambdaAgent agent : agents){
            agent.reset();
        }
    }

    public boolean isRecording() {
        return recording;
    }

    public void setRecording(boolean recording) {
        this.recording = recording;
    }
    
    public double getCorrelation(StateAction sa){
        PearsonsCorrelation pearson = new PearsonsCorrelation();
        double r = 0.0;
        
        for(int i=0; i<agents.length; i++){
            for(int j=i+1; j<agents.length; j++){
                r += pearson.correlation(agents[i].getQs(sa), agents[j].getQs(sa));
            }
        }
        
        return 2*r/(agents.length*(agents.length-1));
    }
}
