/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.shapings;

import agent.state.StateAction;
import java.util.HashMap;
import problem.Problem;

/**
 *
 * @author timbrys
 */
public abstract class Shaping{
    
    protected double gamma;
    
    protected double scaling;
    
    protected Problem problem;

    public Shaping(){
    }
    
    public Shaping(double scaling, double gamma){
        this.gamma = gamma;
        this.scaling = scaling;
    }
    
    public void setProblem(Problem problem){
        this.problem = problem;
    }
    
    public abstract void reset();
    
    public abstract double shape(StateAction sa1, StateAction sa2, double reward);
    
    public abstract void endEpisode();
}
