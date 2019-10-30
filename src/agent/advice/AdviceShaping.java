/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.advice;

import agent.shapings.PotentialBasedShaping;
import agent.shapings.Shaping;
import agent.state.StateAction;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 * @author timbrys
 */
public class AdviceShaping extends PotentialBasedShaping implements KeyListener{
    
    protected boolean adviceGiven;
    
    public AdviceShaping(double scaling, double gamma){
        super(scaling, gamma);
        reset();
    }
    
//    private StateAction simplifyStateAction(StateAction sa){
//        int[] state = sa.getState().clone();
//        float[] extraState = sa.getExtraState().clone();
//        
//        state[3] = 0;
//        state[4] = 0;
//        state[5] = 0;
//        state[6] = 0;
//        state[7] = 0;
//        state[8] = 0;
//        state[9] = 0;
//        
//        return new StateAction(state, extraState, sa.getAction());
//    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("advice");
        adviceGiven = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
    }

    @Override
    protected double actualPotential(StateAction sa) {
        return adviceGiven ? 1.0f : 0.0f;
    }

    @Override
    public void reset() {
        adviceGiven = false;
    }
    
}
