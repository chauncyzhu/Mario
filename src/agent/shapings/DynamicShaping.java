/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.shapings;

import agent.state.StateAction;
import agent.agents.QLambdaAgent;
import agent.agents.SarsaAgent;
import problem.Problem;

/**
 *
 * @author timbrys
 */
public class DynamicShaping extends PotentialBasedShaping{

    protected QLambdaAgent agent;
    protected PotentialBasedShaping shaping;
    
    public DynamicShaping(double scaling, double gamma, PotentialBasedShaping shaping, double alpha, double lambda){
        super(scaling, gamma);
        this.agent = new SarsaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), gamma);
        this.shaping = shaping;
    }
    
    @Override
    public void setProblem(Problem problem){
        super.setProblem(problem);
        agent.setProblem(problem);
    }
    
    @Override
    public double shape(StateAction sa1, StateAction sa2, double reward){
        if(dummyState(sa2)){
            return dummyShape(sa1, reward);
        }
        agent.update(sa1, -reward(sa1, reward, sa2), sa2);
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
    
    @Override
    protected double actualPotential(StateAction sa) {
        return agent.getQ(sa);
    }
    
    protected double reward(StateAction sa1, double reward, StateAction sa2){
        return shaping.potential(sa1);
    }
}
