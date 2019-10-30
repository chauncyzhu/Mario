/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package problem.cartpole;

import agent.state.StateAction;
import agent.shapings.PotentialBasedShaping;

/**
 *
 * @author timbrys
 */
public class CartPoleHeuristicShaping extends PotentialBasedShaping {

    protected int id;
    
    public CartPoleHeuristicShaping(int id, double scaling, double gamma){
        super(scaling, gamma);
        this.id = id;
    }
    
    @Override
    protected double actualPotential(StateAction sa) {
        switch(id){
            default:
            case -1: return 0.0;
            case 0: return (-Math.abs(sa.getState()[2])/CartPole.DEFAULTRIGHTANGLEBOUND);
            case 1: return (-Math.abs(sa.getState()[3])/CartPole.DEFAULTRIGHTSPEEDBOUND);
            case 2: if((sa.getAction() == 0 && sa.getState()[2] <= 0) || (sa.getAction() == 1 && sa.getState()[2] >= 0)){
                return 1.0;
            } else {
                return 0.0;
            }
//            case 2: return 1.0-(Math.abs(sa.getState()[0])/CartPole.DEFAULTRIGHTCARTBOUND);
//            case 3: return 1.0-(Math.abs(sa.getState()[1])/CartPole.DEFAULTRIGHTSPEEDBOUND);
        }
    }
    
}
