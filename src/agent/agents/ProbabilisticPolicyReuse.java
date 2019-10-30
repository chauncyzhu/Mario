/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.agents;

import agent.state.QLHash;
import agent.state.StateAction;
import agent.transfer.TransferMapping;
import util.RNG;

/**
 *
 * @author timbrys
 */
public class ProbabilisticPolicyReuse extends EGreedyPolicy {
    
    protected QLHash reuse;
    protected Policy reusePolicy;
    protected TransferMapping mapping;
    
    public ProbabilisticPolicyReuse(Policy policy, QLHash reuse, Policy reusePolicy, double psi) {
        this(policy, reuse, reusePolicy, psi, 1.0, 0.0, new TransferMapping());
    }
    
    public ProbabilisticPolicyReuse(Policy policy, QLHash reuse, Policy reusePolicy, double psi, TransferMapping mapping) {
        this(policy, reuse, reusePolicy, psi, 1.0, 0.0, mapping);
    }
    
    public ProbabilisticPolicyReuse(Policy policy, QLHash reuse, Policy reusePolicy, double psi, double psiDecay, double psiMin) {
        this(policy, reuse, reusePolicy, psi, psiDecay, psiMin, new TransferMapping());
    }
    
    public ProbabilisticPolicyReuse(Policy policy, QLHash reuse, Policy reusePolicy, double psi, double psiDecay, double psiMin, TransferMapping mapping) {
        super(policy, psi, psiDecay, psiMin);
        this.reuse = reuse;
        this.reusePolicy = reusePolicy;
        this.mapping = mapping;
    }
    
    @Override
    public int epsilonAction(StateAction sa, double[] preferences) {
        StateAction[] mapped = mapping.newToOld(sa);
        StateAction mapped1 = mapped[RNG.randomInt(mapped.length)];
        mapped1.setAction(reusePolicy.chooseAction(mapped1, reuse.getValues(mapped1)));
        mapped = mapping.oldToNew(mapped1);
        return mapped[RNG.randomInt(mapped.length)].getAction();
    }
}
