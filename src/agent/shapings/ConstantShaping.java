/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.shapings;

import agent.state.StateAction;

import java.io.Serializable;

/**
 *
 * @author timbrys
 */
public class ConstantShaping extends PotentialBasedShaping implements Serializable{

    protected double constant;

    public ConstantShaping(){
    }
    
    public ConstantShaping(double scaling, double gamma, double constant){
        super(scaling, gamma);
        this.constant = constant;
    }
    
    @Override
    protected double actualPotential(StateAction sa) {
        return constant;
    }
    
}
