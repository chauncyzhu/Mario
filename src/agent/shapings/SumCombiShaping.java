/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.shapings;

import agent.state.StateAction;

/**
 *
 * @author timbrys
 */
public class SumCombiShaping extends PotentialBasedShaping {
    
    protected PotentialBasedShaping[] shapings;
    
    public SumCombiShaping(PotentialBasedShaping[] shapings, double gamma){
        this(shapings, gamma, false);
    }
    
    public SumCombiShaping(PotentialBasedShaping[] shapings, double gamma, boolean memoize){
        super(1.0, gamma, memoize);
        this.shapings = shapings;
    }
    
    @Override
    protected double actualPotential(StateAction sa){
        double shape = 0.0;
        for (PotentialBasedShaping shaping : shapings) {
            shape += shaping.potential(sa);
        }
        if(shapings.length == 0){
            return 0.0;
        } else {
            return shape/shapings.length;
        }
    }
}
