/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.state;

import java.io.Serializable;

/**
 *
 * @author timbrys
 */
public abstract class StateAction implements Serializable{
    protected double[] state;
    protected int action;
    protected long[] key;
    
    public StateAction(double[] state, int action){
        this(state, action, null);
    }
    
    public StateAction(double[] state, int action, long[] key){
        this.state = state;
        this.action = action;
        this.key = key;
    }

    public double[] getState(){
        return state;
    }

    public int getAction() {
        return action;
    }
    
    public void setAction(int action){
        this.action = action;
        key = null;
    }
    
    public abstract StateAction unnormalize(double[] state, int action);
    public abstract double[] normalize();
    
    public abstract long[] calculateKey();
    public long[] key(){
        if(key == null){
            key = calculateKey();
        }
        return key;
    }
    
    public abstract StateAction clone();
    public abstract String dataformat();
    
    public abstract StateAction newStateAction(double[] state, int action);
}