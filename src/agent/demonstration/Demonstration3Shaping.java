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
public class Demonstration3Shaping extends PotentialBasedShaping{
    
    protected Demonstration demonstration;
    protected double sigma;
    
    public Demonstration3Shaping(double scaling, double gamma, Demonstration demonstration, double sigma){
        super(scaling, gamma, true);
        this.demonstration = demonstration;
        this.sigma = sigma;
    }
    
    @Override
    protected double actualPotential(StateAction sa){
//        double potential = 0.0, tmp;
//        double sum = 0.0;
//        
//        double[] normalizedsa = sa.normalize();
//        
//        DemonstratedSample closest;
//        
//        StateAction sa2 = sa.clone();
//        for(int i=0; i<sa.getNumActions(); i++){
//            sa2.setAction(i);
//            
//            closest = demonstration.getClosest(sa2);
//            double distance = distance(normalizedsa, closest.getState());
//                    
//            tmp = Math.pow(Math.E, -Math.pow(Math.sqrt(distance), 2.0)/(2*sigma*sigma))*closest.getCounter();
//            sum += tmp;
//            if(i==sa.getAction()){
//                potential = tmp;
//            }
//        }
//        
//        return potential/sum;
        return 0.0;
    }
    
    protected double distance(double[] state1, double[] state2){
        double distance = 0.0;
        for(int i=0; i<state1.length; i++){
            distance += Math.pow(state1[i]-state2[i], 2.0); 
        }
        return distance;
    }
    
}
