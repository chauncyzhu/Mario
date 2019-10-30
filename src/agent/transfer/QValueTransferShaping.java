/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.transfer;

import agent.state.QLHash;
import agent.state.StateAction;
import agent.shapings.PotentialBasedShaping;
import util.Util;

/**
 *
 * @author timbrys
 */
public class QValueTransferShaping extends PotentialBasedShaping{

    protected QLHash source;
    protected TransferMapping mapping;
    
    public QValueTransferShaping(double scaling, double gamma, QLHash source){
        this(scaling, gamma, source, new TransferMapping());
    }
    
    public QValueTransferShaping(double scaling, double gamma, QLHash source, TransferMapping mapping){
        super(scaling, gamma);
        this.source = source;
        this.mapping = mapping;
    }
    
    @Override
    protected double actualPotential(StateAction sa) {
        StateAction[] mapped = mapping.newToOld(sa.clone());
        double Q = 0;
        for (StateAction mapped1 : mapped) {
            Q += source.getValue(mapped1) / mapped.length;
        }
        return Q;
    }
}
