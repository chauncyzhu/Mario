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
public abstract class SingleAgentProblem extends Problem{
    
//    public abstract double[] step(int action);
    public double[] step(int[] action){
        return step(action[0]);
    }
    
    public abstract StateAction getState();
    public StateAction getState(EnsembleAgent agent){
        return getState();
    }
    public StateAction getState(TeachingAgent agent){
        return getState();
    }

    public void setAgent(EnsembleAgent agent){
        setAgents(new EnsembleAgent[]{agent});
    }
}
