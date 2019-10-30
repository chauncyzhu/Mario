/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.demonstration;

/**
 *
 * @author timbrys
 */
public class DemonstratedSample {
    
    protected double [] state;
    protected int action;
    protected double reward;
    
    protected int occurrenceCounter;
    
    public DemonstratedSample(double[] state, int action, double reward){
        this.state = state.clone();
        this.action = action;
        this.reward = reward;
        this.occurrenceCounter = 1;
    }
    
    public void increaseCounter(){
        occurrenceCounter++;
    }
    
    public int getCounter(){
        return occurrenceCounter;
    }

    public double[] getState() {
        return state;
    }

    public int getAction() {
        return action;
    }
    
    public double getReward() {
        return reward;
    }
    
}
