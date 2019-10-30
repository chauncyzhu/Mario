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
public class Demonstration2Shaping extends PotentialBasedShaping{
    
    protected Demonstration demonstration;
    protected double sigma;
    
    public Demonstration2Shaping(double scaling, double gamma, Demonstration demonstration, double sigma){
        super(scaling, gamma, true);
        this.demonstration = demonstration;
        this.sigma = sigma;
    }
    
    @Override
    protected double actualPotential(StateAction sa){
        return 0.0;
//        double potential = 0.0, tmp;
//        double sum = 0.0;
        
//        StateAction sa2 = sa.clone();
//        for(int i=0; i<sa.getNumActions(); i++){
//            sa2.setAction(i);
//            tmp = Math.pow(Math.E, -Math.pow(demonstration.getDistanceClosest(sa2), 2.0)/(2*sigma*sigma));
//            sum += tmp;
//            if(i==sa.getAction()){
//                potential = tmp;
//            }
//        }
        
//        return potential/sum;
    }
    
}
