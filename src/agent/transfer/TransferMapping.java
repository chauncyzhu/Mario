/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.transfer;

import agent.state.StateAction;

/**
 *
 * @author timbrys
 */
public class TransferMapping {
    
    public StateAction[] newToOld(StateAction sa){
        return new StateAction[]{sa.clone()};
    }
    
    public StateAction[] oldToNew(StateAction sa){
        return new StateAction[]{sa.clone()};
    }
    
}
