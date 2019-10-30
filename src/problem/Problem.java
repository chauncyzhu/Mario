/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package problem;

import agent.agents.EnsembleAgent;
import agent.state.StateAction;
import problem.mario.teaching.TeachingAgent;

/**
 *
 * @author timbrys
 */
public abstract class Problem {
    
    protected EnsembleAgent[] agents;
    
    public void setAgents(EnsembleAgent[] agents){
        this.agents = agents;
    }
    
    public EnsembleAgent getAgent(int i){
        return agents[i];
    }
    
    public int getNrAgents(){
        return agents.length;
    }
    
    public abstract int getNumActions();
    public abstract StateAction getState(EnsembleAgent agent);

    public abstract StateAction getState(TeachingAgent agent);

    //double[2] {agent reward, observer metric} (usually the same)
    public double[] step(int action){
        return new double[]{0};
    }

    public abstract double[] step(int[] action);
    public abstract boolean isGoalReached(int iteration);
    
    public abstract void reset();
}
