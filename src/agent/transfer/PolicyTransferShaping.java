/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.transfer;

import agent.agents.Policy;
import agent.state.QLHash;
import agent.state.StateAction;
import agent.shapings.PotentialBasedShaping;
import problem.Problem;
import util.Util;

/**
 *
 * @author timbrys
 */
public class PolicyTransferShaping extends PotentialBasedShaping{

    protected QLHash source;
    protected TransferMapping mapping;
    protected Policy policy;
    protected Problem prob;
    
    public PolicyTransferShaping(double scaling, double gamma, QLHash source, Problem prob, Policy policy){
        this(scaling, gamma, source, new TransferMapping(), prob, policy);
    }
    
    public PolicyTransferShaping(double scaling, double gamma, QLHash source, TransferMapping mapping, Problem prob, Policy policy){
        super(scaling, gamma, true);
        this.source = source;
        this.mapping = mapping;
        this.prob = prob;
        this.policy = policy;
    }
    
    @Override
    protected double actualPotential(StateAction sa) {
        //System.out.println(policy.probability(sa, getQs(sa)));
        return policy.probability(sa, getQs(sa));
        //return (Util.amongstArgMax(sa.getAction(), getQs(sa)) ? 1.0 : 0);
    }
    
    protected double[] getQs(StateAction state){
        StateAction clone = state.clone();
        double[] Qs = new double[prob.getNumActions()];
        for(int i=0; i<Qs.length; i++){
            clone.setAction(i);
            StateAction[] mapped = mapping.newToOld(clone);
            for (StateAction mapped1 : mapped) {
                Qs[i] += source.getValue(mapped1) / mapped.length;
            }
        }
        return Qs;
    }
}
