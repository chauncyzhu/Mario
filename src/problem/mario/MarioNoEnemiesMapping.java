/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package problem.mario;

import agent.state.StateAction;
import agent.transfer.TransferMapping;

/**
 *
 * @author timbrys
 */
public class MarioNoEnemiesMapping extends TransferMapping {
    
    @Override
    public StateAction[] newToOld(StateAction sa){
        StateAction sa1 = new MarioStateAction(sa.getState().clone(), sa.getAction());
        for(int i=5; i<21; i++){
            sa1.getState()[i] = 0;
        }
        sa1.getState()[25] = 21;
        sa1.getState()[26] = 21;
        
        return new StateAction[]{sa1};
    }

    @Override
    public StateAction[] oldToNew(StateAction sa) {
        return new StateAction[]{sa.clone()};
    }
    
    
}
