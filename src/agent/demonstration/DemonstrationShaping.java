/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.demonstration;

import agent.state.StateAction;
import agent.shapings.PotentialBasedShaping;

/**
 *
 * @author timbrys
 */
public class DemonstrationShaping extends PotentialBasedShaping{
    
    protected Demonstration demonstration;
    protected double sigma;
    
    public DemonstrationShaping(double scaling, double gamma, Demonstration demonstration, double sigma){
        super(scaling, gamma, true);
        this.demonstration = demonstration;
        this.sigma = sigma;
    }
    
    @Override
    protected double actualPotential(StateAction sa){
        return Math.pow(Math.E, -Math.pow(demonstration.getDistanceClosest(sa), 2.0)/(2*sigma*sigma));
    }
    
}
