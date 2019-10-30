/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package problem.mario;

import problem.cartpole.*;
import agent.state.StateAction;
import agent.shapings.PotentialBasedShaping;

/**
 *
 * @author timbrys
 */
public class MarioHeuristicShaping extends PotentialBasedShaping {

    protected int id;
    
    public MarioHeuristicShaping(int id, double scaling, double gamma){
        super(scaling, gamma);
        this.id = id;
    }
    
    @Override
    protected double actualPotential(StateAction sa) {
        switch(id){
            default:
            case -1: return 0.0;
            case 0: return sa.getState()[3];
            case 1: return 3-sa.getState()[4];
            case 2: if(sa.getAction() == 2 || sa.getAction() == 5 || sa.getAction() == 11){
                return 1.0;
            } else {
                return 0.0;
            }
        }
    }
    
}
