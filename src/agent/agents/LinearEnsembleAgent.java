/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.agents;

import agent.state.StateAction;
import java.util.Arrays;
import problem.Problem;
import util.Util;

/**
 *
 * @author timbrys
 */
public class LinearEnsembleAgent extends EnsembleAgent {

    public LinearEnsembleAgent(Problem prob, QLambdaAgent[] agents, Policy policy) {
        super(prob, agents, policy, false);
    }
    
    public LinearEnsembleAgent(Problem prob, QLambdaAgent[] agents, Policy policy, boolean record) {
        super(prob, agents, policy, record);
    }
    
    @Override
    protected double[] getPreferences(StateAction sa) {
        double[] Qs = new double[prob.getNumActions()];
        for (QLambdaAgent agent : agents) {
            for (int i = 0; i<Qs.length; i++) {
                sa.setAction(i);
                Qs[i] += agent.getQ(sa);
            }
        }
        return Qs;
    }
}
